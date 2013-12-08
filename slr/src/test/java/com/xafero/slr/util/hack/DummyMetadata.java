package com.xafero.slr.util.hack;

import java.io.File;
import java.util.Map;

import org.eclipse.aether.metadata.AbstractMetadata;
import org.eclipse.aether.metadata.Metadata;

public class DummyMetadata extends AbstractMetadata implements Metadata {
	private String groupId;
	private String artifactId;
	private String version;
	private String type;
	private Nature nature;
	private File file;
	private Map<String, String> properties;

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public String getType() {
		return type;
	}

	public Nature getNature() {
		return nature;
	}

	public File getFile() {
		return file;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public DummyMetadata groupId(String groupId) {
		this.groupId = groupId;
		return this;
	}

	public DummyMetadata artifactId(String artifactId) {
		this.artifactId = artifactId;
		return this;
	}

	public DummyMetadata version(String version) {
		this.version = version;
		return this;
	}

	public DummyMetadata type(String type) {
		this.type = type;
		return this;
	}
}