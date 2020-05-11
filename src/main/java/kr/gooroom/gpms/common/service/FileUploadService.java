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

package kr.gooroom.gpms.common.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for fileupload service.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public interface FileUploadService {

	/**
	 * initialize method from object
	 * 
	 * @return void
	 *
	 */
	void init();

	/**
	 * save file physically to specified location.
	 * 
	 * @param file MultipartFile file request body
	 * @return FileVO saved file data
	 *
	 */
	FileVO store(MultipartFile file);

	/**
	 * read all file data from disk.
	 * <p>
	 * return stream format.
	 * 
	 * @return stream File stream object
	 *
	 */
	Stream<Path> loadAll();

	/**
	 * read one file data from disk.
	 * <p>
	 * return stream format.
	 * 
	 * @param filename string target file name
	 * @return path file path
	 *
	 */
	Path load(String filename);

	/**
	 * read and create resource object from specified file.
	 * 
	 * @param filename string target file name
	 * @return resource Resource for file, spring.io.Resource
	 *
	 */
	Resource loadAsResource(String filename);

	/**
	 * delete all file data.
	 * <p>
	 * use spring util.
	 * 
	 * @return void
	 *
	 */
	void deleteAll();
}
