package org.wood.util;

import java.io.IOException;
import java.io.InputStream;

public class UtilOutGrabber extends Thread {

	private StringBuffer m_res = new StringBuffer(1024);

	private InputStream m_input;

	public UtilOutGrabber(InputStream t_input) {
		m_input = t_input;
	}

	@Override
	public void run() {
		int t_inChr;
		try {
			while ((t_inChr = m_input.read()) != -1) {
				m_res.append((char) t_inChr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public StringBuffer getM_res() {
		return m_res;
	}

}
