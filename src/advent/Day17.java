package advent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Day17 {

  private enum Tile {
    SAND(' '),
    CLAY('#'),
    FALLING_WATER('|'),
    RESTING_WATER('~'),
    LEFT_WATER('('),
    RIGHT_WATER(')');

    private final char icon;

    Tile(char icon) {
      this.icon = icon;
    }

    @Override
    public String toString() {
      return String.valueOf(icon);
    }
  }

  private static final EnumSet<Tile> ALL_WATER =
      EnumSet.of(Tile.FALLING_WATER, Tile.RESTING_WATER, Tile.LEFT_WATER, Tile.RIGHT_WATER);

  private static class World {
    private final Tile[][] world;
    private final int minX;
    private final int minY;

    public World(Tile[][] world, int minX, int minY) {
      this.world = world;
      this.minX = minX;
      this.minY = minY;
    }

    public Tile getTile(int x, int y) {
      return DataUtility.isInBounds(world, x - minX, y - minY) ? world[x - minX][y - minY] : Tile.SAND;
    }

    public Tile setTile(int x, int y, Tile tile) {
      world[x - minX][y - minY] = tile;
      return tile;
    }

    public int countTiles(EnumSet<Tile> tiles) {
      int count = 0;

      for (int x = getMinX(); x <= getMaxX(); x++) {
        for (int y = getMinY(); y <= getMaxY(); y++) {
          if (tiles.contains(getTile(x, y))) {
            count++;
          }
        }
      }

      return count;
    }
    
    public int getMinX() {
      return minX;
    }

    public int getMaxX() {
      return minX + world.length - 1;
    }

    public int getMinY() {
      return minY;
    }

    public int getMaxY() {
      return minY + world[0].length - 1;
    }

    @Override
    public String toString() {
      return DataUtility.matrixToString(world);
    }
  }

  private static World parseWorld(String[] lines) {
    List<int[]> clayRectangles = new ArrayList<>(lines.length);
    for (String line : lines) {
      int[] coords = ParseUtility.extractInts(line);
      int[] clayRectangle; // {xMin, xMax, minY, yMax}

      if (line.startsWith("x")) {
        // {xMin, xMax, minY, yMax}
        clayRectangle = new int[] {coords[0], coords[0], coords[1], coords[2]};
      } else { // if (line.startsWith("y"))
        // {xMin, xMax, minY, yMax}
        clayRectangle = new int[] {coords[1], coords[2], coords[0], coords[0]};
      }

      clayRectangles.add(clayRectangle);
    }

    int minX = clayRectangles.stream().mapToInt(c -> c[0]).min().getAsInt() - 1;
    int maxX = clayRectangles.stream().mapToInt(c -> c[1]).max().getAsInt() + 1;
    int minY = clayRectangles.stream().mapToInt(c -> c[2]).min().getAsInt();
    int maxY = clayRectangles.stream().mapToInt(c -> c[3]).max().getAsInt();

    Tile[][] world = new Tile[maxX - minX + 1][maxY - minY + 1];
    DataUtility.fillMatrix(world, Tile.SAND);

    for (int[] clayRectangle : clayRectangles) {
      for (int x = clayRectangle[0]; x <= clayRectangle[1]; x++) {
        for (int y = clayRectangle[2]; y <= clayRectangle[3]; y++) {
          world[x - minX][y - minY] = Tile.CLAY;
        }
      }
    }

    return new World(world, minX, minY);
  }

  public static void main(String[] args) throws IOException {
    String[] lines = FileUtility.fileToString("input/17.txt").split("\n");

    World world = parseWorld(lines);
    produceWater(world, 500, world.getMinY());

    // Part one
    FileUtility.printAndOutput(world.countTiles(ALL_WATER), "output/17a.txt");

    // Part two
    FileUtility.printAndOutput(world.countTiles(EnumSet.of(Tile.RESTING_WATER)), "output/17b.txt");
  }

  private static Tile produceWater(World world, int x, int y) {
    if (y > world.getMaxY()) {
      return Tile.FALLING_WATER;
    } else if (world.getTile(x, y) != Tile.SAND) {
      return world.getTile(x, y);
    }

    world.setTile(x, y, Tile.FALLING_WATER);

    Tile below = produceWater(world, x, y + 1);

    // If the conditions below allow water to spread...
    if (below == Tile.CLAY || below == Tile.RESTING_WATER) {
      // Earlier tiles are informed by later tiles that were blocked on one or both sides
      Tile left = produceWater(world, x - 1, y);
      Tile right = produceWater(world, x + 1, y);

      boolean blockedLeft = left == Tile.LEFT_WATER || left == Tile.CLAY;
      boolean blockedRight = right == Tile.RIGHT_WATER || right == Tile.CLAY;

      if (blockedLeft && blockedRight) {
        propagateRestingWater(world, x, y);
      } else if (blockedLeft) {
        world.setTile(x, y, Tile.LEFT_WATER);
      } else if (blockedRight) {
        world.setTile(x, y, Tile.RIGHT_WATER);
      } else {
        world.setTile(x, y, Tile.FALLING_WATER);
      }
    }

    return world.getTile(x, y);
  }

  private static void propagateRestingWater(World world, int x, int y) {
    if (world.getTile(x, y) == Tile.CLAY || world.getTile(x, y) == Tile.RESTING_WATER) {
      return;
    }

    world.setTile(x, y, Tile.RESTING_WATER);
    propagateRestingWater(world, x - 1, y);
    propagateRestingWater(world, x + 1, y);
  }
}
