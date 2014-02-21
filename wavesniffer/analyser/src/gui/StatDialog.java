package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import common.Settings;

import frame.FrameContainer;

public class StatDialog extends JDialog implements ActionListener {
	private JTabbedPane tabsPanel;
	private StatFramePanel statFramePanel;
	private WpConfigPanel wpConfigpanel;
	private DetectedModulesPanel detectedModulesPanel;
	private JToolBar toolBar;
	private JButton refreshBut;
	
	public StatDialog(FrameContainer container, Settings settings) {
		this.setSize(410, 300);
		this.setTitle("Sniffing Statistics");
		
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		this.refreshBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/view-refresh.png")));
		this.refreshBut.setToolTipText("Refresh Modules List");
		this.refreshBut.addActionListener(this);
		
		this.toolBar = new JToolBar();
		this.toolBar.add(this.refreshBut);
		
		this.statFramePanel = new StatFramePanel(container, settings);
		this.wpConfigpanel = new WpConfigPanel(container, settings);
		this.detectedModulesPanel = new DetectedModulesPanel(container, settings);
		
		this.tabsPanel = new JTabbedPane();
		this.tabsPanel.addTab("Detected Modules", this.detectedModulesPanel);
		this.tabsPanel.addTab("WavePort configuration", this.wpConfigpanel);
		this.tabsPanel.addTab("Frame Statistic", this.statFramePanel);
		
		this.setLayout(new BorderLayout());
		this.add(this.toolBar, BorderLayout.NORTH);
		this.add(this.tabsPanel, BorderLayout.CENTER);
		
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == this.refreshBut){
			switch(this.tabsPanel.getSelectedIndex()){
				case 0:
					this.detectedModulesPanel.refresh();
					break;
				case 1:
					this.wpConfigpanel.refresh();
					break;
				case 2:
					this.statFramePanel.refresh();
					break;
			}
		}
		
	}


}
