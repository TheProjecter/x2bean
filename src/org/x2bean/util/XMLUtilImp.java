package org.x2bean.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author woody (wangbb@live.cn)
 * 
 */
public class XMLUtilImp extends XMLUtil {

	/**
	 * The only instance of this utility
	 */
	protected static XMLUtil xmlUtil = null;

	/**
	 * Protected construction. So the only instance is promised.
	 */
	protected XMLUtilImp() {

	}

	/**
	 * Get this utility.
	 * 
	 * @return The only utility
	 */
	public static XMLUtil getInstance() {
		if (xmlUtil == null) {
			xmlUtil = new XMLUtilImp();
		}
		return xmlUtil;
	}

	@Override
	public Object parseObject(String xml, String objectName, String classType) {
		// check the XML, get the results
		Node[] nodes = initParse(xml, objectName);
		if (nodes == null || nodes.length == 0) {
			return null;
		}
		return _parseObject((Element) nodes[0], classType);
	}

	@Override
	public Object[] parseObjects(String xml, String objectName, String classType) {
		// check the XML, get the results
		Node[] nodes = initParse(xml, objectName);
		if (nodes == null) {
			return null;
		}
		Object[] res = new Object[nodes.length];

		// create every object
		for (int i = 0; i < nodes.length; i++) {
			res[i] = _parseObject((Element) nodes[i], classType);
		}

		return res;
	}

	@Override
	public Object parseObject(InputStream input, String objectName,
			String classType) {
		// check the XML, get the results
		Node[] nodes = initParse(input, objectName);
		if (nodes == null || nodes.length == 0) {
			return null;
		}
		return _parseObject((Element) nodes[0], classType);
	}

	@Override
	public Object[] parseObjects(InputStream input, String objectName,
			String classType) {
		// check the XML, get the results
		Node[] nodes = initParse(input, objectName);
		if (nodes == null) {
			return null;
		}
		Object[] res = new Object[nodes.length];

		// create every object
		for (int i = 0; i < nodes.length; i++) {
			res[i] = _parseObject((Element) nodes[i], classType);
		}

		return res;
	}

	/**
	 * Get all nodes, presented the top bean.
	 * 
	 * @param xml
	 * @return Nodes according to the object name
	 * @throws Exception
	 */
	public Node[] initParse(String xml, String objectName) {
		if (xml == null) {
			return null;
		}
		ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
		return _initParse(input, objectName);

	}

	/**
	 * Get all nodes, presented the top bean.
	 * 
	 * @param xml
	 * @return Nodes according to the object name
	 * @throws Exception
	 */
	public Node[] initParse(InputStream input, String objectName) {
		if (input == null) {
			return null;
		}
		return _initParse(input, objectName);
	}

	/**
	 * Get all nodes, presented the top bean.
	 * 
	 * @param xml
	 * @return Nodes according to the object name
	 * @throws Exception
	 */
	public Node[] _initParse(InputStream input, String objectName) {
		DocumentBuilderFactory builderFacotry = DocumentBuilderFactory
				.newInstance();
		if (input == null) {
			return null;
		}
		DocumentBuilder builder;
		try {
			builder = builderFacotry.newDocumentBuilder();
			Document document = builder.parse(input);
			NodeList roots = document.getChildNodes();
			HashSet<Node> nodes = new HashSet<Node>();
			for (int i = 0; i < roots.getLength(); i++) {
				if (roots.item(i).getNodeName().equalsIgnoreCase(objectName)) {
					nodes.add(roots.item(i));
				}
			}
			if (nodes.size() > 0) {
				return nodes.toArray(new Node[0]);
			} else {
				Logger
						.error("No Beans in this XML.\nMore information is given as follow:");
			}
		} catch (ParserConfigurationException e) {
			Logger.error(e);
		} catch (SAXException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		}
		return null;
	}

