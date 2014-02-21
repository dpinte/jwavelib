package modules.analyser;

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
import modules.analyser.gui.AnalyserPanel;

public class Analyser extends AbstractModule implements ActionListener {

	private AnalyserPanel panel;
	private JToolBar toolBar;
	private JButton nextBut, prevBut, refBut;
	
	public Analyser() {}
		
	@Override
	public void createModule(Settings settings) {
		//this.panel = new AnalyserPanel(container, settings);
		
		this.panel = new AnalyserPanel(settings);
		
		//create buttons
		this.nextBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/go-down.png")));
		this.nextBut.setToolTipText("Select next frame");
		this.prevBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/go-up.png")));
		this.prevBut.setToolTipText("select previous frame");
		this.prevBut.setEnabled(false);
		this.refBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/view-refresh.png")));
		this.refBut.setToolTipText("Refresh");
		
		//create toolbar
		this.toolBar = new JToolBar(this.getModuleName());
		this.toolBar.add(this.nextBut);
		this.toolBar.add(this.prevBut);
		this.toolBar.add(this.refBut);
		
		//set listeners
		this.nextBut.addActionListener(this);
		this.prevBut.addActionListener(this);
		
	}
	
	@Override
	public String getModuleName() {
		return "Frame analyser module";
	}

	@Override
	public String getModuleVersion() {
		return "0.1";
	}

	@Override
	public boolean hasMenu() {
		//return true;
		return false;
	}
	
	@Override
	public JMenu getMenu() {
		return new JMenu(this.getModuleName());
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
	public boolean hasSettingsPanel() {
		return false;
	}
	
	@Override
	public SettingsInterface getSettingsPanel() {
		return null;
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
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == this.nextBut){
			if(this.panel.getTablePanel().selectNextRow()){
				if(!this.prevBut.isEnabled()){
					this.prevBut.setEnabled(true);
				} 
			} else {
				if(this.prevBut.isEnabled()){
					this.nextBut.setEnabled(false);
				}
			}
		} else if(event.getSource() == this.prevBut){
			if(this.panel.getTablePanel().selectPrevRow()){
				if(!this.nextBut.isEnabled()){
					this.nextBut.setEnabled(true);
				}
			} else {
				if(this.nextBut.isEnabled()){
					this.prevBut.setEnabled(false);
				}
			}
		} else if(event.getSource() == this.refBut){
			if(panel != null)
				this.panel.refresh();
		}
		
	}

	@Override
	public void destroyModule() {}
}
