package advent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtility {
  public static int[] extractInts(String s) {
    String[] valuesText = s.split("\\D+");
    int[] values = values = new int[valuesText.length - 1];

    for (int i = 0; i < values.length; i++) {
      values[i] = Integer.parseInt(valuesText[i + 1]);
    }

    return values;
  }

  private static final Pattern INT_PATTERN = Pattern.compile("\\d+");

  public static List<Integer> extractIntegers(String s) {
    List<Integer> integers = new ArrayList<>();
    Matcher matcher = INT_PATTERN.matcher(s);
    while (matcher.find()) {
      integers.add(Integer.parseInt(matcher.group()));
    }

    return integers;
  }

  /** Returns group #1 from the first match found in the given string */
  public static String firstMatch(String s, Pattern p) {
    Matcher m = p.matcher(s);
    m.find();
    return m.group(1);
  }

  /** Returns group #1 (cast to an int) from the first match found in the given string */
  public static int firstMatchInt(String s, Pattern p) {
    Matcher m = p.matcher(s);
    m.find();
    return Integer.parseInt(m.group(1));
  }
}
