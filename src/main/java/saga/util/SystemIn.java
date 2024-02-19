package saga.util;

import java.util.Scanner;

public class SystemIn {

  public static String readLine() {
    Scanner scanner = new Scanner(System.in);
    return scanner.nextLine();
  }

  public static String readToken() {
    Scanner scanner = new Scanner(System.in);
    return scanner.next();
  }

}
