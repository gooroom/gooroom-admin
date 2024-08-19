package kr.gooroom.gpms.common.utils;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.config.service.CtrlItemVO;
import kr.gooroom.gpms.config.service.CtrlPropVO;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CommonUtils {

	private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

	public static Date covertDateForEndTime(Date paramDate) {

		if (paramDate != null) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(paramDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);

			return cal.getTime();

		} else {
			return null;
		}
	}

	public static boolean validationDate(String date) {
		if(date.length() != 8) {
			return false;
		}

		try {
			yyyyMMdd.parse(date);
			return true;
		} catch (ParseException ex) {
			return false;
		}
	}

	public static String convertDataToString(Date date) {
		if(date != null) {
			return yyyyMMdd.format(date);
		}
		return "";
	}

	public static Date yyyyMMddToDate(String dateStr) {
		try {
			if(!ObjectUtils.isEmpty(yyyyMMdd)) {
				return yyyyMMdd.parse(dateStr);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * get property value by property name
	 * 
	 * @return string property value
	 *
	 */
	public static String getViewStatusForClient(String status, String isOn, String isViolated) {
		if (GPMSConstants.STS_REVOKED.equals(status)) {
			// revoke
			return GPMSConstants.STS_VIEW_REVOKE;
		} else if ("0".equals(isOn)) {
			// offiline
			return GPMSConstants.STS_VIEW_OFFLINE;
		} else if (!"0".equals(isViolated)) {
			// violated
			return GPMSConstants.STS_VIEW_VIOLATED;
		} else {
			// normal(on)
			return GPMSConstants.STS_VIEW_NORMAL;
		}
	}

	/**
	 * get property value by property name
	 * 
	 * @param vo       CtrlItemVO property item
	 * @param propName string property name
	 * @return string property value
	 *
	 */
	public static String getPropertyValue(CtrlItemVO vo, String propName) {

		if (vo != null && propName != null) {
			ArrayList<CtrlPropVO> props = vo.getPropList();
			if (props != null && props.size() > 0) {
				for (CtrlPropVO prop : props) {
					if (propName.equals(prop.getPropNm())) {
						return prop.getPropValue();
					}
				}
			}
		}

		return "";
	}

	/**
	 * get property values (string array) by property name
	 * 
	 * @param vo       CtrlItemVO property item
	 * @param propName string property name
	 * @return string array property values
	 *
	 */
	public static ArrayList<String> getPropertyValues(CtrlItemVO vo, String propName) {
		ArrayList<String> values = new ArrayList<>();

		if (vo != null && propName != null) {
			ArrayList<CtrlPropVO> props = vo.getPropList();
			if (props != null && props.size() > 0) {
				for (CtrlPropVO prop : props) {
					if (propName.equals(prop.getPropNm())) {
						values.add(prop.getPropValue());
					}
				}
			}
		}

		return values;
	}

	/**
	 * check if value array has same value
	 * 
	 * @return boolean true if ntp addresses was same data
	 *
	 */
	public static boolean isEqualArrayProperties(String[] propertyArray, CtrlItemVO vo, String propertyName) {

		if (vo != null) {
			ArrayList<String> values = getPropertyValues(vo, propertyName);

			if (values != null && values.size() > 0) {

				if (propertyArray == null || propertyArray.length < 1) {
					return false;
				}

				if (propertyArray.length != values.size()) {
					return false;
				}

				for (String value : values) {
					boolean isequal = false;
					for (String prop : propertyArray) {
						if (value != null && value.equals(prop)) {
							isequal = true;
							break;
						}
					}
					if (!isequal) {
						return false;
					}
				}
				return true;

			} else {
				return false;
			}

		} else {

			return true;
		}
	}

	/**
	 * create context address for Images (icon...).
	 * 
	 * @return string context url.
	 *
	 */
	public static String createContextAddress() {

		return GPMSConstants.ICON_SERVER_PROTOCOL + "://" +
				GPMSConstants.ICON_SERVERPATH + "/";
	}

	/**
	 * create context address for Images (icon...).
	 * 
	 * @return string context url.
	 *
	 */
	public static String createIconUrlPath() {

		return GPMSConstants.ICON_SERVER_PROTOCOL + "://" +
				GPMSConstants.ICON_SERVERPATH + "/" +
				GPMSConstants.PATH_FOR_ICONURL + "/";
	}
	
	/**
	  * MyBatis String endswith condition
	  * 
	  * @return Boolean : true / false
	  */
	 public static Boolean endsWith(Object param1, Object param2) {
		 if(param1.getClass().equals(String.class) && param2.getClass().equals(String.class)) {
			 return ((String)param1).endsWith((String)param2);
		 } else {
			 return Boolean.FALSE;
		 }
	 }

	/**
	 * get ctrl item type(software)
	 * @param propValue String
	 * @return Boolean : String
	 */
	public static String getCtrlItemType(String propValue) {
		switch(propValue) {
			case GPMSConstants.CHROMIUM_WEB_BROWSER : return GPMSConstants.CTRL_ITEM_NETWORK;
			case GPMSConstants.GOOROOM_WEB_BROWSER : return GPMSConstants.CTRL_ITEM_NETWORK;
			case GPMSConstants.GNOME_CONTROL_CENTER : return GPMSConstants.CTRL_ITEM_SYSTEM;
			case GPMSConstants.GRAC_EDITOR : return GPMSConstants.CTRL_ITEM_SYSTEM;
			case GPMSConstants.SOFTWARE : return GPMSConstants.CTRL_ITEM_SYSTEM;
			case GPMSConstants.TERMINAL : return GPMSConstants.CTRL_ITEM_SYSTEM;
			case GPMSConstants.SYNAPTIC_PACKAGE_MANAGER : return GPMSConstants.CTRL_ITEM_SYSTEM;
			case GPMSConstants.CALCULATOR : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.GOOROOM_MANAGEMENT_SETTINGS : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.GOOROOM_SECURITY_STATUS_VIEW : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.GOOROOM_TOOLKIT : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.HANCOM_OFFICE_HWP_2014_VIEWER : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.ARCHIVE_MANAGER : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.FILES : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.SCRATCH_3_0 : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.SCREENSHOT : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.VEYON_MASTER : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.BLUETOOTH_MANAGER : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.MOUSEPAD : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.GOOROOM_GUIDE : return GPMSConstants.CTRL_ITEM_UTILITY;
			case GPMSConstants.IMAGE_VIEWER : return GPMSConstants.CTRL_ITEM_GRAPHICS;
			case GPMSConstants.VIDEOS : return GPMSConstants.CTRL_ITEM_AUDIO_VIDEO;
			default : return "";
		}
	}
}
