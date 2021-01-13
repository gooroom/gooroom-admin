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

package kr.gooroom.gpms.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Helper class for message service.
 * <p>
 * write message in property file (resources/messages/)
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class MessageSourceHelper {

	private static final Logger logger = LoggerFactory.getLogger(MessageSourceHelper.class);

	private static MessageSource messageSource = null;

	private MessageSourceHelper(MessageSource messageSource) {
		MessageSourceHelper.messageSource = messageSource;
	}

	/**
	 * get message source object
	 * 
	 * @return MessageSource message object
	 *
	 */
	public static MessageSource getMessageSource() {
		return messageSource;
	}

	/**
	 * set message source object
	 * 
	 * @param messageSource MessageSource message object
	 * @return void
	 *
	 */
	public static void setMessageSource(MessageSource messageSource) {
		MessageSourceHelper.messageSource = messageSource;
	}

	/**
	 * get message by I18n process
	 * 
	 * @param code string message code
	 * @return String message
	 *
	 */
	public static String getMessage(String code) {
		try {
			return messageSource == null ? code : messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
		} catch (Exception e) {
			logger.error(e.toString());
			return code;
		}
	}

	/**
	 * get message by I18n process
	 * 
	 * @param msr message source resolvable
	 * @return String message
	 *
	 */
	public static String getMessage(MessageSourceResolvable msr) {
		return messageSource == null ? msr.getDefaultMessage()
				: messageSource.getMessage(msr, LocaleContextHolder.getLocale());
	}

	/**
	 * get message by I18n process.
	 * <p>
	 * use code and arguements
	 * 
	 * @param code string message code
	 * @param args string array arguements
	 * @return String message
	 *
	 */
	public static String getMessage(String code, Object[] args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}

	/**
	 * get message by I18n process.
	 * <p>
	 * use code and one arguement
	 * 
	 * @param code string message code
	 * @param args string arguement
	 * @return String message
	 *
	 */
	public static String getMessage(String code, String args) {
		return messageSource.getMessage(code, new String[] { args }, LocaleContextHolder.getLocale());
	}
}
