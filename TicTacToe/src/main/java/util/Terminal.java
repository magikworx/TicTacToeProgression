package util;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Terminal {
  public static InputStream istream = System.in;
  public static PrintStream ostream = System.out;
  public static PrintStream estream = System.err;
  public static void println(String output) {
    ostream.println(output);
  }
  public static void print(String output) {
    ostream.print(output);
  }
  public static void errorln(String output) {
    estream.println(output);
  }
  public static void error(String output) {
    estream.print(output);
  }

  public static int getInt(String prompt) {
    print(prompt);
    return new Scanner(istream).nextInt();
  }
  
  public static char getChar(String prompt) {
    return getString(prompt, "Please input a character").charAt(0);
  }
  
  public static String getString(String prompt) {
    return getString(prompt, "You must enter at least one word");
  }

  public static String getString(String prompt, String emptyError) {
    Scanner scanner = new Scanner(istream);
    print(prompt);
    String input = scanner.nextLine();
    while (input.isEmpty())
    {
      println(emptyError);
      print(prompt);
      input = scanner.nextLine();
    }
    return input;
  }
  
  public static int getIntFromRange(String prompt, int start, int stop) {
    int number = getInt(prompt);
    while (number < start || number > stop)
    {
      println("Number is not in range");
      number = getInt(prompt);
    }
    return number;
  }
  
  public static int getCharFromRangeCaseInsensitive(String prompt, char start, char stop) {
    char character = getChar(prompt);
    while (character < start || character > stop)
    {
      println("Character is not in range");
      character = getChar(prompt);
    }
    return character;
  }
  
  public static int getIntFromChoice(String prompt, int...choices) {
    var possibles = new ArrayList<Integer>();
    for(var i: choices) {
      possibles.add(i);
    }
    
    int number = getInt(prompt);
    while (!possibles.contains(number)) {
      println("Input is not a valid choice");
      number = getInt(prompt);
    }
    return number;
  }
  
  public static char getCharFromChoice(String prompt, char...choices) {
    var possibles = new ArrayList<Character>();
    for(var c: choices) {
      possibles.add(c);
    }
    
    char character = getChar(prompt);
    while (!possibles.contains(character)) {
      println("Input is not a valid choice");
      character = getChar(prompt);
    }
    return character;
  }

  public static char getCharFromChoiceCaseInsensitive(String prompt, char...choices) {
    var possibles = new ArrayList<Character>();
    for(var c: choices) {
      possibles.add(Character.toLowerCase(c));
      possibles.add(Character.toUpperCase(c));
    }
    
    char character = getChar(prompt);
    while (!possibles.contains(character)) {
      println("Input is not a valid choice");
      character = getChar(prompt);
    }
    return Character.toLowerCase(character);
  }
}
