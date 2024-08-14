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

package kr.gooroom.sample.schedule.service.impl;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.sample.schedule.ProgrammableSchedulerRunner;
import kr.gooroom.sample.schedule.service.MigSampleService;

/**
 * 
 * 
 * @author HNC
 */

@Service("migSampleService")
public class MigSampleServiceImpl implements MigSampleService {

	private static final Logger logger = LoggerFactory.getLogger(MigSampleServiceImpl.class);

	@Resource(name = "migSampleDAO")
	private MigSampleDAO migSampleDao;
	
	@Autowired
    public ProgrammableSchedulerRunner schedulerRunner;

	/**
	 * 
	 * 
	 * @param
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO createMigProcess(String isForce) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			
			// D1. delete dept_mstr_temp
			long resultDept1 = migSampleDao.deleteDeptTempData();
			System.out.println("####################################################\n");
			System.out.println("resultDept1 : " + String.valueOf(resultDept1));

			// U1. delete user_mstr_temp
			long resultUser1 = migSampleDao.deleteUserTempData();
			System.out.println("####################################################\n");
			System.out.println("resultUser1 : " + String.valueOf(resultUser1));

			// D2. copy dept_mstr_view => delete dept_mstr_temp
			long resultDept2 = migSampleDao.copyViewToDeptTemp();
			System.out.println("####################################################\n");
			System.out.println("resultDept2 : " + String.valueOf(resultDept2));

			// U2. copy dept_mstr_view => delete dept_mstr_temp
			long resultUser2 = migSampleDao.copyViewToUserTemp();
			System.out.println("####################################################\n");
			System.out.println("resultUser2 : " + String.valueOf(resultUser2));

			// D3. get count not exist dept_mstr_temp compare dept_mstr (for DELETE)
			long resultDept3 = migSampleDao.getCountForDeptDelete();
			System.out.println("####################################################\n");
			System.out.println("resultDept3 : " + String.valueOf(resultDept3));

			// U3. get count not exist dept_mstr_temp compare dept_mstr (for DELETE)
			long resultUser3 = migSampleDao.getCountForUserDelete();
			System.out.println("####################################################\n");
			System.out.println("resultUser3 : " + String.valueOf(resultUser3));

			// D4. get count diff data dept_mstr compare dept_mstr_temp (for UPDATE)
			long resultDept4 = migSampleDao.getCountForDeptUpdate();
			System.out.println("####################################################\n");
			System.out.println("resultDept4 : " + String.valueOf(resultDept4));

			// U4. get count diff data dept_mstr compare dept_mstr_temp (for UPDATE)
			long resultUser4 = migSampleDao.getCountForUserUpdate();
			System.out.println("####################################################\n");
			System.out.println("resultUser4 : " + String.valueOf(resultUser4));

			// D5. get count not exist dept_mstr compare dept_mstr_temp (for INSERT)
			long resultDept5 = migSampleDao.getCountForDeptInsert();
			System.out.println("####################################################\n");
			System.out.println("resultDept5 : " + String.valueOf(resultDept5));

			// U5. get count not exist dept_mstr compare dept_mstr_temp (for INSERT)
			long resultUser5 = migSampleDao.getCountForUserInsert();
			System.out.println("####################################################\n");
			System.out.println("resultUser5 : " + String.valueOf(resultUser5));

			// D6. get total count dept_mstr
			long resultDept6 = migSampleDao.getCountForDept();
			System.out.println("####################################################\n");
			System.out.println("resultDept6 : " + String.valueOf(resultDept6));

			// U6. get total count dept_mstr
			long resultUser6 = migSampleDao.getCountForUser();
			System.out.println("####################################################\n");
			System.out.println("resultUser6 : " + String.valueOf(resultUser6));

			// D7. check limit change count rule
			long currDeptPercent = (long)(((float)resultDept3 + (float)resultDept4) / (float)resultDept6 * 100);
			System.out.println("####################################################\n");
			System.out.println("currDeptPercent : " + String.valueOf(currDeptPercent));

			// U7. check limit change count rule
			long currUserPercent = (long)(((float)resultUser3 + (float)resultUser4) / (float)resultUser6 * 100);
			System.out.println("####################################################\n");
			System.out.println("currUserPercent : " + String.valueOf(currUserPercent));
			
			
			// 999. get limit value 
			int resultLimit = migSampleDao.getLimitValue();
			System.out.println("####################################################\n");
			System.out.println("resultLimit : " + String.valueOf(resultLimit));
			
			// ##############################################
			if("0".equals(isForce) && (currDeptPercent > resultLimit || currUserPercent > resultLimit)) {
				//STOP
				if (statusVO != null) {
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "MIG_0021",
							"overflow limit : limit= " + resultLimit + ", currDept= " + currDeptPercent + ", currUser= " + currUserPercent);
				}
				
			} else {
				
				// D8. act delete 
				long resultDept8 = migSampleDao.deleteForMigDeptData();
				System.out.println("####################################################\n");
				System.out.println("resultDept8 : " + String.valueOf(resultDept8));

				// U8. act delete 
				long resultUser8 = migSampleDao.deleteForMigUserData();
				System.out.println("####################################################\n");
				System.out.println("resultUser8 : " + String.valueOf(resultUser8));

				// D9. act update
				long resultDept9 = migSampleDao.updateForMigDeptData();
				System.out.println("####################################################\n");
				System.out.println("resultDept9 : " + String.valueOf(resultDept9));

				// U9. act update
				long resultUser9 = migSampleDao.updateForMigUserData();
				System.out.println("####################################################\n");
				System.out.println("resultUser9 : " + String.valueOf(resultUser9));

				// D10. act insert
				long resultDept10 = migSampleDao.insertForMigDeptData();
				System.out.println("####################################################\n");
				System.out.println("resultDept10 : " + String.valueOf(resultDept10));

				// U10. act insert
				long resultUser10 = migSampleDao.insertForMigUserData();
				System.out.println("####################################################\n");
				System.out.println("resultUser10 : " + String.valueOf(resultUser10));
				
				
				if (statusVO != null) {
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, "MIG_0001",
							MessageSourceHelper.getMessage(GPMSConstants.MSG_SUCCESS));
				}
			}

		} catch (Exception ex) {
			logger.error("error in createMigProcess : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	@Override
	public StatusVO changeValue(String valueType, String value) throws Exception {
		StatusVO statusVO = new StatusVO();

		long result = 0;
		
		try {
			if("period".equalsIgnoreCase(valueType)) {
				result = migSampleDao.updatePeriod(value);
			} else if("limit".equalsIgnoreCase(valueType)) {
				result = migSampleDao.updateLimit(value);
			}

			if (result > 0) {
				if("period".equalsIgnoreCase(valueType)) {
					if(!"0".equalsIgnoreCase(value)) {
						try {
							int p = Integer.valueOf(value);
							schedulerRunner.runSchedule(p);
						} catch(NumberFormatException nex) {
							schedulerRunner.runSchedule(30);
						}
					} else {
						try {
							schedulerRunner.stopSchedule();
						} catch(Exception nex) {
							System.out.println(">>>>> " + nex);
						}
					}
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
							"Period Save OK");
				} else if("limit".equalsIgnoreCase(valueType)) {
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
							"Limit-value Save OK");
				}
			} else {
				if("period".equalsIgnoreCase(valueType)) {
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
							"Period Save Fail");
				} else if("limit".equalsIgnoreCase(valueType)) {
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
							"Limit-value Save Fail");
				}
			}

		} catch (Exception ex) {
			logger.error("error in changeValue : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

}
