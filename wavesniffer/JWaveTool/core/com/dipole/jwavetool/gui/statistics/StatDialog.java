package com.dipole.jwavetool.gui.statistics;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import com.dipole.jwavetool.common.Common;
import com.dipole.jwavetool.common.Settings;


public class StatDialog extends JDialog implements ActionListener {
	private JTabbedPane tabsPanel;
	private JButton refBut;
	
	private StatFramePanel statFramePanel;
	private WpConfigPanel wpConfigpanel;
	private DetectedModulesPanel detectedModulesPanel;
	
	public StatDialog(final Settings settings) {
		super();
		
		this.statFramePanel = new StatFramePanel(settings);
		this.wpConfigpanel = new WpConfigPanel(settings);
		this.detectedModulesPanel = new DetectedModulesPanel(settings);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(400, 300);
		this.setTitle("Statistics");
		this.setLocationRelativeTo(null);
		
		this.refBut = new JButton(new ImageIcon(this.getClass().getResource(Common.ICONS_22_PATH +"view-refresh.png")));
		this.refBut.setToolTipText("Refresh");
		this.refBut.addActionListener(this);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(this.refBut);		
		
		this.tabsPanel = new JTabbedPane();
		this.tabsPanel.addTab("Detected Modules", this.detectedModulesPanel);
		this.tabsPanel.addTab("WavePort configuration", this.wpConfigpanel);
		this.tabsPanel.addTab("Frame Statistic", this.statFramePanel);
		
		this.setLayout(new BorderLayout());
		this.add(toolBar, BorderLayout.NORTH);
		this.add(this.tabsPanel, BorderLayout.CENTER);
		
		this.setVisible(true);
	}

	public void refreshTab() {
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
			
			default:
				break;
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.refreshTab();
		
	}
}
