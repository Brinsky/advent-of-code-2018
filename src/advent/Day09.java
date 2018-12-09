package advent;

import java.io.IOException;
import java.util.List;

public class Day09 {
  private static class Marble {
    public Marble previous;
    public Marble next;
    public final int value;

    public Marble(int value) {
      this.value = value;
    }

    /** Positive value of {@code i} is clockwise, negative value is counter-clockwise. */
    public Marble getMarble(int i) {
      Marble current = this;
      if (i < 0) {
        for (int j = 0; j < -i; j++) {
          current = current.previous;
        }
      } else {
        for (int j = 0; j < i; j++) {
          current = current.next;
        }
      }

      return current;
    }

    public Marble removeMarble(int i) {
      Marble current = getMarble(i);

      current.next.previous = current.previous;
      current.previous.next = current.next;

      current.previous = null;
      current.next = null;

      return current;
    }

    public Marble insertMarble(int i, int value) {
      Marble current = getMarble(i);
      Marble toInsert = new Marble(value);

      if (i > 0) {
        // Insert behind current
        toInsert.next = current;
        toInsert.previous = current.previous;

        current.previous.next = toInsert;
        current.previous = toInsert;
      } else {
        // Insert in front of current
        toInsert.previous = current;
        toInsert.next = current.next;

        current.next.previous = toInsert;
        current.next = toInsert;
      }

      return toInsert;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      Marble current = this;
      do {
        builder.append(current.value).append(' ');
        current = current.next;
      } while (current != null && current != this);
      return builder.toString();
    }
  }

  public static void main(String[] args) throws IOException {
    List<Integer> params = ParseUtility.extractIntegers(FileUtility.fileToString("input/09.txt"));

    final int NUM_PLAYERS = params.get(0);
    final int HIGHEST_MARBLE = params.get(1);

    // Part one
    FileUtility.printAndOutput(getHighScore(NUM_PLAYERS, HIGHEST_MARBLE), "output/09a.txt");

    // Part two
    FileUtility.printAndOutput(getHighScore(NUM_PLAYERS, HIGHEST_MARBLE * 100), "output/09b.txt");
  }

  private static long getHighScore(int numPlayers, int highestMarble) {
    long[] playerScores = new long[numPlayers];

    Marble currentMarble = new Marble(0);
    currentMarble.next = currentMarble;
    currentMarble.previous = currentMarble;
    int currentPlayer = 0;
    for (int nextMarble = 1; nextMarble <= highestMarble; nextMarble++) {
      if (nextMarble % 23 == 0) {
        playerScores[currentPlayer] += nextMarble;

        currentMarble = currentMarble.getMarble(-6);
        playerScores[currentPlayer] += currentMarble.removeMarble(-1).value;
      } else {
        // Insert clockwise, between positions 1 and 2
        currentMarble = currentMarble.insertMarble(2, nextMarble);
      }
      currentPlayer = (currentPlayer + 1) % numPlayers;
    }
    return max(playerScores);
  }

  private static long max(long... values) {
    long max = Long.MIN_VALUE;
    for (long value : values) {
      if (value > max) {
        max = value;
      }
    }
    return max;
  }
}
