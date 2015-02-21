/**
 * SimulationRunner.java
 * */
package org.crf.libmatrix.core;

import org.crf.libmatrix.core.LibraryConfiguration;
import org.crf.libmatrix.simulations.Simulation;
import org.crf.libmatrix.MatrixGenerator;

import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
public class SimulationRunner {

	public static final void main(final String[] args) throws Exception {
		Map<String, Object> config = configOf( args );

// TODO: meresek..
		Path singlethCsvFile = pathOf( (Path) config.get(REPORTS_HOME), CSV_ROOT, "single-th.csv" );
		System.out.println("Running Matrix multiplication simulations..");
		new Simulation<MatrixGenerator>( MatrixGenerator.class,  singlethCsvFile).run();
		System.out.println("end of Matrix multiplication simulations..");

//      new Simulation<ParallelMatrixGenerator>( ParallelMatrixGenerator.class, outputFile2 ).run();

//      generateReports(outputFile1, outputFile2);
	}

	/**
	 * Builds a configuration <code>Map</code> from the arguments passed by filtering
	 * the required values, and providing default ones in case not found.
	 * 
	 * @param args
	 * @return a configuration map
	 * */
	private static final Map<String, Object> configOf(final String[] args) throws Exception {
		Map<String, Object> config = new HashMap<>();
		config.put("reports-home", "../reports");
		
		for(int i = 0; i < args.length; ++i) {
			if ("--reports-home".equals( args[i] ))
				config.put(REPORTS_HOME, Paths.get(args[++i]).toAbsolutePath());
			
			else if ("--mtx-thread-count".equals( args[i] ))
				LibraryConfiguration.set(LibraryConfiguration.MTX_THREAD_COUNT, Integer.valueOf( args[++i] ));
		}
		return config;
	}

	/**
	 * 
	 * */
	private static final Path pathOf(final Path root, final String subFolder, final String file) 
							throws Exception {
		Path path = root.resolve( subFolder ).normalize();
		Files.createDirectories( path );
		return path.resolve( file );
	}

	public static final String REPORTS_HOME = "reports-home"; 
	public static final String CSV_ROOT = "csv";
}
