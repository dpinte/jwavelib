package com.dipole.jwavetool.gui.analyser;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.dipole.jwavetool.common.Settings;

public class AnalyserPanel extends JSplitPane {
	private FrameDetailPanel detailPanel;
	private FrameTablePanel tablePanel;
	private JPanel topPanel;
	
	public AnalyserPanel(final Settings settings){
		
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);

		this.tablePanel = new FrameTablePanel(settings);
		this.tablePanel.setMinimumSize(new Dimension(800, 300));
		this.detailPanel = new FrameDetailPanel(this.tablePanel.getTable());
		
		this.topPanel = new JPanel(new BorderLayout());
		this.topPanel.add(new FilterPanel(), BorderLayout.NORTH);
		this.topPanel.add(this.tablePanel);
		
		this.setTopComponent(this.topPanel);
		this.setBottomComponent(this.detailPanel);
		
		this.setVisible(true);
	}
	
	public void refresh(){
		((FrameTableModel)this.tablePanel.getTable().getModel()).refresh();
	}
	
	public FrameTablePanel getTablePanel(){
		return this.tablePanel;
	}
}
