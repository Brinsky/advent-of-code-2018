package advent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day03 {

  private static class Claim {
    public final int id;

    // Inclusive
    public final int xStart;
    public final int yStart;

    // Exclusive
    public final int xEnd;
    public final int yEnd;

    public Claim(String claim) {
      List<Integer> args = ParseUtility.extractIntegers(claim);
      id = args.get(0);
      xStart = args.get(1);
      yStart = args.get(2);
      xEnd = xStart + args.get(3);
      yEnd = yStart + args.get(4);
    }
  }

  public static void main(String[] args) throws IOException {
    String[] claimsSpecs = FileUtility.fileToString("input/03.txt").split("\n");

    int[] fabricDimensions = new int[2];
    List<Claim> claims = getClaims(claimsSpecs, fabricDimensions);

    int fabricWidth = fabricDimensions[0];
    int fabricHeight = fabricDimensions[1];

    // Part one
    FileUtility.printAndOutput(countOverlaps(claims, fabricWidth, fabricHeight), "output/03a.txt");

    // Part two
    FileUtility.printAndOutput(
        findNonOverlappingIds(claims, fabricWidth, fabricHeight).iterator().next(),
        "output/03b.txt");
  }

  private static List<Claim> getClaims(String[] claimsSpecs, int[] fabricDimensions) {
    List<Claim> claims = new ArrayList<>(claimsSpecs.length);
    for (String claimSpec : claimsSpecs) {
      Claim claim = new Claim(claimSpec);
      claims.add(claim);

      if (claim.xEnd > fabricDimensions[0]) {
        fabricDimensions[0] = claim.xEnd;
      }

      if (claim.yEnd > fabricDimensions[1]) {
        fabricDimensions[1] = claim.yEnd;
      }
    }
    return claims;
  }

  private static int countOverlaps(List<Claim> claims, int fabricWidth, int fabricHeight) {
    // Grid that counts # of overlaps at a give position
    int[][] overlaps = new int[fabricWidth][fabricHeight];
    int overlapCount = 0;

    for (Claim claim : claims) {
      for (int x = claim.xStart; x < claim.xEnd; x++) {
        for (int y = claim.yStart; y < claim.yEnd; y++) {
          overlaps[x][y]++;
          if (overlaps[x][y] == 2) {
            overlapCount++;
          }
        }
      }
    }

    return overlapCount;
  }

  private static Set<Integer> findNonOverlappingIds(
      List<Claim> claims, int fabricWidth, int fabricHeight) {
    Set<Integer> nonOverlappingIds =
        IntStream.rangeClosed(1, claims.size()).boxed().collect(Collectors.toSet());

    // Grid that tracks IDs of most recent claims
    int[][] ids = new int[fabricWidth][fabricHeight];

    for (Claim claim : claims) {
      for (int x = claim.xStart; x < claim.xEnd; x++) {
        for (int y = claim.yStart; y < claim.yEnd; y++) {
          // Disqualify claims that overlap
          if (ids[x][y] != 0) {
            nonOverlappingIds.remove(ids[x][y]);
            nonOverlappingIds.remove(claim.id);
          }
          ids[x][y] = claim.id;
        }
      }
    }

    return nonOverlappingIds;
  }
}
