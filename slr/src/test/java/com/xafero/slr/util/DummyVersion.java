package com.xafero.slr.util;

import org.eclipse.aether.version.Version;

public class DummyVersion implements Version {

	public int compareTo(Version other) {
		return 0;
	}
	
}