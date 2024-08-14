/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.gooroom.gpms.job.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.service.ClientInJobVO;
import kr.gooroom.gpms.job.service.JobService;
import kr.gooroom.gpms.job.service.JobVO;

/**
 * Gooroom job management service implemts class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("jobService")
public class JobServiceImpl implements JobService {

	private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

	@Resource(name = "jobDAO")
	private JobDAO jobDAO;

	/**
	 * create new job by job data bean.
	 * 
	 * @param jobVO JobVO job configuration data bean.
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createJob(JobVO jobVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			long reCnt1 = jobDAO.createJobMaster(jobVO);

			if (reCnt1 > 0) {
				long reCnt2 = 0;
				String[] clientArray = jobVO.getClientIds();
				for (int i = 0; i < clientArray.length; i++) {
					jobVO.setClientId(clientArray[i]);
					reCnt2 = jobDAO.createJobTarget(jobVO);
				}

				if (reCnt2 > 0) {
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
							MessageSourceHelper.getMessage("job.result.insert"));
				} else {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
							MessageSourceHelper.getMessage("job.result.noinsert"));
				}
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("job.result.noinsert"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in createJob (by bean) : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createJob (by bean) : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * cancel gooroom job.
	 * 
	 * @param jobVO JobVO job configuration data bean.
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO setJobToCancel(JobVO jobVO) throws Exception {
		StatusVO statusVO = new StatusVO();
		try {
			long reCnt = jobDAO.updateJobClientToCancel(jobVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("job.result.update"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("job.result.noupdate"));
			}
		} catch (Exception ex) {
			logger.error("error in setJobToCancel : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}
		return statusVO;
	}

	/**
	 * response job list data.
	 * 
	 * @param job_status String job status option.
	 * @return ResultVO result object
	 * @throws Exception
	 */
	@Override
	public ResultVO getJobList(String job_status) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			List<JobVO> re = jobDAO.selectJobList(job_status);

			if (re != null && re.size() > 0) {
				JobVO[] row = re.stream().toArray(JobVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getJobList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response job list data for paging.
	 * 
	 * @param options HashMap<String, Object>.
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getJobListPaged(HashMap<String, Object> options) throws Exception {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<JobVO> re = jobDAO.selectJobListPaged(options);
			long totalCount = jobDAO.selectJobListTotalCount(options);
			long filteredCount = jobDAO.selectJobListFilteredCount(options);

			if (re != null && re.size() > 0) {
				JobVO[] row = re.stream().toArray(JobVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));

				resultVO.setRecordsTotal(String.valueOf(totalCount));
				resultVO.setRecordsFiltered(String.valueOf(filteredCount));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getJobListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response job information by job id.
	 * 
	 * @param jobId String job id.
	 * @return ResultVO result object.
	 * @throws Exception
	 */
	@Override
	public ResultVO getJobInfo(String jobNo) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			JobVO re = jobDAO.selectJobInfo(jobNo);

			if (re != null) {
				JobVO[] row = new JobVO[1];
				row[0] = re;
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getJobInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response client list data that was targeted job.
	 * 
	 * @param jobNo String job id(number).
	 * @return ResultVO result object.
	 * @throws Exception
	 */
	@Override
	public ResultVO readClientListInJob(String jobNo) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			List<ClientInJobVO> re = jobDAO.selectClientListInJob(jobNo);

			if (re != null && re.size() > 0) {
				ClientInJobVO[] row = re.stream().toArray(ClientInJobVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in readClientListInJob : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response client list data that was targeted job.
	 * 
	 * @param options HashMap<String, Object> option data.
	 * @return ResultVO result object.
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getClientListInJobPaged(HashMap<String, Object> options) throws Exception {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<ClientInJobVO> re = jobDAO.selectClientListInJobPaged(options);
			long totalCount = jobDAO.selectClientListInJobTotalCount(options);
			long filteredCount = jobDAO.selectClientListInJobFilteredCount(options);

			if (re != null && re.size() > 0) {
				ClientInJobVO[] row = re.stream().toArray(ClientInJobVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));

				resultVO.setRecordsTotal(String.valueOf(totalCount));
				resultVO.setRecordsFiltered(String.valueOf(filteredCount));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in readClientListInJobPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

}
