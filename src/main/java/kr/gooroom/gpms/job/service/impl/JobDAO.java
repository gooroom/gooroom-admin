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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.service.ClientInJobVO;
import kr.gooroom.gpms.job.service.JobResultVO;
import kr.gooroom.gpms.job.service.JobVO;

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
	 * @throws SQLException
	 */
	public List<JobVO> selectJobList(String job_status) throws SQLException {

		List<JobVO> re = null;
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("jobStatus", job_status);
			re = sqlSessionMeta.selectList("selectJobList", map);

		} catch (Exception ex) {
			logger.error("error in selectJobList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response job list data for paging.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return JobVO List job list
	 * @throws SQLException
	 */
	public List<JobVO> selectJobListPaged(HashMap<String, Object> options) throws SQLException {

		List<JobVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectJobListPaged", options);

		} catch (Exception ex) {
			logger.error("error in selectJobListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response filtered count for job list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectJobListFilteredCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectJobListFilteredCount", options);
	}

	/**
	 * response total count for job list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectJobListTotalCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectJobListTotalCount", options);
	}

	/**
	 * response job information by job id.
	 * 
	 * @param jobNo String job id.
	 * @return JobVO result object.
	 * @throws SQLException
	 */
	public JobVO selectJobInfo(String jobNo) throws SQLException {

		JobVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectJobInfo", jobNo);
		} catch (Exception ex) {
			logger.error("error in selectJobInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * modify job by job data bean.
	 * 
	 * @param jobVO JobVO job configuration data bean.
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long updateJobClientToCancel(JobVO vo) throws SQLException {
		return (long) sqlSessionMeta.update("updateJobClientToCancel", vo);
	}

	/**
	 * response client list data that was targeted job.
	 * 
	 * @param jobNo String job id(number).
	 * @return ClientInJobVO list selected client list data.
	 * @throws SQLException
	 */
	public List<ClientInJobVO> selectClientListInJob(String jobNo) throws SQLException {

		List<ClientInJobVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectClientListInJob", jobNo);
		} catch (Exception ex) {
			logger.error("error in selectClientListInJob : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response client list data that was targeted job.
	 * 
	 * @param jobNo String job id(number).
	 * @return ClientInJobVO list selected client list data.
	 * @throws SQLException
	 */
	public List<ClientInJobVO> selectClientListInJobPaged(HashMap<String, Object> options) throws SQLException {

		List<ClientInJobVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectClientListInJobPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectClientListInJobPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response filtered count for job list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectClientListInJobFilteredCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectClientListInJobFilteredCount", options);
	}

	/**
	 * response total count for job list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectClientListInJobTotalCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectClientListInJobTotalCount", options);
	}

	/**
	 * create job master by job data bean.
	 * 
	 * @param jobVO JobVO job configuration data bean.
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createJobMaster(JobVO jobVO) throws SQLException {

		return (long) sqlSessionMeta.insert("insertJobMaster", jobVO);

	}

	/**
	 * create job target by job data bean.
	 * 
	 * @param jobVO JobVO job configuration data bean.
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createJobTarget(JobVO jobVO) throws SQLException {

		return (long) sqlSessionMeta.insert("insertJobTarget", jobVO);

	}

}
