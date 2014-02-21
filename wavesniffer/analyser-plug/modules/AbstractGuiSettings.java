package modules;

import javax.swing.JPanel;

import common.Settings;

public abstract class AbstractGuiSettings 	extends JPanel
											implements 	SettingsInterface {

	protected Settings settings;
	
	public AbstractGuiSettings(Settings settings){
		this.settings = settings;
	}
}
