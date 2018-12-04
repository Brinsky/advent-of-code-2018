package advent;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Day04 {

  private static class Guard {
    private final int id;
    private final int[] sleepFrequencies = new int[60];

    private int minutesAsleep = 0;

    /* The minute was most often spent sleeping */
    private int sleepiestMinute = 0;

    /* How often the sleepiest minute was spent sleeping */
    private int maxFrequency = 0;

    public Guard(int id) {
      this.id = id;
    }

    public void markSleep(int sleepStart, int sleepEnd) {
      for (int i = sleepStart; i < sleepEnd; i++) {
        sleepFrequencies[i]++;
        minutesAsleep++;

        if (sleepFrequencies[i] > maxFrequency) {
          sleepiestMinute = i;
          maxFrequency = sleepFrequencies[i];
        }
      }
    }

    public int getId() {
      return id;
    }

    public int getSleepiestMinute() {
      return sleepiestMinute;
    }

    public int getMaxFrequency() {
      return maxFrequency;
    }

    public int getMinutesAsleep() {
      return minutesAsleep;
    }
  }

  private static final Pattern GUARD_ID_PATTERN = Pattern.compile("#(\\d+)");
  private static final Pattern MINUTE_PATTERN = Pattern.compile("(\\d\\d)]");

  public static void main(String[] args) throws IOException {
    String[] events = FileUtility.fileToString("input/04.txt").split("\n");
    Arrays.sort(events);

    Collection<Guard> guards = initializeGuards(events);

    // Part one
    Guard strategyOneGuard =
        guards.stream().max(Comparator.comparingInt(Guard::getMinutesAsleep)).get();
    FileUtility.printAndOutput(
        strategyOneGuard.getId() * strategyOneGuard.getSleepiestMinute(), "output/04a.txt");

    // Part two
    Guard strategyTwoGuard =
        guards.stream().max(Comparator.comparingInt(Guard::getMaxFrequency)).get();
    FileUtility.printAndOutput(
        strategyTwoGuard.getId() * strategyTwoGuard.getSleepiestMinute(), "output/04b.txt");
  }

  private static Collection<Guard> initializeGuards(String[] events) {
    Map<Integer, Guard> guards = new HashMap<>();

    Guard currentGuard = new Guard(-1);
    int sleepStart = -1;
    for (String event : events) {
      int time = ParseUtility.firstMatchInt(event, MINUTE_PATTERN);
      if (event.contains("Guard")) {
        currentGuard = getGuard(guards, ParseUtility.firstMatchInt(event, GUARD_ID_PATTERN));
      } else if (event.contains("falls asleep")) {
        sleepStart = time;
      } else { // if (event.contains("wakes up")) {
        currentGuard.markSleep(sleepStart, time);
      }
    }

    return guards.values();
  }

  private static Guard getGuard(Map<Integer, Guard> guards, int id) {
    if (guards.containsKey(id)) {
      return guards.get(id);
    }

    Guard guard = new Guard(id);
    guards.put(id, guard);
    return guard;
  }
}
