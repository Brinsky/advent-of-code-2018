package advent;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day06 {

  private static final int TOTAL_DISTANCE_CUTOFF = 10000;

  private static class Point {
    public final int x;
    public final int y;

    public Point(String s) {
      List<Integer> args = ParseUtility.extractIntegers(s);
      x = args.get(0);
      y = args.get(1);
    }

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Point)) {
        return false;
      }

      Point p = (Point) obj;
      return p.x == x && p.y == y;
    }

    @Override
    public int hashCode() {
      return x * 3 + y * 7;
    }

    @Override
    public String toString() {
      return "(" + x + ", " + y + ")";
    }
  }

  public static void main(String[] args) throws IOException {
    List<Point> allPoints =
        Arrays.stream(FileUtility.fileToString("input/06.txt").split("\n"))
            .map(Point::new)
            .collect(Collectors.toList());

    // Part one
    FileUtility.printAndOutput(getLargestFiniteArea(allPoints), "output/06a.txt");

    // Part two
    Point average =
        new Point(
            allPoints.stream().mapToInt(p -> p.x).sum() / allPoints.size(),
            allPoints.stream().mapToInt(p -> p.x).sum() / allPoints.size());
    FileUtility.printAndOutput(
        getAreaUnderTotalDistanceCutoff(allPoints, average, TOTAL_DISTANCE_CUTOFF),
        "output/06b.txt");
  }

  private static int getLargestFiniteArea(List<Point> allPoints) {
    Set<Point> finiteAreaPoints = new HashSet<>(allPoints.size());
    for (Point point : allPoints) {
      if (!isCloseToInfinitelyManySpaces(allPoints, point)) {
        finiteAreaPoints.add(point);
      }
    }

    int largestFiniteArea = 0;
    for (Point point : finiteAreaPoints) {
      int area = getAreaClosestToPoint(allPoints, point);
      if (area > largestFiniteArea) {
        largestFiniteArea = area;
      }
    }
    return largestFiniteArea;
  }

  private static int getAreaClosestToPoint(List<Point> allPoints, Point anchor) {
    return getAreaSatisfyingCondition(
        allPoints, anchor, (aP, current) -> isClosestToAnchor(current, anchor, aP));
  }

  private static int getAreaUnderTotalDistanceCutoff(
      List<Point> allPoints, Point start, int cutoff) {
    return getAreaSatisfyingCondition(
        allPoints, start, (aP, current) -> getTotalDistance(aP, current) < cutoff);
  }

  @FunctionalInterface
  private interface PointComparisonCondition {
    boolean isSatisfied(List<Point> allPoints, Point current);
  }

  private static int getAreaSatisfyingCondition(
      List<Point> allPoints, Point start, PointComparisonCondition condition) {
    Set<Point> visited = new HashSet<>();
    Deque<Point> toVisit = new ArrayDeque<>();

    // Iterative implementation of DFS to prevent stack overflows
    toVisit.push(start);
    int area = 0;
    while (!toVisit.isEmpty()) {
      Point current = toVisit.pop();

      if (visited.contains(current) || !condition.isSatisfied(allPoints, current)) {
        continue;
      }

      visited.add(current);
      area++;

      toVisit.push(new Point(current.x - 1, current.y));
      toVisit.push(new Point(current.x + 1, current.y));
      toVisit.push(new Point(current.x, current.y - 1));
      toVisit.push(new Point(current.x, current.y + 1));
    }

    return area;
  }

  /** Returns the sum of the Manhattan distances from each point to the specified point. */
  private static int getTotalDistance(List<Point> allPoints, Point current) {
    int totalDistance = 0;
    for (Point point : allPoints) {
      totalDistance += manhattanDistance(current, point);
    }
    return totalDistance;
  }

  /**
   * Returns true iff {@code current} is closer to {@code anchor} than to any other point in {@code
   * allPoints}.
   */
  private static boolean isClosestToAnchor(Point current, Point anchor, List<Point> allPoints) {
    int distance = manhattanDistance(current, anchor);

    for (Point point : allPoints) {
      if (!point.equals(anchor) && manhattanDistance(current, point) <= distance) {
        return false;
      }
    }

    return true;
  }

  private static int manhattanDistance(Point a, Point b) {
    return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
  }

  /**
   * Determines if there are infinitely many spaces closer to the {@code toTest} than to the other points.
   */
  // Consider the region around a point A divided into four infinite cones (to the North, East,
  // West, and South):
  //
  //  \  n  /
  //   \   /
  //    \ /
  //  w  A  e
  //    / \
  //   /   \
  //  /  s  \
  //
  // Our conjecture is that if there any of these cones contains no named points other than A (where
  // each cone includes the separating lines on either side), then there will be infinitely many
  // spaces closer to A than to any other named point.
  private static boolean isCloseToInfinitelyManySpaces(List<Point> allPoints, Point toTest) {
    boolean northCone = false;
    boolean eastCone = false;
    boolean westCone = false;
    boolean southCone = false;

    for (Point point : allPoints) {
      if (point == toTest) {
        continue;
      }

      int deltaX = point.x - toTest.x;
      int deltaY = point.y - toTest.y;

      if (deltaY < 0 && Math.abs(deltaX) <= Math.abs(deltaY)) {
        northCone = true;
      }
      if (deltaX > 0 && Math.abs(deltaY) <= Math.abs(deltaX)) {
        eastCone = true;
      }
      if (deltaY > 0 && Math.abs(deltaX) <= Math.abs(deltaY)) {
        southCone = true;
      }
      if (deltaX < 0 && Math.abs(deltaY) <= Math.abs(deltaX)) {
        westCone = true;
      }
    }

    // A point is close to infinity if any of its cones are unobstructed
    return !(northCone && eastCone && southCone && westCone);
  }
}
