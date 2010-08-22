package org.wood.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ModelMsgPanel {

	// prevent to create object
	private ModelMsgPanel() {
	};

	// the model dialog
	private static JDialog g_dialog;

	/**
	 * Get the Dialog
	 * 
	 * @return
	 */
	public static JDialog getDialog() {
		if (null == g_dialog) {
			g_dialog = new JDialog(MainFrame.getMainFrame(), true);
			g_dialog.setUndecorated(true);
		}
		return g_dialog;
	}

	/**
	 * Clean every thing in the Dialog and hidden this dialog
	 */
	public static void clean() {
		if (null == g_dialog) {
			return;
		}
		if (getDialog().isVisible()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					getDialog().getContentPane().removeAll();
					getDialog().setVisible(false);
				}

			});
		}
	}

	/**
	 * Show the Process Dialog.
	 * 
	 * @param message
	 */
	public static void startProgressDialog(String message) {
		createProgressDialog(message);
		getDialog().setLocationRelativeTo(MainFrame.getMainFrame());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getDialog().setVisible(true);
			}

		});

	}

	/**
	 * Create Process Dialog
	 * 
	 * @param message
	 */
	private static void createProgressDialog(String message) {
		JProgressBar g_progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
		g_progressBar.setIndeterminate(true);

		JLabel g_msg = new JLabel();
		g_msg.setText(message);

		JButton g_cancleButton = new JButton();
		g_cancleButton.setText("Cancle");
		g_cancleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getDialog().setVisible(false);
			}
		});

		JPanel g_progressPanel = new JPanel();
		g_progressPanel.add(g_msg);
		g_progressPanel.add(g_progressBar);
		g_progressPanel.add(g_cancleButton);

		// clean
		clean();

		// initial the ProgressDialo
		getDialog().getContentPane().add(g_progressPanel);
		getDialog().setSize(new Dimension(160, 80));
	}

}
