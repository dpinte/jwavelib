package com.dipole.jwavetool.events;

import java.util.EventListener;

public interface ModuleErrorListener extends EventListener {
	public void moduleError(String source, String message);
}
