/*
 * This program elaborates a file that is specified using a system property. The file is read row by row
 * (each one of them represents a time and date in the gregorian calendar) and the most recent date is echoed out.
 * 
 * This solution uses streams, lambda expressions, "nio" library and classes for scanning and formatting dates.
 */

import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;

public class Exercise2 {

	public static void main(String[] args) {
		// Get the "it.polito.dp2.file" system property
		String dir = System.getProperty("it.polito.dp2.file");
		if(dir == null) {
			System.err.println("The system property it.polito.dp2.file has not been set");
			return;
		}
		// Variable for storing the maximum date found
		Optional<GregorianCalendar> max = null;
		try {
			// Read the input file and process it using streams
			max = Files.lines(Paths.get(dir))
			// each line is mapped to a GregorianCalendar object
			.map(s -> {
				GregorianCalendar gc = new GregorianCalendar();
				try {
					// parse the string
					gc.setTime(new SimpleDateFormat("HH mm ss dd MM yyyy").parse(s));
				} catch (ParseException e) {
					// error in parsing string
					System.err.println("Error parsing string: " + s);
					e.printStackTrace();
					// a null element in the Stream<GregorianCalendar> will be filtered out
					return null;
				}
				return gc;
			})
			// remove null elements (generated by wrong format in lines)
			.filter(c -> c != null)
			// find the maximum
			.max((a, b) -> a.compareTo(b));
		} catch (IOException e) {
			// This exception can be thrown from one of the file reads, e.g. if input file does not exist with the specified name
			System.err.println("IO exception with file " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		// check if the maximum is present (at least one valid GregorianCalendar object was in the stream)
		if(max.isPresent()) {
			// Print out the Date by specifying the format wanted
			System.out.println(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(max.get().getTime()));
		} else {
			System.err.println("No valid lines found");
		}
	}

}