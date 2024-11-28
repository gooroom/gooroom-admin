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

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.service.ClientInJobVO;
import kr.gooroom.gpms.job.service.JobVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * data access object class for gooroom job management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("jobDAO")
public class JobDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(JobDAO.class);

	/**
	 * response job list data
	 * 
	 * @param job_status String job status option.
	 * @return JobVO List job list
	 */
	public List<JobVO> selectJobList(String job_status) {

		List<JobVO> re = null;
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put("jobStatus", job_status);
			re = sqlSessionMeta.selectList("selectJobList", map);

		} catch (Exception ex) {
			logger.error("error in selectJobList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response job list data for paging.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return JobVO List job list
	 */
	public List<JobVO> selectJobListPaged(HashMap<String, Object> options) {

		List<JobVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectJobListPaged", options);

		} catch (Exception ex) {
			logger.error("error in selectJobListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response filtered count for job list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectJobListFilteredCount(HashMap<String, Object> options) {

		return sqlSessionMeta.selectOne("selectJobListFilteredCount", options);
	}

	/**
	 * response total count for job list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectJobListTotalCount(HashMap<String, Object> options) {

		return sqlSessionMeta.selectOne("selectJobListTotalCount", options);
	}

	/**
	 * response job information by job id.
	 * 
	 * @param jobNo String job id.
	 * @return JobVO result object.
	 */
	public JobVO selectJobInfo(String jobNo) {

		JobVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectJobInfo", jobNo);
		} catch (Exception ex) {
			logger.error("error in selectJobInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * modify job by job data bean.
	 * 
	 * @param vo jobVO JobVO job configuration data bean.
	 * @return long data insert result count.
	 */
	public long updateJobClientToCancel(JobVO vo) {
		return sqlSessionMeta.update("updateJobClientToCancel", vo);
	}

	/**
	 * response client list data that was targeted job.
	 * 
	 * @param jobNo String job id(number).
	 * @return ClientInJobVO list selected client list data.
	 */
	public List<ClientInJobVO> selectClientListInJob(String jobNo) {

		List<ClientInJobVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectClientListInJob", jobNo);
		} catch (Exception ex) {
			logger.error("error in selectClientListInJob : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response client list data that was targeted job.
	 * 
	 * @param options HashMap<Sring, Object>
	 * @return ClientInJobVO list selected client list data.
	 */
	public List<ClientInJobVO> selectClientListInJobPaged(HashMap<String, Object> options) {

		List<ClientInJobVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectClientListInJobPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectClientListInJobPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response filtered count for job list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectClientListInJobFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectClientListInJobFilteredCount", options);
	}

	/**
	 * response total count for job list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectClientListInJobTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectClientListInJobTotalCount", options);
	}

	/**
	 * create job master by job data bean.
	 * 
	 * @param jobVO JobVO job configuration data bean.
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createJobMaster(JobVO jobVO) throws SQLException {
		return sqlSessionMeta.insert("insertJobMaster", jobVO);
	}

	/**
	 * create job target by job data bean.
	 * 
	 * @param jobVO JobVO job configuration data bean.
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createJobTarget(JobVO jobVO) throws SQLException {
		return sqlSessionMeta.insert("insertJobTarget", jobVO);
	}

}
