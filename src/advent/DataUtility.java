package advent;

import java.util.Arrays;

public class DataUtility {

  public static String matrixToString(Object[][] matrix) {
    StringBuilder builder = new StringBuilder(matrix[0].length * matrix.length);
    for (int y = 0; y < matrix[0].length; y++) {
      for (int x = 0; x < matrix.length; x++) {
        builder.append(matrix[x][y]);
      }
      if (y < matrix[0].length - 1) {
        builder.append("\n");
      }
    }
    return builder.toString();
  }

  public static void fillMatrix(Object[][] matrix, Object value) {
    for (Object[] column : matrix) {
      Arrays.fill(column, value);
    }
  }
  
  public static boolean isInBounds(Object[][] matrix, int x, int y) {
    return x >= 0 && x < matrix.length && y >= 0 && y < matrix[0].length;
  }
}
