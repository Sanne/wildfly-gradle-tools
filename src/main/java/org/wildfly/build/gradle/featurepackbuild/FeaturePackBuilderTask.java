package org.wildfly.build.gradle.featurepackbuild;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import groovy.lang.Closure;
import nu.xom.ParsingException;
import org.wildfly.build.gradle.provisioning.ProvisionOverride;

public class FeaturePackBuilderTask extends DefaultTask {

	private File moduleTemplates;
	private File destinationDir;
	private String slot = "main";
	private Map<String,String> variables = new HashMap<>();

	@TaskAction
	void doBuildFeaturePack() throws Exception {
		getLogger().info( "Starting creationg of feature pack from templates in: '{}'",  moduleTemplates );
		ModuleFilesBuilder moduleFileCreator = new ModuleFilesBuilder( moduleTemplates.toPath(), variables, destinationDir.toPath(), slot );
		moduleFileCreator.build();
	}

	@OutputDirectory
	public File getDestinationDir() {
		return destinationDir;
	}

	public void setDestinationDir(File destinationDir) {
		this.destinationDir = destinationDir;
	}

	@InputDirectory
	public File getModuleTemplates() {
		return moduleTemplates;
	}

	public void setModuleTemplates(File moduleTemplates) {
		this.moduleTemplates = moduleTemplates;
	}

	@Input
	@Optional
	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	@Input
	public Map<String, String> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, String> variables) {
		this.variables = variables;
	}
}
