package advent;

import java.util.Arrays;

public class ParseUtility {

    public static int[] extractInts(String s) {
        String[] valuesText = s.split("\\D+");
        int[] values = new int[valuesText.length - 1];

        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.parseInt(valuesText[i + 1]);
        }

        return values;
    }
}
