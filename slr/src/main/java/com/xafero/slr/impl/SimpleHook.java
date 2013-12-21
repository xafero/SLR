package com.xafero.slr.impl;

import com.xafero.slr.api.IHook;

public class SimpleHook implements IHook {
	private Runnable onShutdown;

	@Override
	public Runnable getOnShutdown() {
		return onShutdown;
	}

	@Override
	public void setOnShutdown(Runnable onShutdown) {
		this.onShutdown = onShutdown;
	}
}