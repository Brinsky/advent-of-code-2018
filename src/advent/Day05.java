package advent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Day05 {
  private static class Unit {
    public final char type;

    public Unit next = null;

    public Unit(char type) {
      this.type = type;
    }

    public boolean willReact(Unit other) {
      if (other == null) {
        return false;
      }

      return type != other.type && Character.toLowerCase(type) == Character.toLowerCase(other.type);
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      for (Unit current = this; current != null; current = current.next) {
        builder.append(current.type);
      }
      return builder.toString();
    }

    public int getLength() {
      int length = 0;
      for (Unit current = this; current != null; current = current.next) {
        length++;
      }
      return length;
    }
  }

  public static void main(String[] args) throws IOException {
    String polymer = FileUtility.fileToString("input/05.txt");

    // Part one
    // .next to drop leading dummy unit
    Unit polymerChain = performAllReactions(constructPolymerChain(polymer)).next;
    FileUtility.printAndOutput(polymerChain.getLength(), "output/05a.txt");

    // Part two
    FileUtility.printAndOutput(
        getShortestLengthWithOmittedChar(polymer, getUniqueCharacters(polymer)), "output/05b.txt");
  }

  private static int getShortestLengthWithOmittedChar(String polymer, Set<Character> charsToOmit) {
    int shortestLength = Integer.MAX_VALUE;
    for (Character unitType : charsToOmit) {
      // .next to drop leading dummy unit
      int length = performAllReactions(constructPolymerChain(polymer, unitType)).next.getLength();
      if (length < shortestLength) {
        shortestLength = length;
      }
    }
    return shortestLength;
  }

  private static Set<Character> getUniqueCharacters(String polymer) {
    Set<Character> unitTypes = new HashSet<Character>();
    for (char c : polymer.toCharArray()) {
      unitTypes.add(Character.toLowerCase(c));
    }
    return unitTypes;
  }

  private static Unit constructPolymerChain(String polymer) {
    return constructPolymerChain(polymer, '\0');
  }

  private static Unit constructPolymerChain(String polymer, char charToIgnore) {
    Unit polymerChain = new Unit('!');
    Unit current = polymerChain;
    for (int i = 0; i < polymer.length(); i++) {
      // Only add characters that don't match the "to ignore" char
      if (Character.toLowerCase(polymer.charAt(i)) != charToIgnore) {
        current.next = new Unit(polymer.charAt(i));
        current = current.next;
      }
    }
    return polymerChain;
  }

  // O(n^2): Runs slightly slow on part two
  private static Unit performAllReactions(Unit polymerChain) {
    Unit current;
    boolean reactionOccurred;
    do {
      reactionOccurred = false;
      current = polymerChain;
      while (current.next != null && current.next.next != null) {
        if (current.next.willReact(current.next.next)) {
          current.next = current.next.next.next;
          reactionOccurred = true;
        } else {
          current = current.next;
        }
      }
    } while (reactionOccurred);

    return polymerChain;
  }
}
