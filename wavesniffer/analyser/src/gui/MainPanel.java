package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import common.Settings;

import frame.FrameContainer;

public class MainPanel extends JSplitPane {
	private FrameContainer container = null;
	private FrameDetailPanel detailPanel;
	private FrameTablePanel tablePanel;
	private JPanel topPanel;
	
	public MainPanel(FrameContainer container, Settings settings){
		this.container = container;
		
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);

		this.tablePanel = new FrameTablePanel(this.container, settings);
		this.tablePanel.setMinimumSize(new Dimension(800, 300));
		this.detailPanel = new FrameDetailPanel(this.container, this.tablePanel.getTable());
		
		this.topPanel = new JPanel(new BorderLayout());
		this.topPanel.add(new FilterPanel(container), BorderLayout.NORTH);
		this.topPanel.add(this.tablePanel);
		
		//this.setTopComponent(this.tablePanel);
		this.setTopComponent(this.topPanel);
		this.setBottomComponent(this.detailPanel);
		
		this.setVisible(true);
	}
	
	public FrameTablePanel getTablePanel(){
		return this.tablePanel;
	}
}
