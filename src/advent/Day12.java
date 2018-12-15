package advent;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Day12 {
  private static final Pattern PLANT_PATTERN = Pattern.compile("([#.]+)");
  private static final int MAX_SPREAD_PER_GENERATION = 2;
  private static final int NUM_GENERATIONS_PART_1 = 20;
  private static final int PATTERN_LENGTH = 5;
  private static final long NUM_GENERATIONS_PART_2 = 50_000_000_000L;

  public static void main(String[] args) throws IOException {
    String[] input = FileUtility.fileToString("input/12.txt").split("\n");

    String initialState = ParseUtility.firstMatch(input[0], PLANT_PATTERN);
    Map<String, Character> rules = parseRules(Arrays.copyOfRange(input, 2, input.length));

    // Part one
    FileUtility.printAndOutput(
        getSum(getFinalState(initialState, rules, NUM_GENERATIONS_PART_1)), "output/12a.txt");

    // Part two
    // Pretty inefficient with the current implementation of getFinalState (previous generations are
    // recomputed each time a new generation is tested). Should be replaced with a 'nextState'
    // function.
    FileUtility.printAndOutput(
        getLongTermSum(rules, initialState, NUM_GENERATIONS_PART_2), "output/12b.txt");
  }

  private static Map<String, Character> parseRules(String[] rulesText) {
    Map<String, Character> rules = new HashMap<>(rulesText.length - 2);
    for (int i = 0; i < rulesText.length; i++) {
      String[] rule = rulesText[i].split("\\s+=>\\s+");

      // Ignore rules that have no effect, e.g. "..#.. => #"
      if (rule[0].charAt(2) != rule[1].charAt(0)) {
        rules.put(rule[0], rule[1].charAt(0));
      }
    }

    if (rules.containsKey(".....")) {
      throw new IllegalArgumentException("Rule with infinitely many matches");
    }
    return rules;
  }

  /**
   * Assumes that eventually generations will become "isomorphic" to previous generations, i.e.
   * containing the same pattern of plants, but shifted. Computes generations up to this point, then
   * extrapolates.
   */
  private static long getLongTermSum(
      Map<String, Character> rules, String initialState, long numGenerations) {
    State previousState;
    State currentState = new State(initialState, 0);
    int generation = 0;
    do {
      generation++;
      previousState = currentState;
      currentState = getFinalState(initialState, rules, generation);
    } while (!isIsomorphic(previousState.state, currentState.state));

    int difference = getSum(currentState) - getSum(previousState);
    return getSum(currentState) + difference * (numGenerations - generation);
  }

  private static class State {
    public final String state;
    public final int zeroIndex;

    private State(String state, int zeroIndex) {
      this.state = state;
      this.zeroIndex = zeroIndex;
    }
  }

  private static boolean isIsomorphic(String a, String b) {
    a = a.replaceAll("^\\.+", "").replaceAll("\\.+$", "");
    b = b.replaceAll("^\\.+", "").replaceAll("\\.+$", "");

    return a.equals(b);
  }

  private static int getSum(State state) {
    int sum = 0;
    for (int i = 0; i < state.state.length(); i++) {
      if (state.state.charAt(i) == '#') {
        sum += i - state.zeroIndex;
      }
    }
    return sum;
  }

  // TODO: Replace with incremental 'nextState' version to prevent rerunning previous generations
  private static State getFinalState(
      String initialState, Map<String, Character> rules, int numGenerations) {
    final int bufferSize = MAX_SPREAD_PER_GENERATION * numGenerations;
    char[] plants = new char[bufferSize * 2 + initialState.length()];
    Arrays.fill(plants, '.');
    for (int i = 0; i < initialState.length(); i++) {
      plants[bufferSize + i] = initialState.charAt(i);
    }

    String currentGeneration = new String(plants);
    for (int generations = 0; generations < numGenerations; generations++) {
      for (int i = 0; i < plants.length - PATTERN_LENGTH; i++) {
        String currentPattern = currentGeneration.substring(i, i + PATTERN_LENGTH);
        if (rules.containsKey(currentPattern)) {
          plants[i + 2] = rules.get(currentPattern);
        }
      }
      currentGeneration = new String(plants);
    }

    return new State(currentGeneration, bufferSize);
  }
}
