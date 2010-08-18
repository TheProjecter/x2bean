package org.wood.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.wood.test.Test1;

public class AnalyUtil {

	public static void initialUtil() throws IOException {
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
	}

	public static void initCfgFile(String[] fileLists) throws IOException {
		String t_xmlFile = FileUtil.getTempFile() + "xml";
		File t_xmlDir = new File(t_xmlFile);
		if (t_xmlDir.exists()) {
			File[] t_subFiles = t_xmlDir.listFiles();
			for (File t_tempFile : t_subFiles) {
				t_tempFile.delete();
			}
			t_xmlDir.delete();
		}

		String t_cfgFileName = FileUtil.getTempFile() + "config.txt";
		int t_lineLen = 0;
		String t_inLine;
		// get config file
		{
			BufferedReader t_cfgFileIn = new BufferedReader(
					new InputStreamReader(Test1.class.getClassLoader()
							.getResourceAsStream("utils/config.txt")));
			File t_cfgFile = new File(t_cfgFileName);
			t_cfgFile.delete();
			t_cfgFile.createNewFile();
			BufferedWriter t_cfgFileOut = new BufferedWriter(new FileWriter(
					t_cfgFile));

			while ((t_inLine = t_cfgFileIn.readLine()) != null) {
				t_lineLen++;
				t_cfgFileOut.write(t_inLine);
				// add file lists
				if (t_lineLen == 102) {
					for (int i = 0; i < fileLists.length; i++) {
						if (i != 0) {
							t_cfgFileOut.write("\\\r\n");
						}
						t_cfgFileOut.write(fileLists[i]);
					}

				}
				t_cfgFileOut.write("\r\n");
			}
			t_cfgFileIn.close();
			t_cfgFileOut.close();
			t_cfgFileIn = null;
			t_cfgFileOut = null;
		}
	}
}
