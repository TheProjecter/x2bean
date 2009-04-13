package org.x2bean.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtilImp extends XMLUtil {

	/**
	 * The only instance of this utility
	 */
	protected static XMLUtil xmlUtil = null;
	protected static Log log = LogFactory.getLog(XMLUtilImp.class);

	/**
	 * Protected construction.
	 * So the only instance is promised.
	 */
	protected XMLUtilImp() {

	}

	/**
	 * Get this utility.
	 * @return The only utility
	 */
	public static XMLUtil getInstance() {
		if (xmlUtil == null) {
			xmlUtil = new XMLUtilImp();
		}
		return xmlUtil;
	}

	/**
	 * No implement yet.
	 */
	public Object parseObject(String xml, String objectName, String classType) {
		return null;
	}

	public Object[] parseObjects(String xml, String objectName, String classType) {
		// check the XML, get the results
		NodeList nodeList = initParse(xml, objectName);
		if (nodeList == null) {
			return null;
		}
		Object[] res = new Object[nodeList.getLength()];

		// create every object
		for (int i = 0; i < nodeList.getLength(); i++) {
			res[i] = _parseObject((Element) nodeList.item(i), classType);
		}

		return res;
	}

	/**
	 * Get all nodes, presented the top bean.
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public NodeList initParse(String xml, String objectName) {
		DocumentBuilderFactory builderFacotry = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder;
		try {
			builder = builderFacotry.newDocumentBuilder();
			ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
			Document document = builder.parse(input);
			NodeList nodeList = document.getElementsByTagName(objectName);
			if (nodeList.getLength() > 0) {
				return nodeList;
			} else {
				log
						.error("No Beans in this XML.\nMore information is given as follow:");
				log.info(xml);
			}
		} catch (ParserConfigurationException e) {
			log.error(e);
		} catch (SAXException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}

	/**
	 * In this method, mapping this node which present a bean with "classType".
	 * 
	 * @param node XML element present a bean
	 * @param classType the bean's class type
	 * @return the created bean
	 */
	@SuppressWarnings("unchecked")
	public Object _parseObject(Element node, String classType) {
		// initialize class and fields of the class
		Class objectClass = null;
		Field[] fields = null;
		try {
			objectClass = Class.forName(classType);
		} catch (ClassNotFoundException e) {
			log.error(e);
			return null;
		}
		fields = objectClass.getFields();

		// instance object
		Object res = null;
		try {
			res = objectClass.newInstance();
		} catch (InstantiationException e) {
			log.error(e);
			return null;
		} catch (IllegalAccessException e) {
			log.error(e);
			return null;
		}

		// evaluate fields' values
		for (int j = 0; j < fields.length; j++) {
			if (fields[j].getType().isPrimitive()) {// primitive
				parsePrimitive(node, fields[j], res);
			} else if (fields[j].getType().isArray()) {// array
				parseArray(node, fields[j], res);
			} else if (fields[j].getType().getName().contains("java.lang")) { // String
				parseOriginObject(node, fields[j], res);
			} else { // bean
				parseBean(node, fields[j], res);
			}
		}

		return res;
	}

	/**
	 * Get a field content in the node (present the bean), and set it to the bean (object).
	 * The field is a primitive data.
	 *  
	 * @param node XML element present a bean
	 * @param field field of the bean (Object).
	 * @param object the bean
	 */
	@SuppressWarnings("unchecked")
	public void parsePrimitive(Element node, Field field, Object object) {
		String fieldName = field.getName();
		Class type = field.getType();

		// Get element with fieldName
		NodeList fieldElements = node.getElementsByTagName(fieldName);
		if (fieldElements.getLength() > 0) {
			Element fieldElement = (Element) fieldElements.item(0);
			NodeList fieldContents = fieldElement.getChildNodes();
			for (int i = 0; i < fieldContents.getLength(); i++) {
				if (fieldContents.item(i).getNodeType() == Node.TEXT_NODE) {
					String content = fieldContents.item(i).getNodeValue();
					content = content.trim();

					try {
						if (type.getName().equalsIgnoreCase("boolean")) {
							boolean r = Boolean.parseBoolean(content);
							field.setBoolean(object, r);
						} else if (type.getName().equalsIgnoreCase("byte")) {
							byte r = Byte.parseByte(content);
							field.setByte(object, r);
						} else if (type.getName().equalsIgnoreCase("char")) {
							char r = content.charAt(0);
							field.setChar(object, r);
						} else if (type.getName().equalsIgnoreCase("double")) {
							double r = Double.parseDouble(content);
							field.setDouble(object, r);
						} else if (type.getName().equalsIgnoreCase("float")) {
							float r = Float.parseFloat(content);
							field.setFloat(object, r);
						} else if (type.getName().equalsIgnoreCase("int")) {
							int r = Integer.parseInt(content);
							field.setInt(object, r);
						} else if (type.getName().equalsIgnoreCase("long")) {
							long r = Long.parseLong(content);
							field.setLong(object, r);
						} else if (type.getName().equalsIgnoreCase("short")) {
							short r = Short.parseShort(content);
							field.setShort(object, r);
						}

					} catch (IllegalArgumentException e) {
						log.error(e);
					} catch (IllegalAccessException e) {
						log.error(e);
					}

				}
			}
		}
	}
	
	/**
	 * Get a field content in the node (present the bean), and set it to the bean (object).
	 * The field is a array.
	 *  
	 * @param node XML element present a bean
	 * @param field field of the bean (Object).
	 * @param object the bean
	 */
	@SuppressWarnings("unchecked")
	public void parseArray(Element node, Field field, Object object) {
		// Get fieldName and class type
		String fieldName = field.getName();
		Class type = field.getType();
		String subTypeName = type.getName();
		// Get correct class name
		// If primitive array, e.g. "[Z", it will deal in the catch code.
		// If Object array, e.g. "[Ljava.lang.String;", it will delete "[L" and ";".
		// If array array, e.g. "[[Z", it will delete "[".
		subTypeName = subTypeName.substring(1);
		if(subTypeName.charAt(0) == 'L'){
			subTypeName = subTypeName.substring(1,subTypeName.length()-1);
		}
		Class subType = null;
		try {
			subType = Class.forName(subTypeName);
		} catch (ClassNotFoundException e) {
			if(subTypeName.equals("Z")){
				subType = boolean.class;
			} else if(subTypeName.equals("B")){
				subType = byte.class;
			} else if(subTypeName.equals("C")) {
				subType = char.class;
			} else if(subTypeName.equals("D")) {
				subType = double.class;
			} else if(subTypeName.equals("F")) {
				subType = float.class;
			} else if(subTypeName.equals("I")) {
				subType = int.class;
			} else if(subTypeName.equals("J")) {
				subType = long.class;
			} else if(subTypeName.equals("S")) {
				subType = short.class;
			} else{
				log.error(e);
				return;
			}
		}

		// Get element with fieldName
		NodeList fieldElements = node.getElementsByTagName(fieldName);
		Object array = Array.newInstance(subType, fieldElements.getLength());
		for (int i = 0; i < fieldElements.getLength(); i++) {
			// evaluate fields' values
			if (subType.isPrimitive()) {// primitive
				parsePrimitiveArray((Element)fieldElements.item(i), subType, array, i);
			} else if (subType.isArray()) {// array
				parseArrayArray((Element)fieldElements.item(i), subType, array, i);
			} else if (subType.getName().contains("java.lang")) { // String
				parseOriginObjectArray((Element)fieldElements.item(i), subType, array, i);
			} else { // bean
				parseBeanArray((Element)fieldElements.item(i), subType, array, i);
			}
		}
		try {
			field.set(object, array);
		} catch (IllegalArgumentException e) {
			log.error(e);
		} catch (IllegalAccessException e) {
			log.error(e);
		}
	}
	

	/**
	 * Get a field content in the node (present the bean), and set it to the bean (object).
	 * The field is a original object, in the "java.lang" package.
	 *  
	 * @param node XML element present a bean
	 * @param field field of the bean (Object).
	 * @param object the bean
	 * @param node
	 * @param field
	 * @param object
	 */
	@SuppressWarnings("unchecked")
	public void parseOriginObject(Element node, Field field, Object object) {
		String fieldName = field.getName();
		Class type = field.getType();

		// Get element with fieldName
		NodeList fieldElements = node.getElementsByTagName(fieldName);
		if (fieldElements.getLength() > 0) {
			Element fieldElement = (Element) fieldElements.item(0);
			NodeList fieldContents = fieldElement.getChildNodes();
			for (int i = 0; i < fieldContents.getLength(); i++) {
				if (fieldContents.item(i).getNodeType() == Node.TEXT_NODE) {
					String content = fieldContents.item(i).getNodeValue();
					content = content.trim();
					// new a instance
					try {
						Constructor constructor = type.getConstructor(String.class);
						Object instance = constructor.newInstance(content);
						field.set(object, instance);	
					} catch (SecurityException e) {
						log.error(e);
						log.error("Not supported type: "+type.getName());
						log.error("More information:");
						log.error(node.getNodeName());
					} catch (NoSuchMethodException e) {
						log.error(e);
						log.error("Not supported type: "+type.getName());
						log.error("More information:");
						log.error(node.getNodeName());
					} catch (IllegalArgumentException e) {
						log.error(e);
					} catch (InstantiationException e) {
						log.error(e);
						log.error("Not supported type: "+type.getName());
						log.error("More information:");
						log.error(node.getNodeName());
					} catch (IllegalAccessException e) {
						log.error(e);
					} catch (InvocationTargetException e) {
						log.error(e);
						log.error("Not supported type: "+type.getName());
						log.error("More information:");
						log.error(node.getNodeName());
					}
				}
			}
		}
	}

	/**
	 * Get a field content in the node (present the bean), and set it to the bean (object).
	 * The field is also a bean.
	 *  
	 * @param node XML element present a bean
	 * @param field field of the bean (Object).
	 * @param object the bean
	 */
	@SuppressWarnings("unchecked")
	public void parseBean(Element node, Field field, Object object) {
		
		String fieldName = field.getName();
		Class fieldType = field.getClass();
		
		// Get element with fieldName
		NodeList fieldElements = node.getElementsByTagName(fieldName);
		if (fieldElements.getLength() > 0) {
			Element fieldElement = (Element) fieldElements.item(0);
			Object instance = _parseObject(fieldElement, fieldType.getName());
			try {
				field.set(object, instance);
			} catch (IllegalArgumentException e) {
				log.error(e);
			} catch (IllegalAccessException e) {
				log.error(e);
			}
		}
	}
	
	/**
	 * Mapping a node into a member in a array.
	 * The member in the array is a primitive data.
	 * 
	 * @param node The node present one member of this array.
	 * @param type The member's class type.
	 * @param array Array field in one bean.
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	public void parsePrimitiveArray(Element node, Class type, Object array,
			int index) {

		NodeList fieldContents = node.getChildNodes();
		for (int i = 0; i < fieldContents.getLength(); i++) {
			if (fieldContents.item(i).getNodeType() == Node.TEXT_NODE) {
				String content = fieldContents.item(i).getNodeValue();
				content = content.trim();

				try {
					if (type.getName().equalsIgnoreCase("boolean")) {
						boolean r = Boolean.parseBoolean(content);
						Array.setBoolean(array, index, r);
					} else if (type.getName().equalsIgnoreCase("byte")) {
						byte r = Byte.parseByte(content);
						Array.setByte(array, index, r);
					} else if (type.getName().equalsIgnoreCase("char")) {
						char r = content.charAt(0);
						Array.setChar(array, index, r);
					} else if (type.getName().equalsIgnoreCase("double")) {
						double r = Double.parseDouble(content);
						Array.setDouble(array, index, r);
					} else if (type.getName().equalsIgnoreCase("float")) {
						float r = Float.parseFloat(content);
						Array.setFloat(array, index, r);
					} else if (type.getName().equalsIgnoreCase("int")) {
						int r = Integer.parseInt(content);
						Array.setInt(array, index, r);
					} else if (type.getName().equalsIgnoreCase("long")) {
						long r = Long.parseLong(content);
						Array.setLong(array, index, r);
					} else if (type.getName().equalsIgnoreCase("short")) {
						short r = Short.parseShort(content);
						Array.setShort(array, index, r);
					} else {
						log.error("Not supported type: "+type.getName());
						log.error("More information:");
						log.error(node.getNodeName());
					}

				} catch (IllegalArgumentException e) {
					log.error(e);
				}
			}
		}
	}
	
	/**
	 * Mapping a node into a member in a array.
	 * The array is a multidimensional array.
	 * 
	 * @param node The node present one member of this array.
	 * @param type The member's class type.
	 * @param array Array field in one bean.
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	public void parseArrayArray(Element node, Class type, Object array, int index){
		
		log.warn("Multidimensional array is not supported in this version.");
	}

	/**
	 * Mapping a node into a member in a array.
	 * The member in the array is a original object in "java.lang" package.
	 * 
	 * @param node The node present one member of this array.
	 * @param type The member's class type.
	 * @param array Array field in one bean.
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	public void parseOriginObjectArray(Element node, Class type, Object array, int index) {

		NodeList fieldContents = node.getChildNodes();
		for (int i = 0; i < fieldContents.getLength(); i++) {
			if (fieldContents.item(i).getNodeType() == Node.TEXT_NODE) {
				String content = fieldContents.item(i).getNodeValue();
				content = content.trim();

				// new a instance
				try {
					Constructor constructor = type.getConstructor(String.class);
					Object instance = constructor.newInstance(content);
					// insert the instance into the array
					Array.set(array, index, instance);
				} catch (SecurityException e) {
					log.error(e);
					log.error("Not supported type: "+type.getName());
					log.error("More information:");
					log.error(node.getNodeName());
				} catch (NoSuchMethodException e) {
					log.error(e);
					log.error("Not supported type: "+type.getName());
					log.error("More information:");
					log.error(node.getNodeName());
				} catch (IllegalArgumentException e) {
					log.error(e);
				} catch (InstantiationException e) {
					log.error(e);
					log.error("Not supported type: "+type.getName());
					log.error("More information:");
					log.error(node.getNodeName());
				} catch (IllegalAccessException e) {
					log.error(e);
				} catch (InvocationTargetException e) {
					log.error(e);
					log.error("Not supported type: "+type.getName());
					log.error("More information:");
					log.error(node.getNodeName());
				}
			}
		}
	}
	
	/**
	 * Mapping a node into a member in a array.
	 * The member in the array is also a bean.
	 * 
	 * @param node The node present one member of this array.
	 * @param type The member's class type.
	 * @param array Array field in one bean.
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	public void parseBeanArray(Element node, Class type, Object array, int index) {
		String classType = type.getName();
		Object instance = _parseObject(node, classType);
		Array.set(array, index, instance);
	}

}
