package org.x2bean.util;

public abstract class XMLUtil {
	
	/**
	 * Parse the XML, bean contained in the XML data is returned.
	 * If error occurs, the return is null.
	 * 
	 * @param xml
	 * @param objectName
	 * @return
	 */
	public abstract Object parseObject(String xml, String objectName, String classType);
	
	/**
	 * Parse the XML, beans contained in the XML data are returned.
	 * If error occurs, the return is null.
	 * 
	 * @param xml
	 * @param objectName
	 * @return
	 */
	public abstract Object[] parseObjects(String xml, String objectName, String classType);
	
	
	
}
