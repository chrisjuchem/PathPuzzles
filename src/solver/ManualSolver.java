package solver;

import java.awt.Color;

import javalib.impworld.WorldScene;
import maze.Maze;
import maze.MazeNode;
import maze.MazeWorld;

// Represents a MazeSolver for manual solving of the maze
public class ManualSolver extends MazeSolver {

  MazeNode active;

  String input;

  public ManualSolver(Maze maze) {
    super(maze);

    this.active = this.start;
    this.input = null;
  }

  // Draws this ManualSolver accordingly onto the given WorldScene using the given
  // nodeSize
  // EFFECTS: modifies the given WorldScene
  public void draw(WorldScene ws, double nodeSize) {
    for (MazeNode n : this.paths.keySet()) {
      n.drawNode(ws, MazeWorld.COLOR_SEEN, nodeSize);
    }

    for (MazeNode n : this.pathFrom(this.active)) {
      Color c = MazeWorld.COLOR_SEMI_ACTIVE;
      if (!this.canStep()) {
        c = MazeWorld.COLOR_ACTIVE;
      }
      n.drawNode(ws, c, nodeSize);
    }

    this.active.drawNode(ws, MazeWorld.COLOR_ACTIVE, nodeSize);

    if (this.canStep()) {
      this.start.drawNode(ws, MazeWorld.COLOR_START, nodeSize);
      this.end.drawNode(ws, MazeWorld.COLOR_END, nodeSize);
    }
  }

  // Moves this ManualSolver forward one step, if possible, according to the input
  // EFFECTS: modifies active and input
  public void step() {
    if (this.input != null) {
      MazeNode newNode = this.active;

      if (this.input.equals("left")) {
        newNode = this.active.left;
      }
      else if (this.input.equals("right")) {
        newNode = this.active.right;
      }
      else if (this.input.equals("down")) {
        newNode = this.active.below;
      }
      else if (this.input.equals("up")) {
        newNode = this.active.above;
      }

      if (!paths.containsKey(newNode)) {
        paths.put(newNode, active);
      }

      this.active = newNode;
      this.input = null;
    }
  }

  // EFFECTS: changes input to the given String i
  public void updateInput(String i) {
    this.input = i;
  }

}