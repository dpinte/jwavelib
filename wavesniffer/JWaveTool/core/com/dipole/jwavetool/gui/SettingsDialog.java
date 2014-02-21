package com.dipole.jwavetool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.dipole.jwavetool.common.Common;

import com.dipole.jwavetool.modules.SettingsInterface;


public class SettingsDialog extends JDialog implements ActionListener {

	private JPanel butPanel;
	private JTabbedPane tabs;
	private JButton saveBut, closeBut;
	private ArrayList <SettingsInterface> confPanes;
	
	public SettingsDialog(final Component parent,
							final ArrayList <SettingsInterface> panels) {
		super();
		
		this.confPanes = panels;
		
		this.setTitle(Common.NAME);
		this.setSize(480, 360);
		this.setResizable(false);
		this.setModal(true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(parent);
		
		this.setLayout(new BorderLayout());
		
		this.saveBut = new JButton("Save");
		this.closeBut = new JButton("Cancel");
		
		this.saveBut.addActionListener(this);
		this.closeBut.addActionListener(this);
		
		this.butPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.butPanel.add(this.saveBut);
		this.butPanel.add(this.closeBut);
		
		this.tabs = new JTabbedPane();
		for(SettingsInterface pane : this.confPanes) {
			this.tabs.addTab(	pane.getTitle(),
								new JScrollPane(pane.display()));
		}
		
		this.add(butPanel, BorderLayout.SOUTH);
		this.add(tabs, BorderLayout.CENTER);
		
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		if(event.getSource().equals(this.saveBut)) {
			for(SettingsInterface pane : this.confPanes) {
				pane.saveSettings();
			}
			this.dispose();
			
		} else {
			this.dispose();
		}
		
		
	}

}
