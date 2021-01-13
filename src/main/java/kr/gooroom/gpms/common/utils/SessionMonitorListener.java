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

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Listener for session management.
 * <p>
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class SessionMonitorListener implements HttpSessionListener {

	private final AtomicInteger activeSessions;

	public SessionMonitorListener() {
		super();

		activeSessions = new AtomicInteger();
	}

	/**
	 * get total session count
	 * 
	 * @return int session count.
	 *
	 */
	public int getTotalActiveSession() {

		return activeSessions.get();
	}

	/**
	 * increase session count
	 * 
	 * @param se HttpSessionEvent
	 * @return void
	 *
	 */
	@Override
	public void sessionCreated(HttpSessionEvent se) {
//		System.out.println(">> session CREATED.::  " + se.getSession().getId());
//		System.out.println(">> getMaxInactiveInterval.::  " + se.getSession().getMaxInactiveInterval());
//		for (@SuppressWarnings("unchecked")
//		Enumeration<String> e = se.getSession().getAttributeNames(); e.hasMoreElements();)
//			System.out.println(">> e :: " + e.nextElement());

		activeSessions.incrementAndGet();
//		System.out.println(">>-------------------------------------------------------- " + getTotalActiveSession() + "\n");
	}

	/**
	 * decrease session count
	 * 
	 * @param se HttpSessionEvent
	 * @return void
	 *
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
//		System.out.println(">> session DESTROYED.::  " + se.getSession().getId());
//		System.out.println(">> getMaxInactiveInterval.::  " + se.getSession().getMaxInactiveInterval());
//		for (@SuppressWarnings("unchecked")
//		Enumeration<String> e = se.getSession().getAttributeNames(); e.hasMoreElements();)
//			System.out.println(">> e :: " + e.nextElement());

		activeSessions.decrementAndGet();
//		System.out.println(">>-------------------------------------------------------- " + getTotalActiveSession() + "\n");
	}

}
