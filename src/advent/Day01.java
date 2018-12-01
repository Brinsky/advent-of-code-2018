package advent;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day01 {
  public static void main(String[] args) throws IOException {
    List<Integer> frequencyChanges =
        Arrays.stream(FileUtility.fileToString("input/01.txt").split("\n"))
            .map(Integer::parseInt)
            .collect(Collectors.toList());

    // Part one
    FileUtility.printAndOutput(
        frequencyChanges.stream().mapToInt(Integer::intValue).sum(), "output/01a.txt");

    // Part two
    Set<Integer> pastFrequencies = new HashSet<>(frequencyChanges.size());
    int currentFrequency = 0;
    int index = 0;
    do {
      pastFrequencies.add(currentFrequency);
      currentFrequency += frequencyChanges.get(index);
      index = (index + 1) % frequencyChanges.size();
    } while (!pastFrequencies.contains(currentFrequency));

    FileUtility.printAndOutput(currentFrequency, "output/01b.txt");
  }
}
