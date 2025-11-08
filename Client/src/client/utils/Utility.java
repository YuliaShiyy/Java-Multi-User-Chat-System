package client.utils;

import java.util.Scanner;

/**
 * @author Yulia
 * @version 2.0
 */
public class Utility {
    private static Scanner scanner = new Scanner(System.in);


    /**
     * Function: Reads a menu option entered from the keyboard; value: range 1-5.
     * @return 1——5
     */
    public static char readMenuSelection() {
        char c;
        for (; ; ) {
            String str = readKeyBoard(1, false);
            c = str.charAt(0);
            if (c != '1' && c != '2' &&
                    c != '3' && c != '4' && c != '5') {
                System.out.print("Please re-enter if you selected the wrong option:");
            } else break;
        }
        return c;
    }

    /**
     * Function: Reads a character input from the keyboard.
     * @return a char
     */
    public static char readChar() {
        String str = readKeyBoard(1, false);
        return str.charAt(0);
    }
    /**
     * Function: Reads a character entered from the keyboard.
     * If Enter is pressed directly, it returns the specified default value;
     * otherwise, it returns the entered character.
     * @param defaultValue Specified default value
     * @return Default value or entered character
     */

    public static char readChar(char defaultValue) {
        String str = readKeyBoard(1, true);
        return (str.length() == 0) ? defaultValue : str.charAt(0);
    }

    /**
     * Function: Reads integer input from the keyboard, with a length of less than 2 digits.
     * @return Integer
     */
    public static int readInt() {
        int n;
        for (; ; ) {
            String str = readKeyBoard(10, false);
            try {
                n = Integer.parseInt(str);
                break;
            } catch (NumberFormatException e) {
                System.out.print("The number was entered incorrectly, please re-enter it:");
            }
        }
        return n;
    }
    /**
     * Function: Reads an integer or default value from keyboard input.
     * If Enter is pressed directly, the default value is returned;
     * otherwise, the integer input is returned.
     * @param defaultValue Specified default value
     * @return Integer or default value
     */
    public static int readInt(int defaultValue) {
        int n;
        for (; ; ) {
            String str = readKeyBoard(10, true);
            if (str.equals("")) {
                return defaultValue;
            }

            try {
                n = Integer.parseInt(str);
                break;
            } catch (NumberFormatException e) {
                System.out.print("The number was entered incorrectly, please re-enter it:");
            }
        }
        return n;
    }

    /**
     * 功能：Read a string of a specified length from keyboard input.
     * @param limit Limiting length
     * @return String of specified length
     */

    public static String readString(int limit) {
        return readKeyBoard(limit, false);
    }

    /**
     * Function: Reads a string or default value from keyboard input of a specified length.
     * If Enter is pressed directly, it returns the default value;
     * otherwise, it returns the string.
     * @param limit Limiting length
     * @param defaultValue Specified default value
     * @return String of specified length
     */

    public static String readString(int limit, String defaultValue) {
        String str = readKeyBoard(limit, true);
        return str.equals("")? defaultValue : str;
    }


    /**
     * Function: Reads the confirmation option (Y or N) entered from the keyboard.
     * Encapsulate small functionalities into a single method.
     * @return Y/N
     */
    public static char readConfirmSelection() {
        System.out.println("Please enter your choice (Y/N): Please select carefully.");
        char c;
        for (; ; ) {
            //y => Y n=>N
            String str = readKeyBoard(1, false).toUpperCase();
            c = str.charAt(0);
            if (c == 'Y' || c == 'N') {
                break;
            } else {
                System.out.print("Please re-enter if you selected the wrong option:");
            }
        }
        return c;
    }

    /**
     * 功能： Reads a string
     * @param limit The length to read
     * @param blankReturn If true, it means an empty string can be read.
     * 					  If false, it means an empty string cannot be read.
     *
     *	If the input is empty, or the input length is greater than limit,
     *                   	it will prompt you to re-enter.
     * @return
     */
    private static String readKeyBoard(int limit, boolean blankReturn) {

        String line = "";

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();

            if (line.length() == 0) {
                if (blankReturn) return line;// If blankReturn=true, an empty string can be returned.
                else continue;// If blankReturn=false, empty strings are not accepted; input is required.
            }

            if (line.length() < 1 || line.length() > limit) {
                System.out.print("The input length cannot be greater than " + limit + ". Please re-enter:");
                continue;
            }
            break;
        }

        return line;
    }
}


