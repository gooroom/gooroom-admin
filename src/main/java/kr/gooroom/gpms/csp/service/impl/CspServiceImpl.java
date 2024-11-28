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

package kr.gooroom.gpms.csp.service.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.Resource;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
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
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.csp.service.CspService;
import kr.gooroom.gpms.csp.service.CspVO;
import kr.gooroom.gpms.gkm.utils.CertificateUtils;
import kr.gooroom.gpms.gkm.utils.CertificateVO;

/**
 * Gooroom cloud service provider management service implements class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("cspService")
public class CspServiceImpl implements CspService {

	private static final Logger logger = LoggerFactory.getLogger(CspServiceImpl.class);

	@Resource(name = "cspDAO")
	private CspDAO cspDao;

	/**
	 * check csp id for duplicate.
	 * 
	 * @param gcspId string target csp id.
	 * @return StatusVO result status
	 */
	@Override
	public StatusVO isExistGcspId(String gcspId) {
		StatusVO statusVO = new StatusVO();

		try {

			boolean re = cspDao.isExistGcspId(gcspId);

			if (re) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DUPLICATE,
						MessageSourceHelper.getMessage("gcsp.result.duplicate"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_NODUPLICATE,
						MessageSourceHelper.getMessage("gcsp.result.noduplicate"));
			}

		} catch (Exception ex) {
			logger.error("error in isExistGcspId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * create new gooroom csp configuration data.
	 * 
	 * @param cspVO CspVO csp cofiguration data bean.
	 * @return StatusVO result status
	 */
	@Override
	public StatusVO createGcspData(CspVO cspVO) {

		StatusVO statusVO = new StatusVO();

		try {

			cspVO.setRegUserId(LoginInfoHelper.getUserId());
			cspVO.setModUserId(LoginInfoHelper.getUserId());

			// set stop status when create.
			cspVO.setStatusCd(GPMSConstants.STS_SERVICE_RUN);

			long resultCnt = cspDao.createGcspData(cspVO);
			if (resultCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("gcsp.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("gcsp.result.noinsert"));
			}

		} catch (Exception ex) {
			logger.error("error in createGcspData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
				MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * modify gooroom csp information
	 * 
	 * @param cspVO CspVO csp cofiguration data bean.
	 * @return StatusVO result status
	 */
	@Override
	public StatusVO editGcspData(CspVO cspVO) {

		StatusVO statusVO = new StatusVO();

		try {

			cspVO.setModUserId(LoginInfoHelper.getUserId());

			long reCnt = cspDao.editGcspData(cspVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("gcsp.result.update"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("gcsp.result.noupdate"));
			}
		} catch (Exception ex) {
			logger.error("error in editGcspData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * generate gooroom csp list data.
	 * 
	 * @param gcsp_status string status value.
	 * @param search_key string search keyword.
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getGcspDataList(String gcsp_status, String search_key) {

		ResultVO resultVO = new ResultVO();
		try {
			List<CspVO> re = cspDao.selectGcspDataList(gcsp_status, search_key);
			if (re != null && re.size() > 0) {
				CspVO[] row = re.toArray(CspVO[]::new);
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
			logger.error("error in getGcspDataList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * generate gooroom csp list data paging
	 * 
	 * @param options HashMap.
	 * @return ResultVO result object
	 */
	@Override
	public ResultPagingVO getGcspListPaged(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();
		try {
			List<CspVO> re = cspDao.selectGcspListPaged(options);
			long totalCount = cspDao.selectGcspListTotalCount(options);
			long filteredCount = cspDao.selectGcspListFilteredCount(options);

			if (re != null && re.size() > 0) {
				CspVO[] row = re.toArray(CspVO[]::new);
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
			logger.error("error in getGcspDataListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * generate gooroom csp data by csp id.
	 * 
	 * @param gcspId string csp id.
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getGcspData(String gcspId) {

		ResultVO resultVO = new ResultVO();

		try {

			CspVO re = cspDao.selectGcspData(gcspId);

			if (re != null) {

				CspVO[] row = new CspVO[1];
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
			logger.error("error in getGcspData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
				MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate certificate for csp from CSR.
	 * 
	 * @param vo CspVO csp cofiguration data bean.
	 * @return ResultVO result object
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	synchronized public ResultVO createGCSPCertificateFromCSR(CspVO vo) throws Exception {

		ResultVO resultVO = new ResultVO();
		String certPem = "";

		CertificateUtils utils = new CertificateUtils();

		boolean isExist = cspDao.isExistGcspId(vo.getGcspId());
		if (isExist) {

			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DUPLICATE,
					MessageSourceHelper.getMessage("gcsp.cn.duplicate")));
			return resultVO;
		}

		// create unique number.
		UUID uuid = UUID.nameUUIDFromBytes(vo.getGcspId().getBytes(StandardCharsets.UTF_8));
		BigInteger newSerialNo = utils.getBigIntegerFromUuid(uuid);

		try {
			// GCSP expire date : after 10 years
			Calendar validTo = Calendar.getInstance();
			// add 10 years and set last date
			validTo.add(Calendar.YEAR, 10);
			validTo.set(Calendar.MONTH, 11);
			validTo.set(Calendar.DATE, 31);
			validTo.set(Calendar.HOUR_OF_DAY, 23);
			validTo.set(Calendar.MINUTE, 59);
			validTo.set(Calendar.SECOND, 59);

			String expirationYmd = new SimpleDateFormat("yyyyMMdd").format(validTo.getTime());
			X509Certificate clientCert = utils.signCSR(new StringReader(vo.getGcspCsr()), validTo.getTime(),
					newSerialNo);

			if (clientCert != null) {
				// change data format to PEM
				PemObject pemObject = new PemObject("CERTIFICATE", clientCert.getEncoded());
				StringWriter sw = new StringWriter();
				try (PemWriter pemWriter = new PemWriter(sw)) {
					pemWriter.writeObject(pemObject);
				}
				certPem = sw.toString();

				if (certPem != null && !"".equals(certPem) && certPem.length() > 0) {

					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_CREATE,
							MessageSourceHelper.getMessage("gcsp.certificate.create")));

					vo.setCert(certPem);
					vo.setExpirationYmd(expirationYmd);
					vo.setModUserId(LoginInfoHelper.getUserId());

					// save to table
					cspDao.saveGcspCert(vo);
					Object[] objects = { vo };
					resultVO.setData(objects);

				} else {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SIGNERROR,
							MessageSourceHelper.getMessage("gcsp.certificate.signerror")));
				}
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SIGNERROR,
						MessageSourceHelper.getMessage("gcsp.certificate.signerror")));
			}

		} catch (SQLException sqlEx) {

			logger.error("error in createGCSPCertificateFromCSR : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
			throw sqlEx;

		} catch (Exception ex) {

			logger.error("error in createGCSPCertificateFromCSR : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}

		return resultVO;
	}

	/**
	 * generate certificate for csp.
	 * 
	 * @param vo CspVO csp cofiguration data bean.
	 * @return ResultVO result object
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	synchronized public ResultVO createGCSPCertificate(CspVO vo) {

		ResultVO resultVO = new ResultVO();
		String certPem = "";
		String privPem = "";

		CertificateUtils utils = new CertificateUtils();

		// create unique number.
		UUID uuid = UUID.nameUUIDFromBytes(vo.getGcspId().getBytes(StandardCharsets.UTF_8));
		BigInteger newSerialNo = utils.getBigIntegerFromUuid(uuid);

		try {
			// GCSP expire date : after 10 years
			Calendar validTo = Calendar.getInstance();
			// add 10 years and set last date
			validTo.add(Calendar.YEAR, 10);
			validTo.set(Calendar.MONTH, 11);
			validTo.set(Calendar.DATE, 31);
			validTo.set(Calendar.HOUR_OF_DAY, 23);
			validTo.set(Calendar.MINUTE, 59);
			validTo.set(Calendar.SECOND, 59);

			String expirationYmd = new SimpleDateFormat("yyyyMMdd").format(validTo.getTime());
			CertificateVO certVO = utils.createGcspCertificate(vo.getGcspId(), validTo.getTime(), newSerialNo);

			certPem = certVO.getCertificatePem();
			privPem = certVO.getPrivateKeyPem();

			if (certPem != null && !"".equals(certPem) && certPem.length() > 0) {

				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_CREATE,
						MessageSourceHelper.getMessage("gcsp.certificate.create")));

				vo.setCert(certPem);
				vo.setPriv(privPem);
				vo.setExpirationYmd(expirationYmd);
				vo.setModUserId(LoginInfoHelper.getUserId());
				vo.setSerialNo(String.valueOf(newSerialNo));

				// save to table - no need anymore.
				// cspDao.saveGcspCert(vo);
				Object[] objects = { vo };
				resultVO.setData(objects);

			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SIGNERROR,
						MessageSourceHelper.getMessage("gcsp.certificate.signerror")));
			}

		} catch (Exception ex) {

			logger.error("error in createGCSPCertificate : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}

		return resultVO;
	}

	/**
	 * delete csp information data
	 * 
	 * @param vo AdminUserVO data bean
	 * @return StatusVO result status
	 */
	@Override
	public StatusVO deleteGcspData(CspVO vo) {

		StatusVO statusVO = new StatusVO();

		try {
			long reCnt = cspDao.deleteGcspData(vo);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("admin.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("admin.result.nodelete"));
			}

		} catch (Exception ex) {
			logger.error("error in deleteGcspData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}
		return statusVO;
	}
}
