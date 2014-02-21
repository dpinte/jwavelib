package com.dipole.jwavetool.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class UnLoadModuleAction extends AbstractAction {

	private int moduleIndex;
	private MainWindow parent;
	
	public UnLoadModuleAction(final MainWindow parent, final int modInd) {
		super("UnLoad");
		
		this.moduleIndex = modInd;
		this.parent = parent;
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		this.parent.unloadModule(this.moduleIndex);

	}

}
