package advent;

import java.io.IOException;
import java.util.Arrays;

public class Day08 {
  private static class Node {
    public final Node[] children;
    public final int[] metadata;

    public Node(int childCount, int metadataCount) {
      children = new Node[childCount];
      metadata = new int[metadataCount];
    }
  }

  private static class NodeData {
    public final Node node;
    public final int nodeEnd;
    public final int metadataSum;

    public NodeData(Node node, int nodeEnd, int metadataSum) {
      this.node = node;
      this.nodeEnd = nodeEnd;
      this.metadataSum = metadataSum;
    }
  }

  private static NodeData createNode(int[] specification, int nodeStart) {
    Node current = new Node(specification[nodeStart], specification[nodeStart + 1]);
    int metadataSum = 0;

    nodeStart += 2;
    for (int i = 0; i < current.children.length; i++) {
      NodeData childData = createNode(specification, nodeStart);
      current.children[i] = childData.node;
      metadataSum += childData.metadataSum;
      nodeStart = childData.nodeEnd;
    }

    for (int i = 0; i < current.metadata.length; i++, nodeStart++) {
      current.metadata[i] = specification[nodeStart];
      metadataSum += current.metadata[i];
    }

    return new NodeData(current, nodeStart, metadataSum);
  }

  public static void main(String[] args) throws IOException {
    int[] specification =
        ParseUtility.extractIntegers(FileUtility.fileToString("input/08.txt"))
            .stream()
            .mapToInt(Integer::intValue)
            .toArray();

    NodeData nodeData = createNode(specification, 0);

    // Part one
    FileUtility.printAndOutput(nodeData.metadataSum, "output/08a.txt");

    // Part two
    FileUtility.printAndOutput(getModifiedMetadataSum(nodeData.node), "output/08b.txt");
  }

  public static int getModifiedMetadataSum(Node root) {
    if (root.children.length == 0) {
      return Arrays.stream(root.metadata).sum();
    }

    int sum = 0;
    for (int index : root.metadata) {
      if (index > 0 && index <= root.children.length) {
        sum += getModifiedMetadataSum(root.children[index - 1]);
      }
    }

    return sum;
  }
}
