
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.*;

public class Exercise2 {

	public static void main(String[] args) {
		// Get the "it.polito.dp2.file" system property
		String dir = System.getProperty("it.polito.dp2.file");
		GregorianCalendar max = null;
		try {
			max = Files.lines(Paths.get(dir))
			.map((String s) -> {
				String[] tokens = s.split("\\s");
				Object[] numbers = Stream.of(tokens).map(s2 -> Integer.parseInt(s2)).toArray();
				return new GregorianCalendar((Integer)numbers[5], (Integer)numbers[4], (Integer)numbers[3], (Integer)numbers[0], (Integer)numbers[1], (Integer)numbers[2]);
			})
			.max((a, b) -> a.compareTo(b))
			.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		fmt.setCalendar(max);
		String dateFormatted = fmt.format(max.getTime());
		System.out.println(dateFormatted);
	}

}
