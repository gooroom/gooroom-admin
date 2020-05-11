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

package kr.gooroom.gpms.job.service;

import java.util.HashMap;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

/**
 * Gooroom job management service interface
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface JobService {

	/**
	 * create new job by job data bean.
	 * 
	 * @param jobVO JobVO job configuration data bean.
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO createJob(JobVO jobVO) throws Exception;

	/**
	 * cancel gooroom job.
	 * 
	 * @param jobVO JobVO job configuration data bean.
	 * @return StatusVO result status
	 * @throws Exception
	 */
	StatusVO setJobToCancel(JobVO jobVO) throws Exception;

	/**
	 * response job list data.
	 * 
	 * @param job_status String job status option.
	 * @return ResultVO result object
	 * @throws Exception
	 */
	ResultVO getJobList(String job_status) throws Exception;

	/**
	 * response job list data for paging.
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	ResultPagingVO getJobListPaged(HashMap<String, Object> options) throws Exception;

	/**
	 * response job information by job id.
	 * 
	 * @param jobId String job id.
	 * @return ResultVO result object.
	 * @throws Exception
	 */
	ResultVO getJobInfo(String jobId) throws Exception;

	/**
	 * response client list data that was targeted job.
	 * 
	 * @param jobNo String job id(number).
	 * @return ResultVO result object.
	 * @throws Exception
	 */
	ResultVO readClientListInJob(String jobNo) throws Exception;

	/**
	 * response client list data that was targeted job for paging
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultVO result object.
	 * @throws Exception
	 */
	ResultPagingVO getClientListInJobPaged(HashMap<String, Object> options) throws Exception;

}
