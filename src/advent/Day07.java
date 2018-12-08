package advent;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Day07 {
  private static class Step implements Comparable<Step> {
    private final char name;
    private final List<Step> nextSteps = new ArrayList<>();
    private final List<Step> priorSteps = new ArrayList<>();

    public Step(char name) {
      this.name = name;
    }

    public char getName() {
      return name;
    }

    public void addNextStep(Step next) {
      nextSteps.add(next);
    }

    public List<Step> getNextSteps() {
      return new ArrayList<>(nextSteps);
    }

    public void addPriorStep(Step prior) {
      priorSteps.add(prior);
    }

    public List<Step> getPriorSteps() {
      return new ArrayList<>(priorSteps);
    }

    public int getDuration() {
      return BASE_STEP_DURATION + (name - 'A' + 1);
    }

    @Override
    public int compareTo(Step other) {
      return Character.compare(name, other.name);
    }
  }

  private static class Task implements Comparable<Task> {
    public final Step step;
    public final int endTime;

    public Task(Step step, int endTime) {
      this.step = step;
      this.endTime = endTime;
    }

    @Override
    public int compareTo(Task other) {
      return Integer.compare(endTime, other.endTime);
    }
  }

  private static final int BASE_STEP_DURATION = 60;
  private static final int NUM_WORKERS = 6;

  private static final Pattern INSTRUCTION_PATTERN =
      Pattern.compile("Step (\\w) must be finished before step (\\w) can begin");

  public static void main(String[] args) throws IOException {
    Set<Step> allSteps = initializeSteps(FileUtility.fileToString("input/07.txt").split("\n"));

    // Part one
    FileUtility.printAndOutput(getStepExecutionOrder(allSteps), "output/07a.txt");

    // Part two
    FileUtility.printAndOutput(getTotalCompletionTime(allSteps), "output/07b.txt");
  }

  private static int getTotalCompletionTime(Set<Step> allSteps) {
    int availableWorkers = NUM_WORKERS;
    int currentTime = 0;
    Set<Step> completedSteps = new HashSet<>(allSteps.size());
    PriorityQueue<Step> currentSteps = getFirstSteps(allSteps);
    PriorityQueue<Task> tasks = new PriorityQueue<>();
    while (completedSteps.size() != allSteps.size()) {
      while (availableWorkers > 0 && !currentSteps.isEmpty()) {
        Step current = currentSteps.remove();
        tasks.add(new Task(current, currentTime + current.getDuration()));
        availableWorkers--;
        System.err.printf(
            "[%s] Started step %c at time %d\n", availableWorkers, current.getName(), currentTime);
      }

      Task finished = tasks.remove();
      currentTime = finished.endTime;
      completedSteps.add(finished.step);
      availableWorkers++;
      for (Step next : finished.step.getNextSteps()) {
        if (completedSteps.containsAll(next.getPriorSteps())) {
          currentSteps.add(next);
        }
      }
      System.err.printf(
          "[%s] Finished step %c at time %d\n",
          availableWorkers, finished.step.getName(), currentTime - 1);
    }
    return currentTime;
  }

  private static String getStepExecutionOrder(Set<Step> allSteps) {
    Set<Step> completedSteps = new HashSet<>(allSteps.size());
    PriorityQueue<Step> currentSteps = getFirstSteps(allSteps);
    StringBuilder executionOrder = new StringBuilder(allSteps.size());
    while (!currentSteps.isEmpty()) {
      Step current = currentSteps.remove();

      completedSteps.add(current);
      executionOrder.append(current.getName());

      for (Step next : current.getNextSteps()) {
        if (completedSteps.containsAll(next.getPriorSteps())) {
          currentSteps.add(next);
        }
      }
    }

    return executionOrder.toString();
  }

  private static Step getStep(Map<Character, Step> steps, char name) {
    if (steps.containsKey(name)) {
      return steps.get(name);
    }

    Step step = new Step(name);
    steps.put(name, step);
    return step;
  }

  private static Set<Step> initializeSteps(String[] instructions) {
    Map<Character, Step> steps = new HashMap<>();
    for (String instruction : instructions) {
      List<String> stepNames = ParseUtility.getMatchedGroups(instruction, INSTRUCTION_PATTERN);
      Step prior = getStep(steps, stepNames.get(0).charAt(0));
      Step next = getStep(steps, stepNames.get(1).charAt(0));

      prior.addNextStep(next);
      next.addPriorStep(prior);
    }
    return new HashSet<>(steps.values());
  }

  private static PriorityQueue<Step> getFirstSteps(Set<Step> allSteps) {
    PriorityQueue<Step> firstSteps = new PriorityQueue<>(allSteps.size());

    // Remove all steps that follow other steps
    for (Step step : allSteps) {
      if (step.getPriorSteps().isEmpty()) {
        firstSteps.add(step);
      }
    }

    return firstSteps;
  }
}
