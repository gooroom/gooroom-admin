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

package kr.gooroom.gpms.mng.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import kr.gooroom.gpms.common.controller.GRFileHandleException;
import kr.gooroom.gpms.common.service.impl.GpmsCommonDAO;
import kr.gooroom.gpms.common.utils.CommonUtils;
import kr.gooroom.gpms.mng.service.WallpaperMngService;
import kr.gooroom.gpms.mng.service.WallpaperVO;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.FileVO;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.mng.service.ThemeMngService;
import kr.gooroom.gpms.mng.service.ThemeVO;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Theme management service implements class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("themeMngService")
public class ThemeMngServiceImpl implements ThemeMngService {

	private static final Logger logger = LoggerFactory.getLogger(ThemeMngServiceImpl.class);

	@Resource(name = "themeMngDAO")
	private ThemeMngDAO themeMngDAO;

	@Resource(name = "gpmsCommonDAO")
	private GpmsCommonDAO gpmsCommonDAO;

	@Resource(name = "wallpaperMngService")
	private WallpaperMngService wallpaperMngService;

	private final Path rootLocation;

	@Autowired
	public ThemeMngServiceImpl() {
		this.rootLocation = Paths.get(GPMSConstants.PATH_FOR_ICONFILE);
	}
	/**
	 * create new theme data
	 * 
	 * @param multipartHttpServletRequest MultipartHttpServletRequest file list
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createThemeData(ThemeVO themeVO, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception {

		Iterator<String> iterator = multipartHttpServletRequest.getFileNames();

		StatusVO statusVO = new StatusVO();

		try {

			//get basic theme info themeId = 1
			List<FileVO> theme1Icons = themeMngDAO.selectThemeData("1").getThemeIcons();

			String themeId = "";
			while(iterator.hasNext()) {
				String fileName = iterator.next();
				MultipartFile multipartFile = multipartHttpServletRequest.getFile(fileName);

				FileVO vo = null;
				if(fileName.equals("wallpaperFile")) {
					WallpaperVO wallpaperVO = new WallpaperVO();
					if(multipartFile != null && !multipartFile.isEmpty()) {
						vo = storeWallpaper(multipartFile, null);
					} else {
						vo = storeWallpaper(null, gpmsCommonDAO.selectFileInfo("1"));
					}

					if (vo != null) {
						themeId = vo.getFileNo();
						wallpaperVO.setWallpaperId(themeId);
						wallpaperVO.setFileNo(themeId);
					}

					// create wallPaper
					try {
						wallpaperMngService.createWallpaperData(wallpaperVO);
					} catch (Exception ex) {
						logger.error("error in createThemeData when createWallpaperData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
								MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
					}
					break;
				}
			}

			//insert icon
			ArrayList<FileVO> files = new ArrayList<>();
			iterator = multipartHttpServletRequest.getFileNames();
			while (iterator.hasNext()) {
				String fileName = iterator.next();
				MultipartFile multipartFile = multipartHttpServletRequest.getFile(fileName);

				if(fileName.equals("wallpaperFile")) {
					continue;
				}

				FileVO vo = null;
				if (multipartFile != null && !multipartFile.isEmpty()) {
					vo = storeIcons(multipartFile, themeId);
				} else { //파일 없을때 - 테마 1번으로
					vo = theme1Icons.stream().filter(s -> s.getFileName().equals("1_" + fileName + ".svg")).findFirst().get();
					vo.setFileEtcInfo(fileName);
					vo.setFileNo(themeId);
					vo.setFileType("icon");
					vo.setRegUserId(LoginInfoHelper.getUserId());
					storeIcons(vo);
				}
				files.add(vo);
			}

//			if(files.size() < 34) {
//				throw new Exception(MessageSourceHelper.getMessage("theme.result.noinsert") + "(아이콘 갯수 부족)");
//			}

			//  wallpaper 등록한 id로 theme_mstr 등록
			themeVO.setThemeId(themeId);
			themeVO.setThemeNm("테마" + themeId);
			themeVO.setThemeCmt(themeVO.getThemeCmt());
			themeVO.setWallpaper(themeId);
			themeVO.setThemeIcons(files);

			themeVO.setModUserId(LoginInfoHelper.getUserId());
			themeVO.setRegUserId(LoginInfoHelper.getUserId());

			long resultCnt = themeMngDAO.insertThemeData(themeVO);
			if (resultCnt > 0) {

				List<FileVO> icons = themeVO.getThemeIcons();
				for (FileVO vo : icons) {
					HashMap<String, String> param = new HashMap<String, String>();
					param.put("themeId", themeVO.getThemeId());
					param.put("appNm", vo.getFileEtcInfo());
					param.put("fileNo", vo.getFileNo());
					param.put("regUserId", LoginInfoHelper.getUserId());

					themeMngDAO.insertThemeIconData(param);
				}
				//file no sequence reset to 0
				themeMngDAO.updateFileSeqToReset();

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("theme.result.insert"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("theme.result.noinsert"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in createThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						ex.getMessage().length() > 0 ? ex.getMessage() : MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * delete theme data.
	 * 
	 * @param themeId String theme id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteThemeData(String themeId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			//실제 파일들 삭제
			ThemeVO originThemeVO = themeMngDAO.selectThemeData(themeId);
			// 1. wallpaper삭제
			File wallpaper = new File(GPMSConstants.PATH_FOR_ICONFILE + File.separator + originThemeVO.getWallpaperFileNm());
			if(wallpaper.exists()) {
				wallpaper.delete();
			}

			// 2. icon file 들 삭제
			File iconFile = null;
			for(FileVO icon : originThemeVO.getThemeIcons()) {
				if(icon.getDeleteYn().equals(GPMSConstants.GUBUN_YES)) {
					iconFile = new File(GPMSConstants.PATH_FOR_ICONFILE + File.separator + icon.getFileOriginalName());
					if(iconFile.exists()) {
						iconFile.delete();
					}
				}
			}

			long re = themeMngDAO.deleteThemeData(themeId);
			if (re > 0) {
				wallpaperMngService.deleteWallpaperData(themeId);
				long re1 = themeMngDAO.deleteThemeFileInfo(themeId);
				if(re1 > 0) {
					themeMngDAO.deleteThemeIconData(themeId);

					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
							MessageSourceHelper.getMessage("theme.result.delete"));
				} else {
					throw new Exception(MessageSourceHelper.getMessage("theme.result.nodelete") + "(아이콘 파일 리스트 삭제 실패)");
				}
			} else {
				throw new Exception(MessageSourceHelper.getMessage("theme.result.nodelete") + "(테마 삭제 실패)");
			}

		} catch (SQLException sqlEx) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in deleteThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in deleteThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						ex.getMessage().length() > 0 ? ex.getMessage() : MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * edit theme data
	 * 
	 * @param themeVO ThemeVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO editThemeData(ThemeVO themeVO, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception {

		Iterator<String> iterator = multipartHttpServletRequest.getFileNames();

		StatusVO statusVO = new StatusVO();

		try {

			//get basic theme info themeId = 1
			List<FileVO> theme1Icons = themeMngDAO.selectThemeData("1").getThemeIcons();

			ThemeVO originThemeVO = themeMngDAO.selectThemeData(themeVO.getThemeId());
			if(originThemeVO == null) {
				// 수정할 테마 아이디 존재안함
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("theme.result.noupdate"));
				return statusVO;
			}

			while(iterator.hasNext()) {
				String fileName = iterator.next();
				MultipartFile multipartFile = multipartHttpServletRequest.getFile(fileName);

				if(fileName.equals("wallpaperFile")) {
					if(multipartFile != null && !multipartFile.isEmpty()) {
						// 기존에 있던 파일 삭제 & 새로운 파일 삽입
						restoreThemeFile(multipartFile, null, gpmsCommonDAO.selectFileInfo(originThemeVO.getWallpaper()));
					} else {
						// 테마 배경화면 파일 없을때
						restoreThemeFile(null, gpmsCommonDAO.selectFileInfo("1"), gpmsCommonDAO.selectFileInfo(originThemeVO.getWallpaper()));
					}
					break;
				}
			}

			//insert icon
			ArrayList<FileVO> files = new ArrayList<>();
			iterator = multipartHttpServletRequest.getFileNames();
			while (iterator.hasNext()) {
				String fileName = iterator.next();
				MultipartFile multipartFile = multipartHttpServletRequest.getFile(fileName);

				if(fileName.equals("wallpaperFile")) {
					continue;
				}

				FileVO vo = null;
				if (multipartFile != null && !multipartFile.isEmpty()) {
					vo = restoreThemeFile(multipartFile, null, originThemeVO.getThemeIcons().stream().filter(n -> n.getFileEtcInfo().equals(fileName)).findFirst().orElse(null));
				} else {
					//파일 없을때 - 테마 1번으로
					vo = theme1Icons.stream().filter(s -> s.getFileName().equals("1_" + fileName + ".svg")).findFirst().get();
					vo.setFileEtcInfo(fileName);
					vo.setFileNo(originThemeVO.getThemeIcons().stream().filter(n -> n.getFileEtcInfo().equals(fileName)).findFirst().orElse(null).getFileNo());
					vo.setFileType("icon");
					vo.setDeleteYn(GPMSConstants.GUBUN_NO);
					restoreThemeFile(null, vo, originThemeVO.getThemeIcons().stream().filter(n -> n.getFileEtcInfo().equals(fileName)).findFirst().orElse(null));
				}
				files.add(vo);
			}

//			if(files.size() < 34) {
//				throw new Exception(MessageSourceHelper.getMessage("theme.result.noinsert") + "(아이콘 갯수 부족)");
//			}

			//  wallpaper 등록한 id로 theme_mstr 등록
			themeVO.setThemeId(originThemeVO.getThemeId());
			themeVO.setThemeNm(themeVO.getThemeNm());
			themeVO.setThemeCmt(themeVO.getThemeCmt());
			themeVO.setWallpaper(originThemeVO.getWallpaper());
			themeVO.setThemeIcons(files);
			themeVO.setModUserId(LoginInfoHelper.getUserId());

			long reCnt = themeMngDAO.updateThemeData(themeVO);
			if (reCnt > 0) {
				List<FileVO> icons = themeVO.getThemeIcons();
				for (FileVO vo : icons) {
					HashMap<String, String> param = new HashMap<String, String>();
					param.put("themeId", themeVO.getThemeId());
					param.put("appNm", vo.getFileEtcInfo());
					param.put("fileNo", vo.getFileNo());
					param.put("regUserId", LoginInfoHelper.getUserId());

					reCnt = themeMngDAO.insertThemeIconData(param);
					if(reCnt < 0) {
						throw new Exception();
					}
				}

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("theme.result.update"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("theme.result.noupdate"));
			}

		} catch (SQLException sqlEx) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in editThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in editThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * response theme list data
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	@Override
	public ResultVO getThemeList() throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			List<ThemeVO> re = themeMngDAO.selectThemeList();

			if (re != null && re.size() > 0) {

				ThemeVO[] row = re.stream().toArray(ThemeVO[]::new);

				// create theme url by filepath and filename
				for (ThemeVO vo : row) {
					vo.setWallpaperUrl(GPMSConstants.PATH_FOR_ICONURL + "/" + vo.getWallpaperFileNm());
				}

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

			logger.error("error in getThemeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;

	}

	/**
	 * response theme list data for paging
	 * 
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getThemeListPaged(HashMap<String, Object> options) throws Exception {

		ResultPagingVO resultVO = new ResultPagingVO();
		try {
			List<ThemeVO> re = themeMngDAO.selectThemeListPaged(options);
			long totalCount = themeMngDAO.selectThemeListTotalCount(options);
			long filteredCount = themeMngDAO.selectThemeListFilteredCount(options);

			if (re != null && re.size() > 0) {
				ThemeVO[] row = re.stream().toArray(ThemeVO[]::new);
				// create theme url by filepath and filename
				for (ThemeVO vo : row) {
					vo.setWallpaperUrl(CommonUtils.createIconUrlPath() + vo.getWallpaperFileNm());
				}
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
			logger.error("error in getThemeListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response theme information data by theme id
	 * 
	 * @param options String theme id , ICON_ADDRESS
	 * @return ResultVO result object
	 * @throws Exception
	 */
	@Override
	public ResultVO getThemeData(HashMap<String, Object> options) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			ThemeVO re = themeMngDAO.selectThemeData(options);

