package org.wood.util;

import java.io.File;

public class FileUtil {

	static protected String m_tempPath;

	static public String getTempFile() {

		if (m_tempPath != null) {
			return m_tempPath;
		}

		m_tempPath = System.getenv("TEMP");
		if (m_tempPath == null) {
			m_tempPath = System.getenv("TEMP");
		}
		if (m_tempPath == null) {
			if (File.listRoots().length > 0) {
				m_tempPath = File.listRoots()[0].toString();
			}
		}
		if (m_tempPath == null) {
			m_tempPath = ".";
		}

		m_tempPath += "\\";

		return m_tempPath;
	}
}
