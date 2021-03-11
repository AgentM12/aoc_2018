import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class ScanTools {
	
	private static Scanner sc;
	private static FileInputStream fis;
	
	public static boolean hasNext() {
		return sc.hasNext();
	}
	
	public static String getNext() {
		return sc.nextLine();
	}
	
	public static void wrap(String input, Func f, int n) {
		initScanner(input);
		System.out.println("Part " + n + ": " + f.exec());
		endScanner();
	}
	
	private static void initScanner(String input) {
		if (sc != null) {
			endScanner();
		}
		try {
			fis = new FileInputStream(input);
			sc = new Scanner(fis);
		} catch (IOException ignored) {
			System.exit(-1);
		}
	}
	
	private static void endScanner() {
		try {
			fis.close();
		} catch (IOException ignored) {
		}
		sc.close();
	}
	
	public interface Func {
		Object exec();
	}
	
	public static boolean reset(String input) {
		try {
			endScanner();
			initScanner(input);
		} catch (Exception ignored) {
			return false;
		}
		return true;
	}
}
