package org.wood.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.wood.util.FileUtil;
import org.wood.util.UtilOutGrabber;

public class Test1 {

	/**
	 * @param args
	 * @throws IOException
	 * @throws IOException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {

		String t_cmdFileName = FileUtil.getTempFile() + "doxygen.exe";
		File t_cmdFile = new File(t_cmdFileName);
		byte[] t_inChr = new byte[1024];
		int t_inLen;
		if (!t_cmdFile.exists()) {
			DataInputStream t_cmdFileIn = new DataInputStream(Test1.class
					.getClassLoader().getResourceAsStream("utils/doxygen.exe"));
			t_cmdFile.createNewFile();
			DataOutputStream t_cmdFileOut = new DataOutputStream(
					new FileOutputStream(t_cmdFile));
			while (true) {
				if ((t_inLen = t_cmdFileIn.read(t_inChr)) <= 0) {
					break;
				}
				t_cmdFileOut.write(t_inChr, 0, t_inLen);
			}
			t_cmdFileIn.close();
			t_cmdFileOut.close();
			t_cmdFileIn = null;
			t_cmdFileOut = null;
		}

		String t_cfgFileName = FileUtil.getTempFile() + "config.txt";
		// get config file
		{
			DataInputStream t_cfgFileIn = new DataInputStream(Test1.class
					.getClassLoader().getResourceAsStream("utils/config.txt"));
			File t_cfgFile = new File(t_cfgFileName);
			t_cfgFile.delete();
			t_cfgFile.createNewFile();
			DataOutputStream t_cfgFileOut = new DataOutputStream(
					new FileOutputStream(t_cfgFile));
			while (true) {
				if ((t_inLen = t_cfgFileIn.read(t_inChr)) <= 0) {
					break;
				}
				t_cfgFileOut.write(t_inChr, 0, t_inLen);
			}
			t_cfgFileIn.close();
			t_cfgFileOut.close();
			t_cfgFileIn = null;
			t_cfgFileOut = null;
		}
		
		// run "doxygen"
		String[] t_commands = new String[2];
		t_commands[0] = t_cmdFileName;
		System.out.println(t_cmdFileName);
		t_commands[1] = t_cfgFileName;
		System.out.println(t_cfgFileName);
		Process t_process = Runtime.getRuntime().exec(t_commands, null,
				new File(FileUtil.getTempFile()));
		DataInputStream t_execOut = new DataInputStream(t_process
				.getErrorStream());
		UtilOutGrabber errGrabber = new UtilOutGrabber(t_execOut);
		t_execOut = new DataInputStream(t_process.getInputStream());
		UtilOutGrabber standGrabber = new UtilOutGrabber(t_execOut);
		errGrabber.start();
		standGrabber.start();
		System.out.println(t_process.waitFor());

		System.out.println(errGrabber.getM_res());
		System.out.println(standGrabber.getM_res());
	}
}
