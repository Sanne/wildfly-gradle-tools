package org.wildfly.build.gradle.featurepackbuild;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import nu.xom.ParsingException;

class ModuleFilesBuilder {

	private final Path templateFolder;
	private final Path rootModulesFolder;
	private final AtomicReference<Exception> firstError = new AtomicReference<>(  );
	private final String assignedSlot;
	private final TemplatePatterns patterns;

	public ModuleFilesBuilder(Path templateFolder, Map<String,String> variables, Path rootModulesFolder, String assignedSlot) {
		this.templateFolder = templateFolder;
		this.rootModulesFolder = rootModulesFolder;
		this.assignedSlot = assignedSlot;
		this.patterns = new TemplatePatterns( variables );
	}

	void build() throws Exception {
		firstError.set( null );
		Files.walk( templateFolder, FileVisitOption.FOLLOW_LINKS )
				.filter( p -> isRegularXML( p ) )
				.parallel()
				.forEach( t -> buildModuleSafe( t ) );
		final Exception anyError = firstError.get();
		if ( anyError != null ) {
			throw anyError;
		}
	}

	private void buildModuleSafe(Path file) {
		try {
			if ( isRegularXML( file ) ) {
				buildModule( file );
			}
		}
		catch (Exception e) {
			firstError.compareAndSet( null, e );
		}
	}

	private static boolean isRegularXML(Path file) {
		return Files.isRegularFile( file ) && file.getFileName().toString().toLowerCase().trim().endsWith( ".xml" );
	}

	private void buildModule(Path templateFile) throws IOException, ParsingException {
		final ModuleTemplate moduleDef = ModuleTemplate.parse( templateFile, patterns );

		final ModuleFolderPath moduleFolderPath = new ModuleFolderPath( rootModulesFolder, moduleDef );
		moduleFolderPath.createFolders();

		final Module xmlFile = new Module( moduleFolderPath );
		xmlFile.createWithContent( moduleDef.getXmlModuleContent() );
	}

}