			if (re != null) {

				re.setWallpaperUrl(CommonUtils.createIconUrlPath() + re.getWallpaperFileNm());

				ThemeVO[] row = new ThemeVO[1];
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

			logger.error("error in getThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	private String createNewFilename(Path path, String filename, int count) {
		String newFilename = null;
		try {
			newFilename = FilenameUtils.getBaseName(filename) + "-" + count + "."
					+ FilenameUtils.getExtension(filename);
			if (path.resolve(newFilename).toFile().exists()) {
				newFilename = createNewFilename(path, filename, count + 1);
			}
		} catch (Exception ex) {
			logger.error("createNewFilename : {}", ex.toString());
			newFilename = filename;
		}

		return newFilename;
	}

	private String store(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (file.isEmpty()) {
				throw new GRFileHandleException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				throw new GRFileHandleException(
						"Cannot store file with relative path outside current directory " + filename);
			}

			if (this.rootLocation.resolve(filename).toFile().exists()) {
				return filename;
				// duplicate file
				// update시에 원래 있던거랑 동일한게 있으면 파일 저장안하고 넘어가
				//filename = createNewFilename(this.rootLocation, filename, 1);
			}

			Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new GRFileHandleException("Failed to store file " + filename, e);
		}

		return filename;
	}

	/**
	 * restore file physically to specified location.
	 * @param newFileObj new File Object
	 * @param newFile new FileVO
	 * @param originFile origin FileVO
	 * @return
	 */
	@Override
	public FileVO restoreThemeFile(MultipartFile newFileObj, FileVO newFile, FileVO originFile) {

		try {

			File targetFile = new File(GPMSConstants.PATH_FOR_ICONFILE + File.separator + originFile.getFileOriginalName());
			if(originFile.getDeleteYn().equals(GPMSConstants.GUBUN_YES) && targetFile.exists()) {
				// 원래 파일 삭제
				targetFile.delete();
			}

			String filename = "";
			if(newFileObj != null && !newFileObj.isEmpty()) {
				filename = store(newFileObj);

				originFile.setFileName(filename);
				originFile.setFileOriginalName(newFileObj.getOriginalFilename());
				originFile.setFileSize(String.valueOf(newFileObj.getSize()));
				originFile.setFilePath(this.rootLocation.toString());
				originFile.setDeleteYn(GPMSConstants.GUBUN_YES);
				originFile.setModUserId(LoginInfoHelper.getUserId());
				themeMngDAO.updateThemeFileInfo(originFile);
				return originFile;

			} else {
				newFile.setFileNo(originFile.getFileNo());
				newFile.setDeleteYn(GPMSConstants.GUBUN_NO);
				newFile.setModUserId(LoginInfoHelper.getUserId());
				themeMngDAO.updateThemeFileInfo(newFile);
				return newFile;
			}

		} catch (SQLException e) {
			throw new GRFileHandleException("Failed to store file " + newFileObj != null ? newFileObj.getOriginalFilename() : newFile.getFileOriginalName(), e);
		}
	}

