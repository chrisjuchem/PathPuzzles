package solver;


import java.util.ArrayList;

import javalib.impworld.WorldScene;
import maze.Maze;
import maze.MazeNode;
import maze.MazeWorld;
import myUtil.ColorMixer;

// Traverses the maze and creates a heat map of the maze
public class HeatMap extends MazeFunction {
  ArrayList<ArrayList<MazeNode>> tiers;

  ArrayList<MazeNode> worklist;

  public HeatMap(Maze maze) {
    super(maze);
    this.tiers = new ArrayList<ArrayList<MazeNode>>();

    this.tiers.add(new ArrayList<MazeNode>());
    this.tiers.get(0).add(this.start);

    this.worklist = new ArrayList<MazeNode>();
    for (MazeNode connection : this.start) {
      if (this.start != connection) {
        this.worklist.add(connection);
      }
    }
  }

  // Checks if this HeatMap can perform another step in the current maze
  public boolean canStep() {
    return this.worklist.size() > 0;
  }

  // Moves this HeatMap forward one step
  // EFFECTS: modifies tiers and worklist
  public void step() {
    ArrayList<MazeNode> previous = this.tiers.get(this.tiers.size() - 1);
    ArrayList<MazeNode> newWorklist = new ArrayList<MazeNode>();

    for (MazeNode n : this.worklist) {
      for (MazeNode connection : n) {
        if (!previous.contains(connection) && connection != n) {
          newWorklist.add(connection);
        }
      }
    }

    this.tiers.add(this.worklist);
    this.worklist = newWorklist;

    if (this.canStep()) {
      this.step();
    }
  }

  // Draws this HeatMap accordingly onto the given WorldScene using the given
  // nodeSize
  // EFFECTS: modifies the given WorldScene
  public void draw(WorldScene ws, double nodeSize) {

    for (int i = 0; i < tiers.size(); i += 1) {
      for (MazeNode n : tiers.get(i)) {
        n.drawNode(ws, ColorMixer.mix(MazeWorld.COLOR_END, MazeWorld.COLOR_START,
                ((double) i / tiers.size())), nodeSize);
      }
    }

  }

}