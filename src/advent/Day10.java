package advent;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Day10 {

  private static final int NUM_TRIALS = 15000; // Determined by trial-and-error

  public static class Position implements Comparable<Position> {
    public final int x;
    public final int y;

    public Position(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Position)) {
        return false;
      }

      Position p = (Position) obj;
      return p.x == x && p.y == y;
    }

    public Position copy() {
      return new Position(x, y);
    }

    public Position getOffset(int deltaX, int deltaY) {
      return new Position(x + deltaX, y + deltaY);
    }

    public Position add(Position other) {
      return new Position(x + other.x, y + other.y);
    }

    @Override
    public int hashCode() {
      return x * 3 + y * 7;
    }

    @Override
    public String toString() {
      return "(" + x + ", " + y + ")";
    }

    @Override
    public int compareTo(Position other) {
      if (y != other.y) {
        return Integer.compare(y, other.y);
      }

      return Integer.compare(x, other.x);
    }
  }

  private static class Point {
    public final Position initialPosition;
    public Position currentPosition;
    public int xVelocity;
    public int yVelocity;

    public Point(String specification) {
      List<Integer> args = ParseUtility.extractIntegers(specification);
      initialPosition = new Position(args.get(0), args.get(1));
      currentPosition = initialPosition.copy();
      xVelocity = args.get(2);
      yVelocity = args.get(3);
    }

    public void move(int timeDelta) {
      currentPosition = currentPosition.getOffset(xVelocity * timeDelta, yVelocity * timeDelta);
    }
  }

  public static void main(String[] args) throws IOException {
    List<Point> points =
        Arrays.stream(FileUtility.fileToString("input/10.txt").split("\n"))
            .map(Point::new)
            .collect(Collectors.toList());

    Set<Position> occupiedPositions = new HashSet<>(points.size());
    int timeOfMax = getTimeOfMaxPercentWithNeighbors(occupiedPositions, points);

    // Part one
    FileUtility.printAndOutput(gridToString(occupiedPositions), "output/10a.txt");

    // Part two
    FileUtility.printAndOutput(timeOfMax, "output/10b.txt");
  }

  private static int getTimeOfMaxPercentWithNeighbors(Set<Position> occupiedPositions, List<Point> points) {
    movePoints(occupiedPositions, points, 0);

    double maxPercentWithNeighbors = 0;
    int timeOfMax = 0;

    int time;
    for (time = 0; time < NUM_TRIALS; time++) {
      double percentWithNeighbors = percentWithNeighbors(occupiedPositions);
      if (percentWithNeighbors > maxPercentWithNeighbors) {
        maxPercentWithNeighbors = percentWithNeighbors;
        timeOfMax = time;
      }

      movePoints(occupiedPositions, points, 1);
    }

    movePoints(occupiedPositions, points, timeOfMax - time);
    return timeOfMax;
  }

  private static void movePoints(Set<Position> occupiedPositions, List<Point> points, int deltaTime) {
    occupiedPositions.clear();
    for (Point point : points) {
      point.move(deltaTime);
      occupiedPositions.add(point.currentPosition);
    }
  }

  private static double percentWithNeighbors(Set<Position> occupiedPositions) {
    return occupiedPositions.stream().filter(p -> hasNeighbors(occupiedPositions, p)).count()
        / (double) occupiedPositions.size();
  }

  private static boolean hasNeighbors(Set<Position> occupiedPositions, Position position) {
    return occupiedPositions.contains(position.getOffset(-1, 0))
        || occupiedPositions.contains(position.getOffset(+1, 0))
        || occupiedPositions.contains(position.getOffset(0, -1))
        || occupiedPositions.contains(position.getOffset(0, +1))
        || occupiedPositions.contains(position.getOffset(+1, -1))
        || occupiedPositions.contains(position.getOffset(+1, +1))
        || occupiedPositions.contains(position.getOffset(-1, -1))
        || occupiedPositions.contains(position.getOffset(-1, +1));
  }

  private static String gridToString(Set<Position> occupiedPositions) {
    int minX = occupiedPositions.stream().mapToInt(p -> p.x).min().getAsInt();
    int maxX = occupiedPositions.stream().mapToInt(p -> p.x).max().getAsInt();
    int minY = occupiedPositions.stream().mapToInt(p -> p.y).min().getAsInt();
    int maxY = occupiedPositions.stream().mapToInt(p -> p.y).max().getAsInt();

    StringBuilder builder = new StringBuilder((maxX - minX) * (maxY - minY));
    for (int y = minY; y <= maxY; y++) {
      for (int x = minX; x <= maxX; x++) {
        builder.append(occupiedPositions.contains(new Position(x, y)) ? '#' : ' ');
      }
      if (y != maxY) {
        builder.append('\n');
      }
    }

    return builder.toString();
  }
}
