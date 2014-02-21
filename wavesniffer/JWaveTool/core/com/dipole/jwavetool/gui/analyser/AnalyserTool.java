package com.dipole.jwavetool.gui.analyser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.dipole.jwavetool.common.Common;
import com.dipole.jwavetool.common.Settings;
import com.dipole.jwavetool.gui.statistics.StatDialog;

public class AnalyserTool extends JToolBar implements ActionListener {
	private JButton nextBut, prevBut, refBut, statBut;
	private FrameTablePanel table;
	
	public AnalyserTool(final FrameTablePanel table) {
		super("Analyser");
		this.table = table;
		
		//create buttons
		this.nextBut = new JButton(new ImageIcon(this.getClass().getResource(Common.ICONS_22_PATH +"go-down.png")));
		this.nextBut.setToolTipText("Select next frame");
		this.prevBut = new JButton(new ImageIcon(this.getClass().getResource(Common.ICONS_22_PATH +"go-up.png")));
		this.prevBut.setToolTipText("select previous frame");
		this.prevBut.setEnabled(false);
		this.refBut = new JButton(new ImageIcon(this.getClass().getResource(Common.ICONS_22_PATH +"view-refresh.png")));
		this.refBut.setToolTipText("Refresh");
		this.statBut = new JButton(new ImageIcon(this.getClass().getResource(Common.ICONS_22_PATH +"edit-find.png")));
		this.statBut.setToolTipText("Show statistics");
		
		//create toolbar
		this.add(this.nextBut);
		this.add(this.prevBut);
		this.add(this.refBut);
		this.add(this.statBut);
		
		//set listeners
		this.nextBut.addActionListener(this);
		this.prevBut.addActionListener(this);
		this.refBut.addActionListener(this);
		this.statBut.addActionListener(this);
	}

	@Override
	@SuppressWarnings("unused")
	public void actionPerformed(final ActionEvent event) {
		if(event.getSource() == this.nextBut) {
			if(this.table.selectNextRow()) {
				if(!this.prevBut.isEnabled()) {
					this.prevBut.setEnabled(true);
				} 
			} else {
				if(this.prevBut.isEnabled()) {
					this.nextBut.setEnabled(false);
				}
			}
			
		} else if(event.getSource() == this.prevBut) {
			if(this.table.selectPrevRow()) {
				if(!this.nextBut.isEnabled()) {
					this.nextBut.setEnabled(true);
				}
			} else {
				if(this.nextBut.isEnabled()) {
					this.prevBut.setEnabled(false);
				}
			}
			
		} else if(event.getSource() == this.refBut) {
			((FrameTableModel)this.table.getTable().getModel()).refresh();
			
		} else if(event.getSource() == this.statBut) {
			StatDialog stat = new StatDialog(Settings.getInstance());
		}
	}
}
