package org.x2bean.util;

import java.io.File;
import java.io.InputStream;

/**
 * 
 * @author woody(wangbb@live.cn)
 * 
 */
public abstract class XMLUtil {

	/**
	 * Parse the XML, bean contained in the XML data is returned. If error
	 * occurs, the return is null.
	 * 
	 * @param xml
	 * @param objectName
	 * @return the bean
	 */
	public abstract Object parseObject(String xml, String objectName,
			String classType);

	/**
	 * Parse the XML, beans contained in the XML data are returned. If error
	 * occurs, the return is null.
	 * 
	 * @param xml
	 * @param objectName
	 * @return beans
	 */
	public abstract Object[] parseObjects(String xml, String objectName,
			String classType);

	/**
	 * Parse the XML file, bean contained in the XML data is returned. If error
	 * occurs, the return is null.
	 * 
	 * @param input
	 * @param objectName
	 * @return the bean
	 */
	public abstract Object parseObject(InputStream input, String objectName,
			String classType);

	/**
	 * Parse the XML file, beans contained in the XML data are returned. If
	 * error occurs, the return is null.
	 * 
	 * @param input
	 * @param objectName
	 * @return beans
	 */
	public abstract Object[] parseObjects(InputStream input, String objectName,
			String classType);

}
