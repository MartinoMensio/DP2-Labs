/* 
 * The program is launched with a parameter that identifies a text file to be elaborated using a dictionary:
 * all the words in the input file that don't appear in the dictionary file are echoed on standard output.
 * 
 * This solution uses streams, lambda expressions and "nio" library.
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Exercise1 {

	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.println("Provide a parameter");
			return;
		}
		try {
			// Read all the lines of the dictionary, to be ready to process the real input file
			List<String> dictionaryRows = Files.readAllLines(Paths.get("dictionary.txt"));
			// Now read the lines of the input file and process them (one by one) using a stream
			Files.lines(Paths.get(args[0]))
			// remove lines that appear in the dictionary
			.filter(s -> !dictionaryRows.contains(s))
			// and echo to standard output the remaining ones
			.forEach(s -> System.out.println(s));
		} catch (IOException e) {
			// This exception can be thrown from one of the file reads, e.g. if input file does not exist with the specified name
			System.err.println("IO exception with file " + e.getMessage());
			e.printStackTrace();
		}
	}

}
