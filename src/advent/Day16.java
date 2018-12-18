package advent;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day16 {

  @FunctionalInterface
  private interface OpFunction {
    int perform(int a, int b);
  }

  private enum BaseOp {
    ADD((a, b) -> a + b),
    MUL((a, b) -> a * b),
    BAN((a, b) -> a & b),
    BOR((a, b) -> a | b),
    SET((a, b) -> a),
    GT((a, b) -> (a > b) ? 1 : 0),
    EQ((a, b) -> (a == b) ? 1 : 0);

    public final OpFunction function;

    BaseOp(OpFunction function) {
      this.function = function;
    }
  }

  private enum InputType {
    IMMEDIATE,
    REGISTER
  }

  private enum Op {
    ADDR(BaseOp.ADD, InputType.REGISTER, InputType.REGISTER),
    ADDI(BaseOp.ADD, InputType.REGISTER, InputType.IMMEDIATE),
    MULR(BaseOp.MUL, InputType.REGISTER, InputType.REGISTER),
    MULI(BaseOp.MUL, InputType.REGISTER, InputType.IMMEDIATE),
    BANR(BaseOp.BAN, InputType.REGISTER, InputType.REGISTER),
    BANI(BaseOp.BAN, InputType.REGISTER, InputType.IMMEDIATE),
    BORR(BaseOp.BOR, InputType.REGISTER, InputType.REGISTER),
    BORI(BaseOp.BOR, InputType.REGISTER, InputType.IMMEDIATE),
    SETR(BaseOp.SET, InputType.REGISTER, InputType.IMMEDIATE /* ignored */),
    SETI(BaseOp.SET, InputType.IMMEDIATE, InputType.IMMEDIATE /* ignored */),
    GTIR(BaseOp.GT, InputType.IMMEDIATE, InputType.REGISTER),
    GTRI(BaseOp.GT, InputType.REGISTER, InputType.IMMEDIATE),
    GTRR(BaseOp.GT, InputType.REGISTER, InputType.REGISTER),
    EQIR(BaseOp.EQ, InputType.IMMEDIATE, InputType.REGISTER),
    EQRI(BaseOp.EQ, InputType.REGISTER, InputType.IMMEDIATE),
    EQRR(BaseOp.EQ, InputType.REGISTER, InputType.REGISTER);

    public final OpFunction function;
    public final InputType typeA;
    public final InputType typeB;

    Op(BaseOp baseOp, InputType typeA, InputType typeB) {
      this.function = baseOp.function;
      this.typeA = typeA;
      this.typeB = typeB;
    }
  }

  private static class Sample {
    public final int[] before;
    public final int[] after;
    public final int[] instruction;
    public final Set<Op> compatibleOps = new HashSet<>();

    public Sample(String specification) {
      String[] lines = specification.split("\n");

      before = ParseUtility.extractInts(lines[0]);
      instruction = ParseUtility.extractInts(lines[1]);
      after = ParseUtility.extractInts(lines[2]);
    }
  }

  private static boolean opMatches(Op op, Sample sample) {
    int[] registers = Arrays.copyOf(sample.before, 4);
    perform(op, sample.instruction, registers);
    return Arrays.equals(sample.after, registers);
  }

  private static void perform(Op op, int[] instruction, int[] registers) {
    int a = instruction[1];
    int b = instruction[2];
    int registerC = instruction[3];

    int valueA = (op.typeA == InputType.REGISTER) ? registers[a] : a;
    int valueB = (op.typeB == InputType.REGISTER) ? registers[b] : b;

    registers[registerC] = op.function.perform(valueA, valueB);
  }

  public static void main(String[] args) throws IOException {
    String[] parts = FileUtility.fileToString("input/16.txt").split("\n\n\n\n");
    List<Sample> samples = parseSamples(parts[0]);

    // Part one
    FileUtility.printAndOutput(
        samples.stream().filter(s -> s.compatibleOps.size() >= 3).count(), "output/16a.txt");

    // Part two
    FileUtility.printAndOutput(
        runProgramAndGetRegisters(parts[1], deriveOpcodes(samples))[0], "output/16b.txt");
  }

  private static int[] runProgramAndGetRegisters(String program, Map<Integer, Op> opcodes) {
    String[] instructionsText = program.split("\n");
    int[][] instructions = new int[instructionsText.length][];
    for (int i = 0; i < instructionsText.length; i++) {
      instructions[i] = ParseUtility.extractInts(instructionsText[i]);
    }

    int[] registers = new int[4];

    for (int[] instruction : instructions) {
      perform(opcodes.get(instruction[0]), instruction, registers);
    }
    return registers;
  }

  private static List<Sample> parseSamples(String part) {
    String[] sampleSpecs = part.split("\n\n");
    List<Sample> samples = Arrays.stream(sampleSpecs).map(Sample::new).collect(Collectors.toList());

    for (Sample sample : samples) {
      for (Op op : Op.values()) {
        if (opMatches(op, sample)) {
          sample.compatibleOps.add(op);
        }
      }
    }
    return samples;
  }

  private static Map<Integer, Op> deriveOpcodes(List<Sample> samples) {
    Map<Integer, Op> opcodes = new HashMap<>(16);
    while (opcodes.size() < 16) {
      for (Sample s : samples) {
        s.compatibleOps.removeAll(opcodes.values());
        if (s.compatibleOps.size() == 1) {
          Op op = s.compatibleOps.iterator().next();
          s.compatibleOps.remove(op);
          opcodes.put(s.instruction[0], op);
        }
      }
    }
    return opcodes;
  }
}
