package solver;

import java.util.ArrayList;

import maze.Maze;
import maze.MazeNode;
import maze.SimpleMaze;
import tester.Tester;

public class SolverTest {



  void testHeatMap(Tester t) {
    HeatMap h = new HeatMap(new Maze(5, 5));
    t.checkExpect(h.canStep(), true);
    h.step();
    t.checkExpect(h.worklist.size(), 0);
    t.checkExpect(h.canStep(), false);
  }

  void testManualSolver(Tester t) {
    ManualSolver ms = new ManualSolver(new Maze(5, 5, 1, 0));
    MazeNode active1 = ms.active;
    t.checkExpect(ms.canStep(), true);
    ms.updateInput("left");
    t.checkExpect(ms.input, "left");
    ms.step();
    t.checkExpect(ms.canStep(), true);
    t.checkExpect(ms.active, active1.left);
  }
  void testDepthSolver(Tester t) {
    ArrayList<MazeNode> nodes = SimpleMaze.maze();
    DepthSolver solver = new DepthSolver(new Maze(nodes));

    t.checkExpect(solver.worklist.peek(), nodes.get(0));
    solver.step();
    t.checkExpect(solver.worklist.peek(), nodes.get(1));
    solver.step();
    t.checkExpect(solver.worklist.peek(), nodes.get(2));
    solver.step();
    t.checkExpect(solver.worklist.peek(), nodes.get(3));
    solver.step();
    t.checkExpect(solver.worklist.peek(), nodes.get(4));
    t.checkExpect(solver.canStep(), false);
  }
  void testBreadthSolver(Tester t) {
    ArrayList<MazeNode> nodes = SimpleMaze.maze();
    BreadthSolver solver = new BreadthSolver(new Maze(nodes));

    t.checkExpect(solver.worklist.peek(), nodes.get(0));
    solver.step();
    t.checkExpect(solver.worklist.peek(), nodes.get(3));
    solver.step();
    t.checkExpect(solver.worklist.peek(), nodes.get(1));
    solver.step();
    t.checkExpect(solver.worklist.peek(), nodes.get(4));
    t.checkExpect(solver.canStep(), false);

  }
}
