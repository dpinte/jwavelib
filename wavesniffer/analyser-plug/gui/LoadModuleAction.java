package gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class LoadModuleAction extends AbstractAction {

	private int moduleIndex;
	private MainWindow parent;
	
	public LoadModuleAction(MainWindow parent, int modInd) {
		super("Load");
		
		this.moduleIndex = modInd;
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.parent.loadModule(this.moduleIndex);

	}

}
