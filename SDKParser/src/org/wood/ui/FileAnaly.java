package org.wood.ui;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.wood.util.AnalyUtil;
import org.wood.util.FileUtil;
import org.wood.util.Logger;
import org.wood.util.UtilOutGrabber;

public class FileAnaly implements Runnable {

	public void run() {
		// file filter
		FileNameExtensionFilter t_fileFilter = new FileNameExtensionFilter(
				"source and head files(.cpp, .h)", "cpp", "h");
		// file chooser
		JFileChooser t_fileChooser = new JFileChooser();
		t_fileChooser.setAcceptAllFileFilterUsed(false);
		t_fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		t_fileChooser.setFileFilter(t_fileFilter);
		t_fileChooser.setMultiSelectionEnabled(true);

		// get choice results
		int t_chooserRet = t_fileChooser.showOpenDialog(MainFrame
				.getMainFrame());

		// get the result and begin analy these files
		if (t_chooserRet == JFileChooser.APPROVE_OPTION) {

			// open the progress dialog
			ModelMsgPanel.startProgressDialog("Analysing ....");
			Thread.yield();

			File[] t_selectedFiles = t_fileChooser.getSelectedFiles();
			String[] t_files = new String[t_selectedFiles.length];
			for (int i = 0; i < t_selectedFiles.length; i++) {
				t_files[i] = t_selectedFiles[i].getAbsolutePath();
			}
			try {
				delXMLDir();
				AnalyUtil.initCfgFile(t_files);
				AnalyUtil.initialUtil();
				String t_cmdFileName = FileUtil.getTempFile() + "doxygen.exe";
				String t_cfgFileName = FileUtil.getTempFile() + "config.txt";

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
				t_process.waitFor();

				// doxygen analy all source
				// begin analy the result of doxygen
				analyXML();
			} catch (IOException e) {

			} catch (InterruptedException e) {

			}

			// colse the progress dialog
			ModelMsgPanel.clean();
		}

	}

	private void analyXML() {
		String t_xmlDirName = FileUtil.getTempFile() + "xml";
		File t_xmlDir = new File(t_xmlDirName);
		if (t_xmlDir.exists()) {
			File[] t_subFiles = t_xmlDir.listFiles();
			for (File t_subFile : t_subFiles) {
				Logger.info(t_subFile);
			}
		}
	}

	private void delXMLDir() {
		String t_xmlDirName = FileUtil.getTempFile() + "xml";
		File t_xmlDir = new File(t_xmlDirName);

		if (t_xmlDir.exists()) {
			File[] t_subFiles = t_xmlDir.listFiles();
			for (File t_subFile : t_subFiles) {
				t_subFile.delete();
			}
			t_xmlDir.delete();
		}

	}
}
