package advent;

import java.io.IOException;
import java.util.Arrays;

public class Day02 {
  public static void main(String[] args) throws IOException {
    String[] boxIds = FileUtility.fileToString("input/02.txt").split("\n");

    // Part one
    long exactlyTwoCount = Arrays.stream(boxIds).filter(id -> hasExactLetterCount(id, 2)).count();
    long exactlyThreeCount = Arrays.stream(boxIds).filter(id -> hasExactLetterCount(id, 3)).count();

    FileUtility.printAndOutput(exactlyTwoCount * exactlyThreeCount, "output/02a.txt");

    // Part two
    FileUtility.printAndOutput(findSingleDifference(boxIds), "output/02b.txt");
  }

  private static boolean hasExactLetterCount(String s, int desiredCount) {
    char[] letters = s.toCharArray();
    Arrays.sort(letters);

    int currentCount = 1;
    for (int i = 1; i < letters.length - 1; i++) {
      if (letters[i] == letters[i - 1]) {
        currentCount++;
      } else if (currentCount == desiredCount) {
        return true;
      } else {
        currentCount = 1;
      }
    }

    return currentCount == desiredCount;
  }

  /**
   * Returns -1 if strings differ in any more (or less) than one position. Otherwise, returns the
   * index of the difference.
   */
  private static int indexOfSingleDifference(String a, String b) {
    int indexOfDifference = -1;
    boolean sawDifference = false;
    for (int i = 0; i < a.length(); i++) {
      if (a.charAt(i) != b.charAt(i)) {
        // If we've already seen a difference, there's more than one
        if (sawDifference) {
          return -1;
        }

        sawDifference = true;
        indexOfDifference = i;
      }
    }

    return indexOfDifference;
  }

  /**
   * Finds the first pair of strings in the array that differ in exactly one position. Returns the
   * common characters left in both strings after removing the differing character.
   */
  private static String findSingleDifference(String[] strings) {
    for (int i = 0; i < strings.length; i++) {
      for (int j = i + 1; j < strings.length; j++) {
        if (i != j) {
          int index = indexOfSingleDifference(strings[i], strings[j]);

          if (index >= 0) {
            return new StringBuilder(strings[i]).deleteCharAt(index).toString();
          }
        }
      }
    }

    return "";
  }
}
