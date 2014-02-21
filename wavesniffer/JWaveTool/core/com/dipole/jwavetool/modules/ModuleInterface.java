package com.dipole.jwavetool.modules;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JToolBar;

import com.dipole.jwavetool.common.Settings;

import com.dipole.jwavetool.events.ModuleErrorListener;
import com.dipole.jwavetool.events.ModuleStatusListener;

public interface ModuleInterface {
	/**
	 * Get the module Name
	 * @return String with module name
	 */
	public String getModuleName();
	
	/**
	 * Get the module version
	 * @return String with the module version
	 */
	public String getModuleVersion();
	
	/**
	 * Create/start/load the module
	 * @param container The frame container
	 * @param settings	The Settings  
	 */
	public void createModule(Settings settings);
	
	/**
	 * Destroy/stop/unload the module
	 */
	public void destroyModule();
	
	public void addModuleErrorListener(ModuleErrorListener listener);
	public void addModuleStatusListener(ModuleStatusListener listener);
	public void removeModuleErrorListener(ModuleErrorListener listener);
	public void removeModuleStatusListener(ModuleStatusListener listener);
	public ModuleErrorListener[] getModuleErrorListeners();
	public ModuleStatusListener[] getModuleStatusListeners();
	
	/**
	 * Check if the module provides a GUI
	 * @return True if the module has a GUI
	 */
	public boolean hasGUI();
	
	/**
	 * Get The GUI of the module
	 * @return The component to display in the GUI
	 */
	public Component getGUI();
	
	/**
	 * Check if the module provides a Settings panel
	 * @return True if the module has a Settings panel
	 */
	public boolean hasSettingsPanel();
	
	/**
	 * Get panel to display in Settings dialog
	 * @return The Settings panel
	 */
	public SettingsInterface getSettingsPanel();
	
	/**
	 * Check if the module provides a menu
	 * @return True if the module has a menu
	 */
	public boolean hasMenu();
	
	/**
	 * Get the Menu of the module
	 * @return A JMenu
	 */
	public JMenu getMenu();
	
	/**
	 * Check if the modules provide a ToolBar
	 * @return True if the module has a ToolBarr
	 */
	public boolean hasToolBar();
	
	/**
	 * Get The ToolBar of the module
	 * @return a JToolBar
	 */
	public JToolBar getToolBar();
}
