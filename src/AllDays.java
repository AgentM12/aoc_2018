import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

/**
 * Simple utility class to print all the solutions of days of code.
 * 
 * The format it prints is === Day <day number> === Part 1: <solution 1> Part 2: <solution 2>
 * 
 * The solution 1 and 2 usually are numbers, occasionally strings of alphanumeric characters and rarely otherwise.
 * 
 * @author Melvin
 */
public class AllDays {
	
	/**
	 * The year of the challenges, used to calculate how many days before Christmas to reveal the answers.
	 */
	private static final int THIS_YEAR = 2018;
	
	/**
	 * Driver method. Simply prints all the results of all the days of code, starting with day 1 till 25. In December of
	 * this year, every day closer to day 25 will print one more day as it uses the system time to determine how many days
	 * away from Christmas it is, so that Day 1 - That day is printed.
	 * 
	 * It uses reflection to access the code. So don't refactor any class names. The format is as follows: Day01, Day02,
	 * Day03 ... Day24, Day25
	 * 
	 * @param args None.
	 * @throws ClassNotFoundException if you tampered with the classes.
	 * @throws IllegalAccessException if the classes are somehow not accessible.
	 * @throws IllegalArgumentException if a wrong class name is passed.
	 * @throws InvocationTargetException if the method `main` can't be called on the class.
	 * @throws NoSuchMethodException if there is no method `main` in the class.
	 * @throws SecurityException if something else happened security wise.
	 */
	public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		long now = System.currentTimeMillis();
		System.out.println("======= ADVENT OF CODE - 2018 =======\n");
		
		Calendar cal = Calendar.getInstance();
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);
		if (year > THIS_YEAR || dayOfMonth > 25) {
			dayOfMonth = 25;
		}
		
		for (int i = 1; i <= dayOfMonth; i++) {
			Class<?> c = Class.forName("Day" + String.format("%02d", i));
			Method method = c.getMethod("main", String[].class);
			method.invoke(null, (Object) args);
		}
		System.out.println("Time taken: " + (System.currentTimeMillis() - now) / 1000d + " seconds!");
	}
}
