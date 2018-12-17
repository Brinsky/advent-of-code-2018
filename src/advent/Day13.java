package advent;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day13 {

  private enum Direction {
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0);

    public final Day10.Position delta;

    Direction(int deltaX, int deltaY) {
      delta = new Day10.Position(deltaX, deltaY);
    }

    public Direction rotateLeft() {
      switch (this) {
        case NORTH:
          return WEST;
        case EAST:
          return NORTH;
        case SOUTH:
          return EAST;
        case WEST:
          return SOUTH;
      }
      throw new IllegalArgumentException();
    }

    public Direction rotateRight() {
      switch (this) {
        case NORTH:
          return EAST;
        case EAST:
          return SOUTH;
        case SOUTH:
          return WEST;
        case WEST:
          return NORTH;
      }
      throw new IllegalArgumentException();
    }
  }

  private static class Cart implements Comparable<Cart> {
    public Direction dir;
    public Day10.Position pos;
    public boolean hasCollided = false;

    private int turnMode = 0;

    public Cart(char cart, Day10.Position pos) {
      dir = getCartDirection(cart);
      this.pos = pos;
    }

    @Override
    public int compareTo(Cart other) {
      return pos.compareTo(other.pos);
    }

    public boolean tick(char[][] map, Map<Day10.Position, Cart> carts) {
      carts.remove(pos);
      char track = map[pos.x][pos.y];

      if (track == '/') {
        switch (dir) {
          case NORTH:
            dir = Direction.EAST;
            break;
          case EAST:
            dir = Direction.NORTH;
            break;
          case SOUTH:
            dir = Direction.WEST;
            break;
          case WEST:
            dir = Direction.SOUTH;
            break;
        }
      } else if (track == '\\') {
        switch (dir) {
          case NORTH:
            dir = Direction.WEST;
            break;
          case EAST:
            dir = Direction.SOUTH;
            break;
          case SOUTH:
            dir = Direction.EAST;
            break;
          case WEST:
            dir = Direction.NORTH;
            break;
        }
      } else if (track == '+') {
        switch (turnMode) {
          case 0:
            dir = dir.rotateLeft();
            break;
          case 2:
            dir = dir.rotateRight();
            break;
        }
        turnMode = (turnMode + 1) % 3;
      }

      pos = pos.add(dir.delta);

      if (carts.containsKey(pos)) { // If collision
        Cart other = carts.remove(pos);
        other.hasCollided = true;
        hasCollided = true;
        return true;
      } else {
        carts.put(pos, this);
        return false;
      }
    }

    @Override
    public String toString() {
      return pos + " " + dir.name() + " " + (hasCollided ? "X" : "");
    }
  }

  private static boolean isCart(char c) {
    return c == '^' || c == '>' || c == 'v' || c == '<';
  }

  private static char getTrack(char cart) {
    switch (cart) {
      case '^':
        return '|';
      case '>':
        return '-';
      case 'v':
        return '|';
      case '<':
        return '-';
    }
    throw new IllegalArgumentException();
  }

  public static Direction getCartDirection(char cart) {
    switch (cart) {
      case '^':
        return Direction.NORTH;
      case '>':
        return Direction.EAST;
      case 'v':
        return Direction.SOUTH;
      case '<':
        return Direction.WEST;
      default:
        throw new IllegalArgumentException();
    }
  }

  public static void main(String[] args) throws IOException {
    String[] lines = FileUtility.fileToString("input/13.txt").split("\n");

    char[][] map = parseMap(lines);
    List<Cart> carts = parseCarts(map);
    Day10.Position[] cartOutcomes = getCartOutcomes(map, carts);

    // Part one
    FileUtility.printAndOutput(cartOutcomes[0].x + "," + cartOutcomes[0].y, "output/13a.txt");

    // Part one
    FileUtility.printAndOutput(cartOutcomes[1].x + "," + cartOutcomes[1].y, "output/13a.txt");
  }

  private static Day10.Position[] getCartOutcomes(char[][] map, List<Cart> carts) {
    Map<Day10.Position, Cart> cartPositions =
        carts.stream().collect(Collectors.toMap(c -> c.pos, Function.identity()));

    Day10.Position firstCollision = null;
    Day10.Position lastCart = null;
    while (lastCart == null) {
      Collections.sort(carts);
      for (Cart cart : carts) {
        if (cart.hasCollided) {
          continue;
        }

        if (cart.tick(map, cartPositions)) { // If collision
          if (firstCollision == null) {
            firstCollision = cart.pos;
          }
        } else if (cartPositions.size() == 1) {
          lastCart = cart.pos;
        }
      }
    }

    return new Day10.Position[] {firstCollision, lastCart};
  }

  private static char[][] parseMap(String[] lines) {
    char[][] map = new char[lines[0].length()][lines.length];

    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[0].length; y++) {
        map[x][y] = lines[y].charAt(x);
      }
    }

    return map;
  }

  private static List<Cart> parseCarts(char[][] map) {
    List<Cart> carts = new ArrayList<>();

    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[0].length; y++) {
        char current = map[x][y];

        if (isCart(current)) {
          carts.add(new Cart(current, new Day10.Position(x, y)));
          map[x][y] = getTrack(current); // Replace cart icons with track
        }
      }
    }

    return carts;
  }
}