	/**
	 * In this method, mapping this node which present a bean with "classType".
	 * 
	 * @param node
	 *            XML element present a bean
	 * @param classType
	 *            the bean's class type
	 * @return the created bean
	 */
	@SuppressWarnings("unchecked")
	public Object _parseObject(Node node, String classType) {
		// initialize class and fields of the class
		Class objectClass = null;
		Field[] fields = null;
		try {
			objectClass = Class.forName(classType);
		} catch (ClassNotFoundException e) {
			Logger.error(e);
			return null;
		}
		fields = objectClass.getFields();

		// instance object
		Object res = null;
		try {
			res = objectClass.newInstance();
		} catch (InstantiationException e) {
			Logger.error(e);
			return null;
		} catch (IllegalAccessException e) {
			Logger.error(e);
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
	 * Get a field content in the node (present the bean), and set it to the
	 * bean (object). The field is a primitive data.
	 * 
	 * @param node
	 *            XML element present a bean
	 * @param field
	 *            field of the bean (Object).
	 * @param object
	 *            the bean
	 */
	@SuppressWarnings("unchecked")
	public void parsePrimitive(Node node, Field field, Object object) {
		String fieldName = field.getName();
		Class type = field.getType();

		// Get element with fieldName
		HashSet<Node> childrenSet = new HashSet<Node>();
		NodeList childrenNode = node.getChildNodes();
		for (int i = 0; i < childrenNode.getLength(); i++) {
			childrenSet.add(childrenNode.item(i));
		}
		NamedNodeMap childrenAttr = node.getAttributes();
		for (int i = 0; i < childrenAttr.getLength(); i++) {
			childrenSet.add(childrenAttr.item(i));
		}
		Node[] children = childrenSet.toArray(new Node[0]);
		for (int i = 0; i < children.length; i++) {
			if (children[i].getNodeName().equalsIgnoreCase(fieldName)) {
				Node fieldNode = children[i];
				String content = null;
				if (fieldNode.getNodeType() == Node.ATTRIBUTE_NODE) {
					content = fieldNode.getNodeValue();
				} else {
					NodeList fieldContents = fieldNode.getChildNodes();
					for (int j = 0; j < fieldContents.getLength(); j++) {
						if (fieldContents.item(j).getNodeType() == Node.TEXT_NODE) {
							content = fieldContents.item(j).getNodeValue();
							content = content.trim();
							break;
						}
					}
				}
				if (content != null) {
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
						Logger.error(e);
					} catch (IllegalAccessException e) {
						Logger.error(e);
					}
				}
			}
		}
	}

	/**
	 * Get a field content in the node (present the bean), and set it to the
	 * bean (object). The field is a array.
	 * 
	 * @param node
	 *            XML element present a bean
	 * @param field
	 *            field of the bean (Object).
	 * @param object
	 *            the bean
	 */
	@SuppressWarnings("unchecked")
	public void parseArray(Node node, Field field, Object object) {
		// Get fieldName and class type
		String fieldName = field.getName();
		Class type = field.getType();
		String subTypeName = type.getName();
		// Get correct class name
		// If primitive array, e.g. "[Z", it will deal in the catch code.
		// If Object array, e.g. "[Ljava.lang.String;", it will delete "[L" and
		// ";".
		// If array array, e.g. "[[Z", it will delete "[".
		subTypeName = subTypeName.substring(1);
		if (subTypeName.charAt(0) == 'L') {
			subTypeName = subTypeName.substring(1, subTypeName.length() - 1);
		}
		Class subType = null;
		try {
			subType = Class.forName(subTypeName);
		} catch (ClassNotFoundException e) {
			if (subTypeName.equals("Z")) {
				subType = boolean.class;
			} else if (subTypeName.equals("B")) {
				subType = byte.class;
			} else if (subTypeName.equals("C")) {
				subType = char.class;
			} else if (subTypeName.equals("D")) {
				subType = double.class;
			} else if (subTypeName.equals("F")) {
				subType = float.class;
			} else if (subTypeName.equals("I")) {
				subType = int.class;
			} else if (subTypeName.equals("J")) {
				subType = long.class;
			} else if (subTypeName.equals("S")) {
				subType = short.class;
			} else {
				Logger.error(e);
				return;
			}
		}

		// Get element with fieldName
		NodeList children = node.getChildNodes();
		HashSet<Node> childNodes = new HashSet<Node>();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equalsIgnoreCase(fieldName)) {
				childNodes.add(children.item(i));
			}
		}
		Node[] fields = childNodes.toArray(new Node[0]);
		Object array = Array.newInstance(subType, fields.length);
		for (int i = 0; i < fields.length; i++) {
			// evaluate fields' values
			if (subType.isPrimitive()) {// primitive
				parsePrimitiveArray(fields[i], subType, array, i);
			} else if (subType.isArray()) {// array
				parseArrayArray(fields[i], subType, array, i);
			} else if (subType.getName().contains("java.lang")) { // String
				parseOriginObjectArray(fields[i], subType, array, i);
			} else { // bean
				parseBeanArray(fields[i], subType, array, i);
			}
		}
		try {
			field.set(object, array);
		} catch (IllegalArgumentException e) {
			Logger.error(e);
		} catch (IllegalAccessException e) {
			Logger.error(e);
		}
	}

	/**
	 * Get a field content in the node (present the bean), and set it to the
	 * bean (object). The field is a original object, in the "java.lang"
	 * package.
	 * 
	 * @param node
	 *            XML element present a bean
	 * @param field
	 *            field of the bean (Object).
	 * @param object
	 *            the bean
	 */
	@SuppressWarnings("unchecked")
	public void parseOriginObject(Node node, Field field, Object object) {
		String fieldName = field.getName();
		Class type = field.getType();

		// Get element with fieldName
		HashSet<Node> childrenSet = new HashSet<Node>();
		NodeList childrenNode = node.getChildNodes();
		for (int i = 0; i < childrenNode.getLength(); i++) {
			childrenSet.add(childrenNode.item(i));
		}
		NamedNodeMap childrenAttr = node.getAttributes();
		for (int i = 0; i < childrenAttr.getLength(); i++) {
			childrenSet.add(childrenAttr.item(i));
		}
		Node[] children = childrenSet.toArray(new Node[0]);

		for (int j = 0; j < children.length; j++) {
			if (children[j].getNodeName().equalsIgnoreCase(fieldName)) {
				Node fieldNode = children[j];
				String content = null;
				if (fieldNode.getNodeType() == Node.ATTRIBUTE_NODE) {
					content = fieldNode.getNodeValue();
				} else {
					NodeList fieldContents = fieldNode.getChildNodes();
					for (int i = 0; i < fieldContents.getLength(); i++) {
						if (fieldContents.item(i).getNodeType() == Node.TEXT_NODE) {
							content = fieldContents.item(i).getNodeValue();
							content = content.trim();
							break;
						}
					}
				}
				if (content != null) {
					// new a instance
					try {
						Constructor constructor = type
								.getConstructor(String.class);
						Object instance = constructor.newInstance(content);
						field.set(object, instance);
					} catch (SecurityException e) {
						Logger.error(e);
						Logger.error("Not supported type: " + type.getName());
						Logger.error("More information:");
						Logger.error(node.getNodeName());
					} catch (NoSuchMethodException e) {
						Logger.error(e);
						Logger.error("Not supported type: " + type.getName());
						Logger.error("More information:");
						Logger.error(node.getNodeName());
					} catch (IllegalArgumentException e) {
						Logger.error(e);
					} catch (InstantiationException e) {
						Logger.error(e);
						Logger.error("Not supported type: " + type.getName());
						Logger.error("More information:");
						Logger.error(node.getNodeName());
					} catch (IllegalAccessException e) {
						Logger.error(e);
					} catch (InvocationTargetException e) {
						Logger.error(e);
						Logger.error("Not supported type: " + type.getName());
						Logger.error("More information:");
						Logger.error(node.getNodeName());
					}
				}
			}
		}
	}

	/**
	 * Get a field content in the node (present the bean), and set it to the
	 * bean (object). The field is also a bean.
	 * 
	 * @param node
	 *            XML element present a bean
	 * @param field
	 *            field of the bean (Object).
	 * @param object
	 *            the bean
	 */
	@SuppressWarnings("unchecked")
	public void parseBean(Node node, Field field, Object object) {

		String fieldName = field.getName();
		Class fieldType = field.getType();

		// Get element with fieldName
		NodeList fieldElements = node.getChildNodes();
		for (int i = 0; i < fieldElements.getLength(); i++) {
			if (fieldElements.item(i).getNodeName().equalsIgnoreCase(fieldName)) {
				Node fieldElement = fieldElements.item(i);
				Object instance = _parseObject(fieldElement, fieldType
						.getName());
				try {
					field.set(object, instance);
				} catch (IllegalArgumentException e) {
					Logger.error(e);
				} catch (IllegalAccessException e) {
					Logger.error(e);
				}
			}
		}
	}

	/**
	 * Mapping a node into a member in a array. The member in the array is a
	 * primitive data.
	 * 
	 * @param node
	 *            The node present one member of this array.
	 * @param type
	 *            The member's class type.
	 * @param array
	 *            Array field in one bean.
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	public void parsePrimitiveArray(Node node, Class type, Object array,
			int index) {

		String content = null;
		if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
			content = node.getNodeValue();
		} else {
			NodeList fieldContents = node.getChildNodes();
			for (int j = 0; j < fieldContents.getLength(); j++) {
				if (fieldContents.item(j).getNodeType() == Node.TEXT_NODE) {
					content = fieldContents.item(j).getNodeValue();
					content = content.trim();
					break;
				}
			}
		}
		if (content != null) {

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
					Logger.error("Not supported type: " + type.getName());
					Logger.error("More information:");
					Logger.error(node.getNodeName());
				}

			} catch (IllegalArgumentException e) {
				Logger.error(e);
			}
		}
	}

	/**
	 * Mapping a node into a member in a array. The array is a multidimensional
	 * array.
	 * 
	 * @param node
	 *            The node present one member of this array.
	 * @param type
	 *            The member's class type.
	 * @param array
	 *            Array field in one bean.
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	public void parseArrayArray(Node node, Class type, Object array, int index) {

		Logger.warn("Multidimensional array is not supported in this version.");
	}

	/**
	 * Mapping a node into a member in a array. The member in the array is a
	 * original object in "java.lang" package.
	 * 
	 * @param node
	 *            The node present one member of this array.
	 * @param type
	 *            The member's class type.
	 * @param array
	 *            Array field in one bean.
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	public void parseOriginObjectArray(Node node, Class type, Object array,
			int index) {

		String content = null;
		if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
			content = node.getNodeValue();
		} else {
			NodeList fieldContents = node.getChildNodes();
			for (int j = 0; j < fieldContents.getLength(); j++) {
				if (fieldContents.item(j).getNodeType() == Node.TEXT_NODE) {
					content = fieldContents.item(j).getNodeValue();
					content = content.trim();
					break;
				}
			}
		}
		if (content != null) {

			// new a instance
			try {
				Constructor constructor = type.getConstructor(String.class);
				Object instance = constructor.newInstance(content);
				// insert the instance into the array
				Array.set(array, index, instance);
			} catch (SecurityException e) {
				Logger.error(e);
				Logger.error("Not supported type: " + type.getName());
				Logger.error("More information:");
				Logger.error(node.getNodeName());
			} catch (NoSuchMethodException e) {
				Logger.error(e);
				Logger.error("Not supported type: " + type.getName());
				Logger.error("More information:");
				Logger.error(node.getNodeName());
			} catch (IllegalArgumentException e) {
				Logger.error(e);
			} catch (InstantiationException e) {
				Logger.error(e);
				Logger.error("Not supported type: " + type.getName());
				Logger.error("More information:");
				Logger.error(node.getNodeName());
			} catch (IllegalAccessException e) {
				Logger.error(e);
			} catch (InvocationTargetException e) {
				Logger.error(e);
				Logger.error("Not supported type: " + type.getName());
				Logger.error("More information:");
				Logger.error(node.getNodeName());
			}
		}
	}

	/**
	 * Mapping a node into a member in a array. The member in the array is also
	 * a bean.
	 * 
	 * @param node
	 *            The node present one member of this array.
	 * @param type
	 *            The member's class type.
	 * @param array
	 *            Array field in one bean.
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	public void parseBeanArray(Node node, Class type, Object array, int index) {
		String classType = type.getName();
		Object instance = _parseObject(node, classType);
		Array.set(array, index, instance);
	}

}
