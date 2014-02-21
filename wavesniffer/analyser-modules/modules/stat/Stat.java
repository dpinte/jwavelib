package modules.stat;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import common.Settings;

import modules.AbstractModule;
import modules.SettingsInterface;
import modules.stat.gui.StatPanel;

public class Stat 	extends AbstractModule
					implements ActionListener {
	private JToolBar toolBar;
	private JButton refreshBut;
	private StatPanel panel;
	
	public Stat(){}
		
	@Override
	public void createModule(Settings settings) {
		this.panel = new StatPanel(settings);
		
		//create buttons
		this.refreshBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/view-refresh.png")));
		this.refreshBut.setToolTipText("Refresh statistiscs");
		
		//create toolbar
		this.toolBar = new JToolBar(this.getModuleName());
		this.toolBar.add(this.refreshBut);
		
		//set listeners
		this.refreshBut.addActionListener(this);
	}
	
	@Override
	public String getModuleName() {
		return "Frame statistics module";
	}

	@Override
	public String getModuleVersion() {
		return "0.1";
	}

	@Override
	public boolean hasMenu() {
		return false;
	}
	
	@Override
	public JMenu getMenu() {
		return null;
	}

	@Override
	public boolean hasSettingsPanel() {
		return false;
	}
	
	@Override
	public SettingsInterface getSettingsPanel() {
		return null;
	}

	@Override
	public boolean hasToolBar() {
		return true;
	}
	
	@Override
	public JToolBar getToolBar() {
		return this.toolBar;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		this.panel.refreshTab();
	}

	@Override
	public Component getGUI() {
		return this.panel;
	}

	@Override
	public boolean hasGUI() {
		return true;
	}
	
	@Override
	public void destroyModule() {}
}
