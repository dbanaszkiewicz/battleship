package console;

import java.io.Console;
import java.util.Arrays;
import java.util.Scanner;

public class ConsoleHelper {
    public static void clear() {
        for(int clear = 0; clear < 100; clear++)
        {
            System.out.println(" ") ;
        }
    }

    public static boolean scanBooleanValue(String queryString) {
        String opt;
        while (true) {
            System.out.print(queryString + " [T/N] ");
            Scanner in = new Scanner(System.in);
            opt = in.nextLine();

            if (opt.toUpperCase().equals("T") || opt.toUpperCase().equals("Y")) {
                return true;
            } else if (opt.toUpperCase().equals("N")) {
                return false;
            }

            System.out.println("Niepoprawny wybór!");
        }
    }

    public static void waitForEnter(String message) {
        System.out.println(message);
        new Scanner(System.in).nextLine();
    }

    public static int parseToInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 99999;
        }
    }

    public static String readPassword(String message) {
        Console cons = System.console();
        char[] pass = cons.readPassword(message + ": ");
        return Arrays.toString(pass);
    }
 }
