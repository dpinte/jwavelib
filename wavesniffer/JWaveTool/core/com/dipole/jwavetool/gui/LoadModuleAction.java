package com.dipole.jwavetool.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class LoadModuleAction extends AbstractAction {

	private int moduleIndex;
	private MainWindow parent;
	
	public LoadModuleAction(final MainWindow parent, final int modInd) {
		super("Load");
		
		this.moduleIndex = modInd;
		this.parent = parent;
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		this.parent.loadModule(this.moduleIndex);

	}

}
