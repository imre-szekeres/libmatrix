/**
 * Simulation.java
 * */
package org.crf.libmatrix.simulations;

import org.crf.libmatrix.Matrix;
import org.crf.libmatrix.MatrixGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 * */
public final class Simulation <T extends MatrixGenerator> {

	public Simulation(final Class<T> clz, final Path logFilePath) throws InstantiationException, IllegalAccessException {
		super( );
		this.generator = clz.newInstance();
		this.logFilePath = logFilePath;
	}

	/**
	 * Executes the simulation and logs the measured values (runtime of each iteration).
	 * 
	 * @throws {@link IOException}
	 * */
	public final void run() {
		try (BufferedWriter writer = Files.newBufferedWriter( logFilePath, 
				                                              StandardCharsets.UTF_8, 
				                                              OPEN_OPTIONS )) {
			
			runSimulation( writer );
		} catch(final IOException e) {
			System.err.println(String.format("IO-ERROR: %s\n", e));
		}
	}

	private final void runSimulation(BufferedWriter writer) throws IOException {
		logHeader( writer );
		long startTime;
		Matrix lhs, rhs, result;
		for(int h = START_HIGHT; h < MAX_HIGHT; h = nextValue( h )) {
			lhs = generator.generate( h );
			rhs = generator.generate( h );
			startTime = System.nanoTime();
			result = lhs.multiply( rhs );
			logRuntime( writer, h, h, System.nanoTime() - startTime );
		}
	}

	/**
	 * 
	 * */
	private final void logHeader(BufferedWriter writer) throws IOException {
		System.out.println("\tHeight\tWidth\tSize\tRuntime");
		System.out.println("------------------------------------------\n");
		writer.write("size,runtime[nanos]");
		writer.newLine();
	}

	/**
	 * 
	 * */
	private final void logRuntime(final BufferedWriter writer, final int height, final int width, final long runtime) 
						throws IOException {
		int size = height*width;
		System.out.println(String.format("\t%s\t%s\t%s\t%s[ns]", height, width, size, runtime));
		writer.write(String.format("%s,%s", size, runtime));
		writer.newLine();
	}

	/**
	 * @param height
	 * @return the next height of the {@link Matrix}es to run the simulation on
	 * */
	private static final int nextValue(int height) {
		return height + (height / 7); 
	}

	public static final OpenOption[] OPEN_OPTIONS;

	static {
		List<OpenOption> options = new ArrayList<>();
		options.add( StandardOpenOption.CREATE );
		options.add( StandardOpenOption.WRITE );
		options.add( StandardOpenOption.TRUNCATE_EXISTING );
		OPEN_OPTIONS = (OpenOption[]) options.toArray( new OpenOption[0] );
	}

	public static final double NANOS_TO_MILLIS = 1e-6;
	public static final int START_HIGHT = 7;
	public static final int MAX_HIGHT = 210;
	private final MatrixGenerator generator;
	private final Path logFilePath;
}
