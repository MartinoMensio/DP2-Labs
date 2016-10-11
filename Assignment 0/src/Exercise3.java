
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
			List<String> rows = Files.readAllLines(Paths.get(args[0]));
			rows.addAll(Files.readAllLines(Paths.get(args[1])));
			
			List<String> sortedRows = rows.stream()
			.sorted()
			.collect(Collectors.toList());
			
			Files.write(Paths.get(args[2]),sortedRows);
			
		} catch (IOException e) {
			System.err.println("IO exception with file " + e.getMessage());
			e.printStackTrace();
		}
	}

}
