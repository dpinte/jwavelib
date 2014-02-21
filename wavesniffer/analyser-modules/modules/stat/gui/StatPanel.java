package modules.stat.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import common.Settings;

public class StatPanel extends JPanel {
	private JTabbedPane tabsPanel;
	private StatFramePanel statFramePanel;
	private WpConfigPanel wpConfigpanel;
	private DetectedModulesPanel detectedModulesPanel;
	
	public StatPanel( Settings settings) {		
		this.statFramePanel = new StatFramePanel(settings);
		this.wpConfigpanel = new WpConfigPanel(settings);
		this.detectedModulesPanel = new DetectedModulesPanel(settings);
		
		this.tabsPanel = new JTabbedPane();
		this.tabsPanel.addTab("Detected Modules", this.detectedModulesPanel);
		this.tabsPanel.addTab("WavePort configuration", this.wpConfigpanel);
		this.tabsPanel.addTab("Frame Statistic", this.statFramePanel);
		
		this.setLayout(new BorderLayout());
		this.add(this.tabsPanel, BorderLayout.CENTER);
	}

	public void refreshTab(){
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
