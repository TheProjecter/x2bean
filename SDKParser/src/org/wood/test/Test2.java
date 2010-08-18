package org.wood.test;

import java.io.IOException;

import org.wood.util.AnalyUtil;

public class Test2 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String[] a = new String[2];
		a[0] = "aaa";
		a[1] = "bbb";
		AnalyUtil.initCfgFile(a);
	}

}
