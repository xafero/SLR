package com.xafero.slr.api;

public interface IHook {

	Runnable getOnShutdown();

	void setOnShutdown(Runnable callback);

}