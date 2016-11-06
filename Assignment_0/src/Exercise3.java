/*
 * This program merges two files into a third one. Names of the three files are specified as command line parameters.
 * 
 * This solution uses streams, lambda expressions and "nio" library.
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class Exercise3 {

	public static void main(String[] args) {
		if(args.length != 3) {
			System.err.println("Provide three file names");
			return;
		}
		
		try {
			// Read all the lines from the first file
			List<String> rows = Files.readAllLines(Paths.get(args[0]));
			// Read all the lines from the second file
			rows.addAll(Files.readAllLines(Paths.get(args[1])));
			
			// Use streams to process all rows
			List<String> sortedRows = rows.stream()
			// sort them (ascending)
			.sorted()
			// and store into a list
			.collect(Collectors.toList());
			
			// Finally write the sorted rows to the third file
			Files.write(Paths.get(args[2]),sortedRows);
			
		} catch (IOException e) {
			// This exception can be thrown from one of the file reads, e.g. if input file does not exist with the specified name
			System.err.println("IO exception with file " + e.getMessage());
			e.printStackTrace();
		}
	}

}
