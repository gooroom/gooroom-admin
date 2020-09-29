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

package kr.gooroom.gpms.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GPMSConstants {

	private static final Properties prop = new Properties();
	private static final String GOOROOM_PROPERTIES = "/properties/gooroomapi.properties";

	static {
		InputStream is = GPMSConstants.class.getClassLoader().getResourceAsStream(GOOROOM_PROPERTIES);
		try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final String GUBUN_YES = "Y";
	public static final String GUBUN_NO = "N";

	public static final String GUBUN_ALL = "ALL";
	public static final String GUBUN_ONE = "ONE";

	public static final String GRROLE_SUPER = "SUPER";
	public static final String GRROLE_ADMIN = "ADMIN";
	public static final String GRROLE_PART = "PART";

	public static final String MSG_SUCCESS = "success";
	public static final String MSG_FAIL = "fail";
	public static final String MSG_DEFAULT = "DEFAULT";

	public static final String RULE_GRADE_DEFAULT = "DEFAULT";
	public static final String RULE_GRADE_STANDARD = "STD";
	public static final String RULE_GRADE_GROUP = "GROUP";
	public static final String RULE_GRADE_DEPT = "DEPT";
	public static final String RULE_GRADE_USER = "USER";

	public static final String GUBUN_DEPT_DELETE_ALL = "DELETEALL";
	public static final String GUBUN_DEPT_UNUSED_ALL = "UNUSEDALL";

	public static final String TYPE_RULE_CLIENT = "CLIENT_CONF";
	public static final String TYPE_RULE_USER = "USER_CONF";

	public static final String TYPE_DESKTOPCONF = "DESKTOPCONF";
	public static final String TYPE_CLIENTCONF = "CLIENTCONF";
	public static final String TYPE_HOSTNAMECONF = "HOSTNAMECONF";
	public static final String TYPE_UPDATESERVERCONF = "UPDATESERVERCONF";

	public static final String TYPE_MEDIARULE = "MEDIARULE";
	public static final String TYPE_BROWSERRULE = "BROWSERRULE";
	//public static final String TYPE_CLIENTSECRULE = "CLIENTSECURULE";
	public static final String TYPE_SECURITYRULE = "SECURITYRULE";
	public static final String TYPE_FILTEREDSOFTWARE = "FILTEREDSWRULE";
	public static final String TYPE_CTRLCENTERITEMRULE = "CTRLCENTERITEMRULE";
	public static final String TYPE_POLICYKITRULE = "POLICYKITRULE";

	public static final String ROOT_CERTPATH = prop.getProperty("gooroom.root.certificate.path");
	public static final String ROOT_CERTFILENAME = prop.getProperty("gooroom.root.certificate.filename");
	public static final String ROOT_KEYPATH = prop.getProperty("gooroom.root.privatekey.path");
	public static final String ROOT_KEYFILENAME = prop.getProperty("gooroom.root.privatekey.filename");
	
	// Mail
	public static final String CFG_MAIL_HOST = prop.getProperty("gooroom.mail.host");
	public static final String CFG_MAIL_PORT = prop.getProperty("gooroom.mail.port");
	public static final String CFG_MAIL_USESSL = prop.getProperty("gooroom.mail.usessl");
	public static final String CFG_MAIL_AUTH_USERNAME = prop.getProperty("gooroom.mail.auth.username");
	public static final String CFG_MAIL_AUTH_PASSWORD = prop.getProperty("gooroom.mail.auth.password");

	public static final String PATH_FOR_ICONFILE = prop.getProperty("gooroom.icon.file.path");
	// icon path
	public static final String ICON_SERVER_PROTOCOL = prop.getProperty("gooroom.icon.server.protocol");
	public static final String ICON_SERVERPATH = prop.getProperty("gooroom.icon.server.path");
	public static final String PATH_FOR_ICONURL = prop.getProperty("gooroom.icon.url.path");

	// Client Status Code
	public static final String STS_INITIALIZE = "STAT001";
	public static final String STS_USABLE = "STAT010";
	public static final String STS_REVOKED = "STAT021";
	public static final String STS_EXPIRE = "STAT022";

	public static final String REQ_STS_USABLE = "usable";
	public static final String REQ_STS_UNUSABLE = "unusable";
	public static final String REQ_STS_EXPIRE = "expire";
	public static final String REQ_STS_REVOKE = "revoke";

	public static final String STS_VIEW_REVOKE = "RVK";
	public static final String STS_VIEW_OFFLINE = "OFF";
	public static final String STS_VIEW_VIOLATED = "VLT";
	public static final String STS_VIEW_NORMAL = "NOR";

	public static final String CFG_HEAD_TAG = prop.getProperty("gooroom.config.head");
	public static final String CFG_TAIL_TAG = prop.getProperty("gooroom.config.tail");

	public static final String CFG_INIT_MAINOS = prop.getProperty("gooroom.config.init.mainos");
	public static final String CFG_INIT_EXTOS = prop.getProperty("gooroom.config.init.extos");
	public static final String CFG_INIT_PRIORITY = prop.getProperty("gooroom.config.init.priority");
	public static final String CFG_INIT_HOSTS = prop.getProperty("gooroom.config.init.hosts");

	// GPMS Main Menu
	public static final String GPMS_MAIN_MENU_TEMPLATE = prop.getProperty("gooroom.gpms.main.menu");
	// GPMS connectable address
	public static final String CTRL_ITEM_AVAILCONNECT_RULE = "AVAILCONNECT_RULE";
	public static final String CTRL_ITEM_AVAILCONNECT_RULE_ABBR = "ACIP";

	// token status_cd
	public static final String TOKEN_LOGIN_STATUS_CODE_VALID = "TOKEN_VALID";

	// GPMS LOGIN ERROR CODE
	public static final String ERR_LOGIN_PASSWORD = "E01";
	public static final String ERR_LOGIN_USER = "E02";
	public static final String ERR_LOGIN_ACCOUNT = "E03";
	public static final String ERR_LOGIN_DENIED = "E21";
	public static final String ERR_LOGIN_DUPLICATE = "E22";
	public static final String ERR_LOGIN_ETC = "E99";

	//
	public static final String STS_SERVICE_RUN = "STAT010";
	public static final String STS_SERVICE_STOP = "STAT020";

	public static final String STS_NORMAL_USER = "STAT010";
	public static final String STS_DELETE_USER = "STAT020";
	public static final String STS_TEMP_PASSWORD = "STAT030";

	// NOTICE STATUS
	public static final String STS_NORMAL_NOTICE = "STAT010";
	public static final String STS_DELETE_NOTICE = "STAT020";

	// NOTICE PUBLISH STATUS
	public static final String STS_ACTIVE_NOTICE_PUBLISH = "STAT010";
	public static final String STS_INACTIVE_NOTICE_PUBLISH = "STAT021";

	// desktop applicaton
	public static final String CFG_DESKTOP_APP = "DESKTOP_APP";
	public static final String CFG_DESKTOP_APP_ABBR = "DEAP";

	// desktop configuration
	public static final String CFG_DESKTOP_SETUP = "DESKTOP_CONF";
	public static final String CFG_DESKTOP_SETUP_ABBR = "DECO";
	
	// client group
	public static final String CTRL_CLIENT_GROUP = "CLIENT_GROUP";
	public static final String CTRL_CLIENT_GROUP_ABBR = "CGRP";
	// (new) gooroom security rule
	public static final String CTRL_ITEM_GRSECU_RULE = "GRSECU_RULE";
	public static final String CTRL_ITEM_GRSECU_RULE_ABBR = "GSRU";
	// gooroom software filter
	public static final String CTRL_ITEM_SWFILTER_RULE = "SWFILTER_RULE";
	public static final String CTRL_ITEM_SWFILTER_RULE_ABBR = "GSFI";
	// gooroom consrolcenter items
	public static final String CTRL_ITEM_CTRLCENTERITEM_RULE = "CTRLCENTERITEM_RULE";
	public static final String CTRL_ITEM_CTRLCENTERITEM_RULE_ABBR = "CTCI";
	
	// gooroom policy kit
	public static final String CTRL_ITEM_POLICYKIT_RULE = "POLICYKIT_RULE";
	public static final String CTRL_ITEM_POLICYKIT_RULE_ABBR = "POKI";
	// policy kit item
	public static final String POLICYKIT_ITEM_GOOROOMUPDATE = "gooroom_update";
	public static final String POLICYKIT_ITEM_GOOROOMAGENT = "gooroom_agent";
	public static final String POLICYKIT_ITEM_GOOROOMREGISTER = "gooroom_register";
	public static final String POLICYKIT_ITEM_GRACEDITOR = "grac_editor";
	public static final String POLICYKIT_ITEM_WIREORWIRELESS = "wire_wireless";
	public static final String POLICYKIT_ITEM_NETWORK = "network_config";
	public static final String POLICYKIT_ITEM_PRINTER = "printer";
	public static final String POLICYKIT_ITEM_DISKMOUNT = "disk_mount";
	public static final String POLICYKIT_ITEM_ADMINEXEC = "pkexec";
	public static final String POLICYKIT_ITEM_PACKAGEMNG = "package_manager";

	// media rule
	public static final String CTRL_ITEM_MEDIACTRL_RULE = "MEDIACTRL_RULE";
	public static final String CTRL_ITEM_MEDIACTRL_RULE_ABBR = "MCRU";
	// media rule item
	public static final String MEDIA_ITEM_USB_MEMORY = "usb_memory";
	public static final String MEDIA_ITEM_CD_DVD = "cd_dvd";
	public static final String MEDIA_ITEM_PRINTER = "printer";
	public static final String MEDIA_ITEM_SCREEN_CAPTURE = "screen_capture";
	public static final String MEDIA_ITEM_CLIPBOARD = "clipboard";
	public static final String MEDIA_ITEM_CAMERA = "camera";
	public static final String MEDIA_ITEM_MICROPHONE = "microphone";
	public static final String MEDIA_ITEM_SOUND = "sound";
	public static final String MEDIA_ITEM_KEYBOARD = "keyboard";
	public static final String MEDIA_ITEM_MOUSE = "mouse";
	public static final String MEDIA_ITEM_WIRELESS = "wireless";
	public static final String MEDIA_ITEM_MAC_ADDRESS = "mac_address";
	public static final String MEDIA_ITEM_USB_SERIALNO = "usb_serialno";
	public static final String MEDIA_ITEM_BLUETOOTH_STATE = "bluetooth_state";


	// browser rule
	public static final String CTRL_ITEM_BROWSERCTRL_RULE = "BROWSERCTRL_RULE";
	public static final String CTRL_ITEM_BROWSERCTRL_RULE_ABBR = "BCRU";

	// client configuration item
	public static final String CTRL_ITEM_SCREENTIME = "screen_time";
	public static final String CTRL_ITEM_PASSWORDTIME = "password_time";
	public static final String CTRL_ITEM_PACKAGEHANDLE = "package_handle";
	public static final String CTRL_ITEM_GLOBALNETWORK = "global_network";
	public static final String CTRL_ITEM_FIREWAllNETWORK = "firewall_network";

	public static final String CTRL_ITEM_FILTEREDSOFTWARE = "filtered_software";
	public static final String CTRL_ITEM_CTRLCENTERITEM = "ctrlcenter_items";
	public static final String CTRL_ITEM_POLICYKIT = "policy_kit";

	// client firewall item
	public static final String CTRL_ITEM_GLOBALFIREWALL = "state";

	// Update server configuration
	public static final String CTRL_UPDATE_SERVER_CONF = "UPSERVER_CONF";
	public static final String CTRL_UPDATE_SERVER_CONF_ABBR = "USCF";
	// Update server configuration item
	public static final String CTRL_ITEM_MAINOS = "MAINOS";
	public static final String CTRL_ITEM_EXTOS = "EXTOS";
	public static final String CTRL_ITEM_PRIORITIES = "PRIORITIES";

	// Hosts configuration
	public static final String CTRL_HOSTS_SETUP_CONF = "HOST_CONF";
	public static final String CTRL_HOSTS_SETUP_CONF_ABBR = "HOCF";
	// Hosts configuration item
	public static final String CTRL_ITEM_HOSTNAME = "HOSTS";

	// client configuration
	public static final String CTRL_CLIENT_SETUP_CONF = "CLIENT_CONF";
	public static final String CTRL_CLIENT_SETUP_CONF_ABBR = "CLCF";
	// client configuration item
	public static final String CTRL_ITEM_AGENTPOLLINGTIME = "AGENTPOLLINGTIME";
	public static final String CTRL_ITEM_WHITEIPALL = "WHITEIPALL";
	public static final String CTRL_ITEM_WHITEIPS = "WHITEIPS";
	public static final String CTRL_ITEM_USEHOMERESET = "USEHOMERESET";
	public static final String CTRL_ITEM_ROOTALLOW = "ROOTALLOW";
	public static final String CTRL_ITEM_SUDOALLOW = "SUDOALLOW";

	// JOB NAME - media rule
	public static final String JOB_MEDIA_RULE_CHANGE = "get_media_config";
	// JOB NAME - browser rule
	public static final String JOB_BROWSER_RULE_CHANGE = "get_browser_config";
	// JOB NAME - client configuration
	public static final String JOB_CLIENTSECU_SCREENTIME_CHANGE = "get_screen_time";
	public static final String JOB_CLIENTSECU_PASSWORDTIME_CHANGE = "get_password_cycle";
	public static final String JOB_CLIENTSECU_PACKAGEHANDLE_CHANGE = "get_update_operation";
	// JOB NAME - software filter
	public static final String JOB_FILTEREDSOFTWARE_RULE_CHANGE = "get_app_list";
	// JOB NAME - Controller Item filter
	public static final String JOB_CTRLCENTERITEMS_RULE_CHANGE = "get_controlcenter_items";
	// JOB NAME - Policy Kit
	public static final String JOB_POLICYKIT_RULE_CHANGE = "get_policykit_config";

	// JOB NAME - root / sudo aloow job
	public static final String JOB_ACCOUNT_RULE_CHANGE = "get_account_config";

	// JOB NAME - client configuration
	public static final String JOB_CLIENTCONF_AGENTPOLLING_CHANGE = "set_serverjob_dispatch_time_config";
	public static final String JOB_CLIENTCONF_HOSTS_CHANGE = "append_contents_etc_hosts";
	public static final String JOB_CLIENTCONF_HOMERESET_CHANGE = "set_homefolder_operation";
	public static final String JOB_CLIENTCONF_LOGCONFIG_CHANGE = "get_log_config";
	public static final String JOB_CLIENTCONF_POLICYKITUSER_CHANGE = "get_polkit_admin_config";

	// LOGGING CODE
	public static final String CODE_SYSERROR = "ERR9999";
	public static final String MSG_SYSERROR = "system.common.error";
	public static final String CODE_NODATA = "GRSM0004";

	public static final String CODE_SELECT = "GRSM0010";
	public static final String CODE_SELECTERROR = "GRSM3010";
	public static final String CODE_INSERT = "GRSM0011";
	public static final String CODE_INSERTERROR = "GRSM3011";
	public static final String CODE_DELETE = "GRSM0012";
	public static final String CODE_DELETEERROR = "GRSM3012";
	public static final String CODE_UPDATE = "GRSM0013";
	public static final String CODE_UPDATEERROR = "GRSM3013";

	public static final String CODE_DUPLICATE = "GRSM0014";
	public static final String CODE_NODUPLICATE = "GRSM3014";

	public static final String CODE_CREATE = "GRSM0015";
	public static final String CODE_SIGNERROR = "GRSM3016";

	public static final String CODE_EXIST = "GRSM0017";
	public static final String CODE_NOEXIST = "GRSM3017";

	// Root Dept Code
	public static final String DEFAULT_GROUPID = CTRL_CLIENT_GROUP_ABBR + MSG_DEFAULT;
	public static final String DEFAULT_DEPTCD = RULE_GRADE_DEPT + MSG_DEFAULT;

	// statistics.
	public static final String DEFAULT_VIOLATED_LOGTYPE = "취약";

	// user Request
	public static final String ACTION_REGISTERING = "registering";
	public static final String ACTION_REGISTERING_CANCEL = "registering-cancel";
	public static final String ACTION_UNREGISTERING = "unregistering";
	public static final String ACTION_REGISTER_APPROVAL = "register-approval";
	public static final String ACTION_REGISTER_DENY = "register-deny";
	public static final String ACTION_UNREGISTER_APPROVAL = "unregister-approval";
	public static final String ACTION_UNREGISTER_DENY = "unregister-deny";
	public static final String ACTION_APPROVAL = "approval";
	public static final String ACTION_DENY = "deny";
	public static final String ACTION_WAITING = "waiting";


}
