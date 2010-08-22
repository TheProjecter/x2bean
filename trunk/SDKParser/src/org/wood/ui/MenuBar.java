package org.wood.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar implements ActionListener {
	/**
	 * Initialize the menubar
	 */
	public MenuBar() {

		m_exit.addActionListener(this);
		m_openFile.addActionListener(this);
		m_file.add(m_openFile);
		m_file.add(m_exit);
		this.add(m_file);

		LookAndFeelInfo[] t_looks = UIManager.getInstalledLookAndFeels();
		m_uiSubMens = new UIMenuItem[t_looks.length];
		for (int i = 0; i < t_looks.length; i++) {
			m_uiSubMens[i] = new UIMenuItem();
			m_uiSubMens[i].setText(t_looks[i].getName());
			m_uiSubMens[i].m_uiClassName = t_looks[i].getClassName();
			m_uiSubMens[i].addActionListener(this);
			m_uiMens.add(m_uiSubMens[i]);
		}
		this.add(m_uiMens);
	}

	/** -------[Begin] member--------------- */
	protected JMenu m_file = new JMenu("File");
	protected JMenuItem m_openFile = new JMenuItem("Open Files...");
	protected JMenuItem m_exit = new JMenuItem("Exit");

	protected JMenu m_uiMens = new JMenu("UIManager");
	protected UIMenuItem m_uiSubMens[];

	/** -------[End ] member--------------- */

	/**
	 * Action Listener for menubar
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == m_exit) {
			System.exit(0);
		} else if (e.getSource() == m_openFile) {
			new Thread(new FileAnaly()).start();
			return;
		}

		// UI menu item
		for (int i = 0; i < m_uiSubMens.length; i++) {
			if (e.getSource() == m_uiSubMens[i]) {
				SwingUtilities.invokeLater(new LoadUI(
						m_uiSubMens[i].m_uiClassName));
			}
		}
	}

	/**
	 * UIMenuItem
	 * 
	 * @author woody
	 * 
	 */
	protected class UIMenuItem extends JMenuItem {
		public String m_uiClassName;
	}

	/**
	 * Load UI thread
	 * 
	 * @author woody
	 * 
	 */
	public static class LoadUI implements Runnable {
		private String m_uiName;

		public LoadUI(String uiName) {
			this.m_uiName = uiName;
		}

		public void run() {
			MainFrame.getMainFrame().setVisible(false);
			try {
				UIManager.setLookAndFeel(m_uiName);
				SwingUtilities.updateComponentTreeUI(MainFrame.getMainFrame());
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
			MainFrame.getMainFrame().setVisible(true);
		}

	}
}
