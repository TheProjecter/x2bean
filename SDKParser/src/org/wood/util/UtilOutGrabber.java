package org.wood.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UtilOutGrabber extends Thread {

	public enum TYPE {
		ERROR, NORMAL
	};

	private InputStream m_input;

	private TYPE m_type;

	public UtilOutGrabber(InputStream input, TYPE type) {
		m_input = input;
		m_type = type;
	}

	public UtilOutGrabber(InputStream input) {
		m_input = input;
		m_type = TYPE.NORMAL;
	}

	@Override
	public void run() {
		String t_temp;
		BufferedReader t_reader = new BufferedReader(new InputStreamReader(
				m_input));
		try {
			while ((t_temp = t_reader.readLine()) != null) {
				if (m_type == TYPE.ERROR) {
					Logger.error(t_temp);
				} else {
					Logger.info(t_temp);
				}
			}
		} catch (IOException e) {
			Logger.error(e);
		}
	}

}
