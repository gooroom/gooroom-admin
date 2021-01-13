package kr.gooroom.gpms.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.config.service.CtrlItemVO;
import kr.gooroom.gpms.config.service.CtrlPropVO;
import org.springframework.util.StringUtils;

public class CommonUtils {

	private static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

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
			if(!StringUtils.isEmpty(yyyyMMdd)) {
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
	 * @param vo       CtrlItemVO property item
	 * @param propName string property name
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
		ArrayList<String> values = new ArrayList<String>();

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
	 * @param ntpAddresses string array ntp addresses.
	 * @param vo           CtrlItemVO property item
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
	 * @param req HttpServletRequest
	 * @return string context url.
	 *
	 */
	public static String createContextAddress() {
		StringBuffer sb = new StringBuffer();

		sb.append(GPMSConstants.ICON_SERVER_PROTOCOL).append("://");
		sb.append(GPMSConstants.ICON_SERVERPATH).append("/");

		return sb.toString();
	}

	/**
	 * create context address for Images (icon...).
	 * 
	 * @param req HttpServletRequest
	 * @return string context url.
	 *
	 */
	public static String createIconUrlPath() {
		StringBuffer sb = new StringBuffer();

		sb.append(GPMSConstants.ICON_SERVER_PROTOCOL).append("://");
		sb.append(GPMSConstants.ICON_SERVERPATH).append("/");
		sb.append(GPMSConstants.PATH_FOR_ICONURL).append("/");

		return sb.toString();
	}
	
	/**
	  * MyBatis String endswith condition
	  * 
	  * @param 
	  * @return Boolean : true / false
	  */
	 public static Boolean endsWith(Object param1, Object param2) {
		 if(param1.getClass().equals(String.class) && param2.getClass().equals(String.class)) {
			 return ((String)param1).endsWith((String)param2);
		 } else {
			 return Boolean.FALSE;
		 }
	 }
}
