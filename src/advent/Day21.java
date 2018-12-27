package advent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Day21 {

  private static int getLastUniqueValueOfR2(int numIterations) {
    Set<Integer> valuesOfR2 = new HashSet<>();
    int latestUniqueValue = -1;
    int r1;
    int r2 = 0;
    for (int i = 0; i < numIterations; i++) {
      r1 = r2 | 0b10000000000000000;
      r2 = 0b100110001010101001010;
      while (true) {
        r2 += r1 & 0b11111111;
        r2 &= 0b111111111111111111111111;
        r2 *= 65899;
        r2 &= 16777215;
        if (r1 < 256) {
          break;
        }
        r1 /= 256;
      }

      if (!valuesOfR2.contains(r2)) {
        latestUniqueValue = r2;
        valuesOfR2.add(r2);
      }
    }

    return latestUniqueValue;
  }

  public static void main(String[] args) throws IOException {
    // See input/21-analysis.txt for analysis of the assembly code.
    // - The purpose of the program seems to be generating random values and storing them in R2
    // - The outer loop ends when the value of R2 generated during a given iteration equals R0
    // - R2 is always forced to be less than 2^24
    // Basically, the question of "how many operations will the program take?" boils down to the
    // question of how many times the outer loop will run. Since there are only 2^24 values of R2
    // and every variable in the loop is dependent on the value of R2, all of the values it takes on
    // must appear within the first 2^24 iterations.

    // Part one
    // The solution is the very first value of R2 produced by the outer loop
    FileUtility.printAndOutput(getLastUniqueValueOfR2(1), "output/21a.txt");

    // Part two
    // The solution is the latest unique value of R2 produced during the first 2^24 iterations.
    FileUtility.printAndOutput(getLastUniqueValueOfR2(1 << 24), "output/21b.txt");
  }
}
