package org.x2bean.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.x2bean.util.Logger;
import org.x2bean.util.XMLUtil;
import org.x2bean.util.XMLUtilImp;

public class TestParse {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Get the XML
		ClassLoader classLoader = TestParse.class.getClassLoader();
		BufferedReader xmlReader = new BufferedReader(new InputStreamReader(
				classLoader.getResourceAsStream("org/x2bean/test/books.xml")));
		String xml = "";
		String temp = null;
		try {
			while ((temp = xmlReader.readLine()) != null) {
				xml += temp + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger.info("\n" + xml);

		// parse the XML

		XMLUtil xmlUtil = XMLUtilImp.getInstance();
		Object[] bookss = xmlUtil.parseObjects(xml, "Books",
				"org.x2bean.test.Books");
		// show the Object
		for (int i = 0; i < bookss.length; i++) {
			Books books = (Books) bookss[i];
			Logger.info("Books " + i);
			Logger.info("Books totalNum " + books.totalNum);
			for (int j = 0; j < books.book.length; j++) {
				Books.Book book = books.book[j];
				Logger.info("\t Book name  : " + book.bookName);
				Logger.info("\t Book author: " + book.author);
				Logger.info("\t Book price:  " + book.price);
				for (int k = 0; k < book.testArray.length; k++) {
					Logger.info("\t testArray: " + book.testArray[k]);
				}
				Logger.info("");
			}
		}
	}

}
