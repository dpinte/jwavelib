package com.dipole.jwavetool.modules;

import javax.swing.event.EventListenerList;

import com.dipole.jwavetool.common.Settings;

import com.dipole.jwavetool.events.ModuleErrorListener;
import com.dipole.jwavetool.events.ModuleStatusListener;

public abstract class AbstractModule implements ModuleInterface {

	private final EventListenerList statusListeners = new EventListenerList();
	private final EventListenerList errorListeners = new EventListenerList();
	
	protected Settings settings;
	
	protected AbstractModule(){}
	
	public abstract void createModule(final Settings settings);
	
	public void addModuleErrorListener(final ModuleErrorListener listener) {
		this.errorListeners.add(ModuleErrorListener.class, listener);
	}

	public void addModuleStatusListener(final ModuleStatusListener listener) {
		this.statusListeners.add(ModuleStatusListener.class, listener);
	}
	
	public void fireModulerError(final String source, final String message) {
		for(ModuleErrorListener listener : this.getModuleErrorListeners()){
			listener.moduleError(source, message);
		}
	}

	public void fireModuleStatus(final String source, final String message) {
		for(ModuleStatusListener listener: this.getModuleStatusListeners()){
			listener.moduleStatus(source, message);
		}
	}

	public ModuleErrorListener[] getModuleErrorListeners() {
		return this.errorListeners.getListeners(ModuleErrorListener.class);
	}
	
	public ModuleStatusListener[] getModuleStatusListeners() {
		return this.statusListeners.getListeners(ModuleStatusListener.class);
	}
	
	public void removeModuleErrorListener(final ModuleErrorListener listener) {
		this.errorListeners.remove(ModuleErrorListener.class, listener);
	}

	public void removeModuleStatusListener(final ModuleStatusListener listener) {
		this.statusListeners.remove(ModuleStatusListener.class, listener);
	}

}
