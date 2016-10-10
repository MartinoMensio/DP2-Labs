
import java.nio.file.*;
import java.util.*;

public class Exercise1 {

	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.println("Provide a parameter");
			return;
		}
		try {
			List<String> dictionaryRows = Files.readAllLines(Paths.get("dictionary.txt"));
			Files.lines(Paths.get(args[0]))
			.filter(s -> !dictionaryRows.contains(s))
			.forEach(s -> System.out.println(s));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
