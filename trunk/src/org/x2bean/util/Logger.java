package org.x2bean.util;

/**
 * @author Bingbing Wang (maile: wangbb@live.cn)
 * 
 */
public class Logger {
    private static java.util.logging.Logger log = java.util.logging.Logger
	    .getLogger("X2Bean");

    public static void info(Object o) {
	log.log(java.util.logging.Level.INFO, o.toString());
    }

    public static void warn(Object o) {
	log.log(java.util.logging.Level.WARNING, o.toString());
    }

    public static void error(Object o) {
	if (o instanceof Exception) {
	    ((Exception) o).printStackTrace();
	}
	log.log(java.util.logging.Level.SEVERE, o.toString());
    }
}
