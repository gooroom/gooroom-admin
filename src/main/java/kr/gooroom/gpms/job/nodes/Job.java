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

package kr.gooroom.gpms.job.nodes;

import java.util.HashMap;

/**
 * 
 * Gooroom job handle class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class Job {

	private Module module;

	/**
	 * get module for job
	 * 
	 * @return Module job module
	 *
	 */
	public Module getModule() {
		return module;
	}

	/**
	 * get module for job
	 * 
	 * @param module job module
	 * @return void
	 *
	 */
	public void setModule(Module module) {
		this.module = module;
	}

	/**
	 * create job with package
	 * 
	 * @param moduleName string job module name
	 * @param taskName   string job task name
	 * @param packages   string job package name
	 * @return void
	 *
	 */
	public void createJobWithPackage(String moduleName, String taskName, String packages) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("pkgs", packages);

		createJobWithMap(moduleName, taskName, map);
	}

	/**
	 * create job
	 * 
	 * @param moduleName string job module name
	 * @param taskName   string job task name
	 * @return void
	 *
	 */
	public void createJob(String moduleName, String taskName) {

		HashMap<String, String> map = new HashMap<String, String>();
		createJobWithMap(moduleName, taskName, map);
	}

	/**
	 * create job with label
	 * 
	 * @param moduleName string job module name
	 * @param taskName   string job task name
	 * @param label      string job label
	 * @return void
	 *
	 */
	public void createJobWithLabel(String moduleName, String taskName, String label) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("label", label);

		createJobWithMap(moduleName, taskName, map);
	}

	/**
	 * create job with map(hashmap)
	 * 
	 * @param moduleName string job module name
	 * @param taskName   string job task name
	 * @param map        HashMap
	 * @return void
	 *
	 */
	public void createJobWithMap(String moduleName, String taskName, HashMap<String, String> map) {

		Module module = new Module();
		module.setModule_name(moduleName);

		Task task = new Task();
		task.setIn(map);
		task.setTask_name(taskName);

		module.setTask(task);

		setModule(module);
	}

	/**
	 * generate job instance
	 * 
	 * @param moduleName string job module name
	 * @param taskName   string job task name
	 * @return Job
	 *
	 */
	public static Job generateJob(String moduleName, String taskName) {

		Job job = new Job();
		HashMap<String, String> map = new HashMap<String, String>();
		job.createJobWithMap(moduleName, taskName, map);

		return job;
	}

	/**
	 * generate job instance with label
	 * 
	 * @param moduleName string job module name
	 * @param taskName   string job task name
	 * @param label      string job label
	 * @return Job
	 *
	 */
	public static Job generateJobWithLabel(String moduleName, String taskName, String label) {

		Job job = new Job();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("label", label);
		job.createJobWithMap(moduleName, taskName, map);

		return job;
	}

	/**
	 * generate job instance with package
	 * 
	 * @param moduleName string job module name
	 * @param taskName   string job task name
	 * @param packages   string job package name
	 * @return Job
	 *
	 */
	public static Job generateJobWithPackage(String moduleName, String taskName, String packages) {

		Job job = new Job();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("pkgs", packages);

		job.createJobWithMap(moduleName, taskName, map);

		return job;
	}

	/**
	 * generate job instance with map(hashmap)
	 * 
	 * @param moduleName string job module name
	 * @param taskName   string job task name
	 * @param map        HashMap
	 * @return void
	 *
	 */
	public static Job generateJobWithMap(String moduleName, String taskName, HashMap<String, String> map) {

		Job job = new Job();
		job.setModule(new Module(moduleName, new Task(taskName, map, "invisiable")));

		return job;
	}

}
