/**
 * SimulationRunner.java
 * */
package org.crf.libmatrix.core;

import java.io.InputStreamReader;
import org.crf.libmatrix.core.LibraryConfiguration;
import org.crf.libmatrix.simulations.Simulation;
import org.crf.libmatrix.Matrix;
import org.crf.libmatrix.ParallelMatrix;

import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.BufferedWriter;

import java.util.Calendar;
/**
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
public class SimulationRunner {

	public static final void main(final String[] args) throws Exception {
		Map<String, Object> config = configOf( args );

		Path singlethCsvFile = pathOf( (Path) config.get(REPORTS_HOME), CSV_ROOT, "single-th.csv" );
		System.out.println("Running Matrix multiplication simulations..");
		new Simulation<Matrix.Generator>( Matrix.Generator.class,  singlethCsvFile).run();
		System.out.println("end of Matrix multiplication simulations..");

		Path parallelCsvFile = pathOf( (Path) config.get(REPORTS_HOME), CSV_ROOT, "parallel.csv" );
		System.out.println("Running ParallelMatrix multiplication simulations..");
        new Simulation<ParallelMatrix.Generator>( ParallelMatrix.Generator.class, parallelCsvFile ).run();

        generateCsvReport( (Path) config.get(REPORTS_HOME), singlethCsvFile, parallelCsvFile );
	}

	private static final void waitForInput() throws IOException {
		// Waiting for JVisualVM to start..
		try (BufferedReader reader = new BufferedReader(new InputStreamReader( System.in ))) {
			System.out.println("Press enter to continue..");
			reader.readLine();
		}
	}

	private static final void generateCsvReport(final Path reportHome, final Path singlethCsv, final Path paralellCsv) 
								throws Exception {
        Calendar now = Calendar.getInstance();
        String time =  String.format("%s%s%s_%s.%s.%s", now.get(Calendar.YEAR),
                                                        now.get(Calendar.MONTH),
                                                        now.get(Calendar.DAY_OF_MONTH), 
                                                        now.get(Calendar.HOUR_OF_DAY), 
                                                        now.get(Calendar.MINUTE), 
                                                        now.get(Calendar.SECOND));
		Path report = reportHome.resolve(String.format("%s/%s", CSV_ROOT, String.format(CSV_REPORT_FILE, time))).normalize();
		try ( 
            BufferedWriter reportWriter = Files.newBufferedWriter( report, 
                                                                   StandardCharsets.UTF_8,
                                                                   Simulation.OPEN_OPTIONS );
            BufferedReader sReader = Files.newBufferedReader( singlethCsv );
            BufferedReader pReader = Files.newBufferedReader( paralellCsv ) 
		) {
			String sLine, pLine;
			sReader.readLine();
			pReader.readLine();
			writeHeader( reportWriter );
			while( ((sLine = sReader.readLine())  != null) && 
				   ((pLine = pReader.readLine())) != null) {
				writeReportLine(reportWriter, sLine, pLine);
			}
		}
	}

	private static final void writeHeader(final BufferedWriter reportWriter) throws IOException {
		reportWriter.write("size,sruntime,pruntime,speedup");
		reportWriter.newLine();
	}
	
	private static final void writeReportLine(final BufferedWriter reportWriter, final String sLine, final String pLine) throws Exception {
		String[] sparts = sLine.split(",");
		String[] pparts = pLine.split(",");
		int size = Integer.parseInt( sparts[0] );
		long sruntime = Long.parseLong( sparts[1] );
		long pruntime = Long.parseLong( pparts[1] );
		
		reportWriter.write(String.format( "%s,%s,%s,%1.2g", size, sruntime, pruntime, (double) sruntime / pruntime ));
		reportWriter.newLine();
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

	public static final String CSV_REPORT_FILE = "M2report%s.csv";
	public static final String REPORTS_HOME = "reports-home"; 
	public static final String CSV_ROOT = "csv";
}
