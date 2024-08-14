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

package kr.gooroom.gpms.common.service.impl;

import jakarta.annotation.Resource;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.controller.GRFileHandleException;
import kr.gooroom.gpms.common.controller.GRFileNotFoundException;
import kr.gooroom.gpms.common.service.FileUploadService;
import kr.gooroom.gpms.common.service.FileVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.stream.Stream;

/**
 * file upload service implements class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("fileUploadService")
public class FileUploadServiceImpl implements FileUploadService {

	private static final Logger logger = LoggerFactory.getLogger(FileUploadServiceImpl.class);

	@Resource(name = "gpmsCommonDAO")
	private GpmsCommonDAO gpmsCommonDAO;

	private final Path rootLocation;

	@Autowired
	public FileUploadServiceImpl() {
		this.rootLocation = Paths.get(GPMSConstants.PATH_FOR_ICONFILE);
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

	/**
	 * save file physically to specified location.
	 * 
	 * @param file MultipartFile file request body
	 * @return FileVO saved file data
	 *
	 */
	@Override
	public FileVO store(MultipartFile file) {
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
				// duplicate file
				filename = createNewFilename(this.rootLocation, filename, 1);
			}

			Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

			// save data
			FileVO vo = new FileVO();
			vo.setFileName(filename);
			vo.setFileOriginalName(file.getOriginalFilename());
			vo.setFilePath(this.rootLocation.toString());
			vo.setFileSize(String.valueOf(file.getSize()));
			vo.setDeleteYn(GPMSConstants.GUBUN_NO);
			vo.setRegUserId(LoginInfoHelper.getUserId());

			if(file.getName().equals("wallpaperFile")) {
				vo.setFileType("wallpaper");
			} else {
				vo.setFileType("icon");
			}
			gpmsCommonDAO.insertFileInfo(vo);

			return vo;

		} catch (IOException e) {
			throw new GRFileHandleException("Failed to store file " + filename, e);
		} catch (SQLException e) {
			throw new GRFileHandleException("Failed to store file " + filename, e);
		}
	}

	/**
	 * save file physically to specified location.
	 *
	 * @param vo Basic theme icon file
	 * @return FileVO saved file data
	 *
	 */
	public FileVO store(FileVO vo) {
		try {
			gpmsCommonDAO.insertFileInfo(vo);
		} catch (Exception e) {
			throw new GRFileHandleException("Failed to store file " + vo.getFileName(), e);
		}

		return vo;
	}

	/**
	 * read all file data from disk.
	 * <p>
	 * return stream format.
	 * 
	 * @return stream File stream object
	 *
	 */
	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(path -> this.rootLocation.relativize(path));
		} catch (IOException e) {
			throw new GRFileHandleException("Failed to read stored files", e);
		}

	}

	/**
	 * read one file data from disk.
	 * <p>
	 * return stream format.
	 * 
	 * @param filename string target file name
	 * @return path file path
	 *
	 */
	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	/**
	 * read and create resource object from specified file.
	 * 
	 * @param filename string target file name
	 * @return resource Resource for file, spring.io.Resource
	 *
	 */
	@Override
	public org.springframework.core.io.Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			org.springframework.core.io.Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new GRFileNotFoundException("Could not read file: " + filename);

			}
		} catch (MalformedURLException e) {
			throw new GRFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	/**
	 * delete all file data.
	 * <p>
	 * use spring util.
	 * 
	 * @return void
	 *
	 */
	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	/**
	 * initialize method from object
	 * 
	 * @return void
	 *
	 */
	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new GRFileHandleException("Could not initialize storage", e);
		}
	}

}
