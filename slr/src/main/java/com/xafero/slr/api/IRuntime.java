package com.xafero.slr.api;

public interface IRuntime {

	int require(String args);

	void setLogger(ILogger log);

}