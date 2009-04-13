package org.x2bean.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.x2bean.util.XMLUtil;
import org.x2bean.util.XMLUtilImp;

public class TestParse {

	protected static Log log = LogFactory.getLog(TestParse.class);

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
				xml += temp + "\n" ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("\n" + xml);

		// parse the XML

		XMLUtil xmlUtil = XMLUtilImp.getInstance();
		Object[] bookss = xmlUtil.parseObjects(xml, "Books",
				"org.x2bean.test.Books");
		// show the Object
		for (int i = 0; i < bookss.length; i++) {
			Books books = (Books) bookss[i];
			log.info("Books " + i);
			log.info("Books totalNum " + books.totalNum);
			for (int j = 0; j < books.book.length; j++){
				Books.Book book = books.book[j];
				log.info("\t Book name  : "+book.bookName);
				log.info("\t Book author: "+book.author);
				log.info("\t Book price:  "+book.price);
				for (int k = 0; k < book.testArray.length; k++){
					log.info("\t testArray: "+book.testArray[k]);
				}
				log.info("");
			}
		}
	}

}
