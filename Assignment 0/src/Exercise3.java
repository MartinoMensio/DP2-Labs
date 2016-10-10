import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
