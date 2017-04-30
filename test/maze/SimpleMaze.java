package maze;

import java.util.ArrayList;

public class SimpleMaze {
  public static ArrayList<MazeNode> maze() {
    ArrayList<MazeNode> nodes = new ArrayList<MazeNode>();
    // 0 1 2
    // 3
    // 4
    nodes.add(new MazeNode(0, 0));
    nodes.add(new MazeNode(1, 0));
    nodes.add(new MazeNode(2, 0));
    nodes.add(new MazeNode(0, 1));
    nodes.add(new MazeNode(0, 2));

    nodes.get(0).connect(nodes.get(1));
    nodes.get(1).connect(nodes.get(0));
    nodes.get(2).connect(nodes.get(1));
    nodes.get(1).connect(nodes.get(2));
    nodes.get(0).connect(nodes.get(3));
    nodes.get(3).connect(nodes.get(0));
    nodes.get(4).connect(nodes.get(3));
    nodes.get(3).connect(nodes.get(4));

    return nodes;
  }
}
