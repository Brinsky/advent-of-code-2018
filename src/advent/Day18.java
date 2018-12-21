package advent;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Day18 {

  private static final int PART_1_MINUTES = 10;
  private static final int PART_2_MINUTES = 1_000_000_000;

  private enum Acre {
    OPEN_GROUND('.'),
    TREES('|'),
    LUMBERYARD('#');

    private final char icon;

    Acre(char icon) {
      this.icon = icon;
    }

    @Override
    public String toString() {
      return String.valueOf(icon);
    }
  }

  private static Acre[][] parseWorld(String worldText) {
    String[] rows = worldText.split("\n");
    Acre[][] world = new Acre[rows[0].length()][rows.length];

    for (int x = 0; x < world.length; x++) {
      for (int y = 0; y < world[0].length; y++) {
        switch (rows[y].charAt(x)) {
          case '.':
            world[x][y] = Acre.OPEN_GROUND;
            break;
          case '|':
            world[x][y] = Acre.TREES;
            break;
          case '#':
            world[x][y] = Acre.LUMBERYARD;
            break;
        }
      }
    }

    return world;
  }

  private static int countSurrounding(Acre[][] world, int x, int y, Acre acreType) {
    int count = 0;

    for (int currentX = x - 1; currentX <= x + 1; currentX++) {
      for (int currentY = y - 1; currentY <= y + 1; currentY++) {
        if (!(currentX == x && currentY == y)
            && DataUtility.isInBounds(world, currentX, currentY)
            && world[currentX][currentY] == acreType) {
          count++;
        }
      }
    }

    return count;
  }

  public static void main(String[] args) throws IOException {
    Acre[][] world = parseWorld(FileUtility.fileToString("input/18.txt"));

    // Part one
    FileUtility.printAndOutput(getResourceValue(simulate(world, PART_1_MINUTES)), "output/18a.txt");

    // Part two
    FileUtility.printAndOutput(getResourceValue(simulate(world, PART_2_MINUTES)), "output/18b.txt");
  }

  private static long getResourceValue(Acre[][] world) {
    long treeCount =
        Arrays.stream(world).flatMap(Arrays::stream).filter(Acre.TREES::equals).count();
    long lumberyardCount =
        Arrays.stream(world).flatMap(Arrays::stream).filter(Acre.LUMBERYARD::equals).count();

    return treeCount * lumberyardCount;
  }

  private static Acre[][] simulate(Acre[][] world, int totalMinutes) {
    Map<String, Integer> previousAcres = new HashMap<>();

    for (int minute = 0; minute < totalMinutes; minute++) {
      // The strategy is to detect any time the world has returned to a previous state. If it's time
      // M and the current state previously occurred at time N, we know that this state will be
      // returned to again every (M - N) minutes. To save computation, we retain the state and skip
      // ahead by the maximum number of multiples of (M - N) possible while staying under
      // totalMinutes.
      if (previousAcres.containsKey(DataUtility.matrixToString(world))) {
        int delta = minute - previousAcres.get(DataUtility.matrixToString(world));
        int maxMultiple = (totalMinutes - minute) / delta;
        minute += maxMultiple * delta;
      } else {
        previousAcres.put(DataUtility.matrixToString(world), minute);
      }

      Acre[][] newWorld = new Acre[world.length][world[0].length];
      for (int x = 0; x < world.length; x++) {
        for (int y = 0; y < world[0].length; y++) {
          switch (world[x][y]) {
            case OPEN_GROUND:
              newWorld[x][y] =
                  countSurrounding(world, x, y, Acre.TREES) >= 3 ? Acre.TREES : Acre.OPEN_GROUND;
              break;
            case TREES:
              newWorld[x][y] =
                  countSurrounding(world, x, y, Acre.LUMBERYARD) >= 3
                      ? Acre.LUMBERYARD
                      : Acre.TREES;
              break;
            case LUMBERYARD:
              newWorld[x][y] =
                  (countSurrounding(world, x, y, Acre.LUMBERYARD) > 0
                          && countSurrounding(world, x, y, Acre.TREES) > 0)
                      ? Acre.LUMBERYARD
                      : Acre.OPEN_GROUND;
              break;
          }
        }
      }

      world = newWorld;
    }
    
    return world;
  }
}
