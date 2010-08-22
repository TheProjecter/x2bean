package org.wood.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JFrame;

/**
 * There is only one instance
 * 
 * @author woody
 * 
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static MainFrame mainFrame;

	/**
	 * Get the only MainFrame
	 * 
	 * @return
	 */
	synchronized public static MainFrame getMainFrame() {
		if (mainFrame == null) {
			mainFrame = new MainFrame();
		}
		return mainFrame;
	}

	private MainFrame() {
		super("SDK Toolkits");
		// initial the frame
		m_menuBar = new MenuBar();

		// put the children to the root panel
		this.getRootPane().setJMenuBar(m_menuBar);
		JEditorPane t_editor = new JEditorPane();
		this.getContentPane().add(t_editor);
		BorderLayout layout = (BorderLayout) this.getLayout();
		layout.addLayoutComponent(t_editor, BorderLayout.SOUTH);

		// change the default close button
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// packet this JFrame
		this.setMinimumSize(new Dimension(800, 600));
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}

	/*--------------[begin] child widgets ---------------------*/
	protected MenuBar m_menuBar;
	protected LeftPanel m_leftPanel;
	protected MainPanel m_mainPanel;

	/*--------------[end  ] child widgets ---------------------*/

}
