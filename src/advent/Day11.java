package advent;

import java.io.IOException;

public class Day11 {

  private static class FuelSquare {
    public int totalPower;
    public int xCorner;
    public int yCorner;
    public int size;

    @Override
    public String toString() {
      return xCorner + "," + yCorner;
    }

    public String toStringWithSize() {
      return toString() + "," + size;
    }
  }

  private static final int GRID_SIZE = 300;
  private static final int SQUARE_SIZE = 3;

  public static void main(String[] args) throws IOException {
    final int gridSerialNumber = Integer.parseInt(FileUtility.fileToString("input/11.txt"));

    // Part one
    FileUtility.printAndOutput(
        findMaxFuelSquare(SQUARE_SIZE, Day11::sumSquare, gridSerialNumber), "output/11a.txt");

    // Part two
    FileUtility.printAndOutput(
        findMaxFuelSquareAllSizes(gridSerialNumber).toStringWithSize(), "output/11b.txt");
  }

  private static FuelSquare findMaxFuelSquareAllSizes(int gridSerialNumber) {
    int[][] previousSquareSums = new int[GRID_SIZE][GRID_SIZE];
    FuelSquare maxAllSizes = new FuelSquare();

    for (int squareSize = 1; squareSize <= GRID_SIZE; squareSize++) {
      FuelSquare maxCurrentSize =
          findMaxFuelSquare(
              squareSize,
              (x, y, size, serialNum) ->
                  sumSquareBasedOnPrevious(x, y, size, serialNum, previousSquareSums),
              gridSerialNumber);
      if (maxCurrentSize.totalPower > maxAllSizes.totalPower) {
        maxAllSizes = maxCurrentSize;
      }
    }

    return maxAllSizes;
  }

  private static FuelSquare findMaxFuelSquare(
      int squareSize, SquareSumFunction squareSumFunction, int gridSerialNumber) {
    FuelSquare maxPowerSquare = new FuelSquare();
    maxPowerSquare.size = squareSize;

    for (int xCorner = 1; xCorner <= GRID_SIZE - squareSize + 1; xCorner++) {
      for (int yCorner = 1; yCorner <= GRID_SIZE - squareSize + 1; yCorner++) {
        int totalPower =
            squareSumFunction.sumSquare(xCorner, yCorner, squareSize, gridSerialNumber);
        if (totalPower > maxPowerSquare.totalPower) {
          maxPowerSquare.totalPower = totalPower;
          // Square positions are indexed from 1
          maxPowerSquare.xCorner = xCorner;
          maxPowerSquare.yCorner = yCorner;
        }
      }
    }

    return maxPowerSquare;
  }

  @FunctionalInterface
  private interface SquareSumFunction {
    int sumSquare(int xCorner, int yCorner, int size, int gridSerialNumber);
  }

  private static int sumSquare(int xCorner, int yCorner, int squareSize, int gridSerialNumber) {
    int sum = 0;
    for (int x = 0; x < squareSize; x++) {
      for (int y = 0; y < squareSize; y++) {
        sum += getPower(xCorner + x, yCorner + y, gridSerialNumber);
      }
    }
    return sum;
  }

  private static int sumSquareBasedOnPrevious(
      int xCorner, int yCorner, int squareSize, int gridSerialNumber, int[][] previousSquareSums) {
    int sum =
        previousSquareSums[xCorner - 1][yCorner - 1]
            + sumGnomon(xCorner, yCorner, squareSize, gridSerialNumber);
    previousSquareSums[xCorner - 1][yCorner - 1] = sum;
    return sum;
  }

  private static int sumGnomon(int xCorner, int yCorner, int squareSize, int gridSerialNumber) {
    int sum = 0;

    // Bottom edge of square
    for (int x = 0; x < squareSize; x++) {
      sum += getPower(xCorner + x, yCorner + squareSize - 1, gridSerialNumber);
    }

    // Right-hand edge of square, minus bottom-right corner
    for (int y = 0; y < squareSize - 1; y++) {
      sum += getPower(xCorner + squareSize - 1, yCorner + y, gridSerialNumber);
    }

    return sum;
  }

  private static int getPower(int x, int y, int gridSerialNumber) {
    int rackId = x + 10;
    int powerLevel = rackId * y;
    powerLevel += gridSerialNumber;
    powerLevel *= rackId;
    powerLevel = (powerLevel / 100) % 10;
    return powerLevel - 5;
  }
}
