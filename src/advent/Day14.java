package advent;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class Day14 {

  private static final int NUM_ELVES = 2;
  private static final Integer[] STARTING_SCORES = new Integer[] {3, 7};
  private static final int TRAILING_SCORES = 10;

  private static Collection<Integer> getDigits(int number) {
    ArrayDeque<Integer> digits = new ArrayDeque<>();

    if (number == 0) {
      digits.add(0);
      return digits;
    }

    for (int i = number; i > 0; i /= 10) {
      digits.addFirst(i % 10);
    }
    return digits;
  }

  public static void main(String[] args) throws IOException {
    final int baseScoreCount = Integer.parseInt(FileUtility.fileToString("input/14.txt"));
    List<Integer> digits = new ArrayList<>(getDigits(baseScoreCount));

    List<Integer> scores = generateEnoughScores(baseScoreCount, digits);

    // Part one
    FileUtility.printAndOutput(getTrailingScores(baseScoreCount, scores), "output/14a.txt");

    // Part two
    FileUtility.printAndOutput(Collections.indexOfSubList(scores, digits), "output/14b.txt");
  }

  private static String getTrailingScores(int baseScoreCount, List<Integer> scores) {
    StringBuilder builder = new StringBuilder(TRAILING_SCORES);
    for (int i = 0; i < TRAILING_SCORES; i++) {
      builder.append(scores.get(baseScoreCount + i));
    }
    return builder.toString();
  }

  private static List<Integer> generateEnoughScores(int baseScoreCount, List<Integer> digits) {
    final int minimumNumScores = baseScoreCount + TRAILING_SCORES;

    List<Integer> scores = new ArrayList<>(Arrays.asList(STARTING_SCORES));
    int[] elves = IntStream.range(0, NUM_ELVES).toArray();

    boolean containsDigits = false;
    while (scores.size() < minimumNumScores || !containsDigits) {
      int sum = Arrays.stream(elves).map(scores::get).sum();
      scores.addAll(getDigits(sum));

      for (int i = 0; i < elves.length; i++) {
        elves[i] = (elves[i] + 1 + scores.get(elves[i])) % scores.size();
      }

      if (!containsDigits) {
        // Determine the absolute minimum index that could be the start of a match (based on the
        // fact that all lower indices were previously checked)
        int lowerBound = Math.max(0, scores.size() - digits.size() - 2);
        containsDigits =
            Collections.indexOfSubList(scores.subList(lowerBound, scores.size()), digits) >= 0;
      }
    }

    return scores;
  }
}
