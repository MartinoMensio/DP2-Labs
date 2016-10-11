
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.*;

public class Exercise2 {

	public static void main(String[] args) {
		// Get the "it.polito.dp2.file" system property
		String dir = System.getProperty("it.polito.dp2.file");
		GregorianCalendar max = null;
		Optional<GregorianCalendar> maxOpt = null;
		try {
			maxOpt = Files.lines(Paths.get(dir))
			.map((String s) -> {
				String[] tokens = s.split("\\s");
				if(tokens.length != 6) {
					System.err.println("Wrong format: expected 6 tokens in line: " + s);
					return null;
				}
				Object[] numbers = Stream.of(tokens).map(s2 -> Integer.parseInt(s2)).toArray();
				return new GregorianCalendar((Integer)numbers[5], (Integer)numbers[4] - 1, (Integer)numbers[3], (Integer)numbers[0], (Integer)numbers[1], (Integer)numbers[2]);
			})
			.max((a, b) -> a.compareTo(b));
			if(maxOpt.isPresent()) {
				max = maxOpt.get();
			} else {
				System.err.println("No valid lines found");
				return;
			}
		} catch (IOException e) {
			System.err.println("IO exception with file " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		fmt.setCalendar(max);
		String dateFormatted = fmt.format(max.getTime());
		System.out.println(dateFormatted);
	}

}
