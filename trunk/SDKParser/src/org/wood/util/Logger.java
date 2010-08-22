package org.wood.util;

public class Logger {
	public static void info(Object msg) {
		System.out.println(msg);
	}

	public static void error(Object msg) {
		System.err.println(msg);
	}
}
