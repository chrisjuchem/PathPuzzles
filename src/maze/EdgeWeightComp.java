package maze;

import myUtil.BoolComparator;

//compares edges based on their weights
public class EdgeWeightComp implements BoolComparator<MazeEdge> {
  public boolean compare(MazeEdge one, MazeEdge two) {
    return one.hasGreaterWeight(two);
  }
}