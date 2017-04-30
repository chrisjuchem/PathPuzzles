package solver;


import deque.Deque;
import javalib.impworld.WorldScene;
import maze.Maze;
import maze.MazeNode;
import maze.MazeWorld;

// Represents a MazeSolver that is automatic
abstract class AutoSolver extends MazeSolver {

  Deque<MazeNode> worklist;

  AutoSolver(Maze maze) {
    super(maze);

    this.worklist = new Deque<MazeNode>();
    this.worklist.addToHead(this.start);
  }

  // Moves this AutoSolver forward a step using worklist as a guide
  // EFFECTS: modifies worklist
  public void step() {
    MazeNode next = this.worklist.removeFromHead();
    for (MazeNode connection : next) {
      if (connection != next && !this.paths.containsKey(connection)) {
        this.addToWorklist(connection);
        paths.put(connection, next);
      }
    }
  }

  //adds the node to the correct end of the worklist for this type of search
  //EFFECT: modifies this.worklist
  abstract void addToWorklist(MazeNode n);

  //Draws this information stored in this iterator onto the given
  //drawing of the complete maze
  //EFFECT: modifies the given WorldScene
  public void draw(WorldScene ws, double nodeSize) {
    for (MazeNode n : this.paths.keySet()) {
      n.drawNode(ws, MazeWorld.COLOR_SEEN, nodeSize);
    }

    for (MazeNode n : this.worklist) {
      n.drawNode(ws, MazeWorld.COLOR_SEMI_ACTIVE, nodeSize);
    }

    this.start.drawNode(ws, MazeWorld.COLOR_START, nodeSize);
    this.end.drawNode(ws, MazeWorld.COLOR_END, nodeSize);

    if (!this.canStep()) {
      for (MazeNode n : this.pathFrom(this.end)) {
        n.drawNode(ws, MazeWorld.COLOR_ACTIVE, nodeSize);
      }
    }
    else {
      this.worklist.peek().drawNode(ws, MazeWorld.COLOR_ACTIVE, nodeSize);
    }
  }
}