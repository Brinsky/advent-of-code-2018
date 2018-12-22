package advent;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day19 {

  private static Day16.Op getOp(String instruction) {
    for (Day16.Op op : Day16.Op.values()) {
      if (instruction.startsWith(op.name().toLowerCase())) {
        return op;
      }
    }

    return null;
  }

  private static class Instruction {
    public final Day16.Op op;
    public final int a, b, c;

    public Instruction(String instruction) {
      int[] args = ParseUtility.extractInts(instruction);

      op = getOp(instruction);
      a = args[0];
      b = args[1];
      c = args[2];
    }

    void perform(int[] registers) {
      int valueA = (op.typeA == Day16.InputType.REGISTER) ? registers[a] : a;
      int valueB = (op.typeB == Day16.InputType.REGISTER) ? registers[b] : b;

      registers[c] = op.function.perform(valueA, valueB);
    }

    @Override
    public String toString() {
      switch (op) {
        case ADDR:
          if (a == c) {
            return String.format("R%d += R%d", c, b);
          } else if (b == c) {
            return String.format("R%d += R%d", c, a);
          } else {
            return String.format("R%d = R%d + R%d", c, a, b);
          }
        case ADDI:
          if (a == c) {
            return String.format("R%d += %d", c, b);
          } else {
            return String.format("R%d = R%d + %d", c, a, b);
          }
        case MULR:
          if (a == c) {
            return String.format("R%d *= R%d", c, b);
          } else if (b == c) {
            return String.format("R%d *= R%d", c, a);
          } else {
            return String.format("R%d = R%d * R%d", c, a, b);
          }
        case MULI:
          if (a == c) {
            return String.format("R%d *= %d", c, b);
          } else {
            return String.format("R%d = R%d * %d", c, a, b);
          }
        case SETR:
          return String.format("R%d = R%d", c, a);
        case SETI:
          return String.format("R%d = %d", c, a);
        case GTRR:
          return String.format("R%d = (R%d > R%d)", c, a, b);
        case EQRR:
          return String.format("R%d = (R%d == R%d)", c, a, b);
      }

      return String.format(
          "%s %s%d %s%d R%d",
          op.name().toLowerCase(),
          (op.typeA == Day16.InputType.REGISTER ? "R" : ""),
          a,
          (op.typeB == Day16.InputType.REGISTER ? "R" : ""),
          b,
          c);
    }
  }

  private static int executeProgram(
      List<Instruction> instructions, int boundToRegister, int register0) {
    int[] registers = new int[NUM_REGISTERS];
    registers[0] = register0;
    for (int i = 0; i >= 0 && i < instructions.size(); i++) {
      registers[boundToRegister] = i;
      instructions.get(i).perform(registers);
      i = registers[boundToRegister];
    }
    return registers[0];
  }

  private static long sumFactors(int number) {
    int sum = 0;
    for (int i = 1; i <= number; i++) {
      if (number % i == 0) {
        sum += i;
      }
    }
    return sum;
  }

  private static final int NUM_REGISTERS = 6;
  private static final int PART_2_REGISTER_1 = 10551300; // Determined from analysis

  public static void main(String[] args) throws IOException {
    String[] lines = FileUtility.fileToString("input/19.txt").split("\n");
    final int boundToRegister = ParseUtility.extractInts(lines[0])[0];
    List<Instruction> instructions =
        Arrays.stream(lines).skip(1).map(Instruction::new).collect(Collectors.toList());

    // Part one
    FileUtility.printAndOutput(executeProgram(instructions, boundToRegister, 0), "output/19a.txt");

    // Part two
    // After analysis, it appears the program is summing all of the factors of N, where N is a value
    // determined at the start of the program and placed in register 1.
    // See input/19-analysis.txt for analysis
    FileUtility.printAndOutput(sumFactors(PART_2_REGISTER_1), "output/19b.txt");
  }
}
