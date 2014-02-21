package com.dipole.jwavetool.events;

import java.util.EventListener;

public interface ModuleStatusListener extends EventListener {
	public void moduleStatus(String source, String message);
}