	/**
	 * save file physically to specified location.
	 * @param fileType
	 * @param voType
	 * @return
	 */
	@Override
	public FileVO storeWallpaper(MultipartFile fileType, FileVO voType) {

		try {

			// 배경화면 이미 없는 경우
			if(fileType == null) {
				themeMngDAO.insertWallpaperFileInfo(voType);
				return voType;
			}

			String filename = store(fileType);
			// save data
			FileVO vo = new FileVO();
			vo.setFileType("wallpaper");
			vo.setFileName(filename);
			vo.setFileOriginalName(fileType.getOriginalFilename());
			vo.setFilePath(this.rootLocation.toString());
			vo.setFileSize(String.valueOf(fileType.getSize()));
			vo.setDeleteYn(fileType == null ? GPMSConstants.GUBUN_NO : GPMSConstants.GUBUN_YES);
			vo.setRegUserId(LoginInfoHelper.getUserId());

			themeMngDAO.insertWallpaperFileInfo(vo);

			return vo;

		} catch (SQLException e) {
			throw new GRFileHandleException("Failed to store file " + fileType == null ? voType.getFileOriginalName() : fileType.getOriginalFilename(), e);
		}
	}

	/**
	 * save file physically to specified location.
	 *
	 * @param file MultipartFile file request body
	 * @param themeId String
	 * @return FileVO saved file data
	 *
	 */
	@Override
	public FileVO storeIcons(MultipartFile file, String themeId) {
		String filename = store(file);
		try {
			// save data
			FileVO vo = new FileVO();
			vo.setFileNo(themeId);
			vo.setFileName(filename);
			vo.setFileEtcInfo(file.getName());
			vo.setFileOriginalName(file.getOriginalFilename());
			vo.setFilePath(this.rootLocation.toString());
			vo.setFileSize(String.valueOf(file.getSize()));
			vo.setDeleteYn(GPMSConstants.GUBUN_YES);
			vo.setRegUserId(LoginInfoHelper.getUserId());

			themeMngDAO.insertIconFileInfo(vo);

			return vo;

		} catch (SQLException e) {
			throw new GRFileHandleException("Failed to store file " + filename, e);
		}
	}

	/**
	 * save file physically to specified location.
	 *
	 * @param vo FileVO
	 * @return FileVO saved file data
	 *
	 */
	@Override
	public FileVO storeIcons(FileVO vo) {
		try {

			themeMngDAO.insertIconFileInfo(vo);
			return vo;

		} catch (SQLException e) {
			throw new GRFileHandleException("Failed to store file " + vo.getFileEtcInfo(), e);
		}
	}
}
