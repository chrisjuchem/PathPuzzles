package maze;

import java.util.ArrayList;

import solver.BreadthSolver;
import solver.DepthSolver;
import solver.HeatMap;
import solver.ManualSolver;
import tester.Tester;

public class MazeTest {

  Maze m = new Maze(5, 5);
  MazeNode mn;
  MazeNode mn1;
  MazeNode mn2;
  MazeNode mn3;
  MazeNode mn4;

  void initMazeNodes() {
    mn = new MazeNode(0, 0);
    mn1 = new MazeNode(1, 0);
    mn2 = new MazeNode(0, 1);
    mn3 = new MazeNode(1, 1);
    mn4 = new MazeNode(2, 0);
  }

  ArrayList<MazeEdge> mz1 = new ArrayList<MazeEdge>();
  ArrayList<MazeEdge> mz2 = new ArrayList<MazeEdge>();
  ArrayList<MazeEdge> mz3 = new ArrayList<MazeEdge>();
  MazeEdge e1 = new MazeEdge(mn, mn, 0);
  MazeEdge e2 = new MazeEdge(mn, mn, 1);
  MazeEdge e3 = new MazeEdge(mn, mn, 1);
  MazeEdge e4 = new MazeEdge(mn, mn, 2);
  MazeEdge e5 = new MazeEdge(mn, mn, 0);
  MazeEdge e6 = new MazeEdge(mn, mn, 0);

  void initMazeEdges() {
    this.initMazeNodes();
    mz1 = new ArrayList<MazeEdge>();
    mz1.add(e1);
    mz1.add(e2);
    mz1.add(e3);
    mz1.add(e4);
    mz1.add(e5);
    mz1.add(e6);
    mz2 = new ArrayList<MazeEdge>();
    mz2.add(e1);
    mz2.add(e1);
    mz2.add(e1);
    mz2.add(e1);
    mz3 = new ArrayList<MazeEdge>();
  }


  void testConnect(Tester t) {
    this.initMazeNodes();
    this.mn.connect(this.mn1);
    t.checkExpect(this.mn.right, this.mn1);
    this.mn1.connect(this.mn);
    t.checkExpect(this.mn1.left, this.mn);
    t.checkException(new RuntimeException("Cannot connect a non-adjacent node."),
            this.mn, "connect", this.mn4);
  }

  void testNodeIterator(Tester t) {
    this.initMazeNodes();
    this.mn.connect(this.mn1);
    this.mn.connect(this.mn2);
    NodeIterator n = new NodeIterator(this.mn);
    t.checkExpect(n.hasNext(), true);
    t.checkExpect(n.next(), this.mn);
    t.checkExpect(n.next(), this.mn);
    t.checkExpect(n.next(), this.mn2);
    t.checkExpect(n.next(), this.mn1);
    t.checkExpect(n.hasNext(), false);
  }

  void testHasGreaterWeight(Tester t) {
    t.checkExpect(e1.hasGreaterWeight(e2), false);
    t.checkExpect(e1.hasGreaterWeight(e6), false);
    t.checkExpect(e4.hasGreaterWeight(e3), true);
    t.checkExpect(e4.hasGreaterWeight(e4), false);
    t.checkExpect(e2.hasGreaterWeight(e1), true);
    t.checkExpect(e3.hasGreaterWeight(e4), false);
  }


  void testNewGrid(Tester t) {
    t.checkExpect(this.m.newGrid(20, 10).size(), 20);
    t.checkExpect(this.m.newGrid(20, 10).get(0).size(), 10);
  }

  void testCreateEdges(Tester t) {
    t.checkExpect(this.m.createEdges(this.m.newGrid(5, 5), 1, 0).get(19).weight, 1);
    t.checkExpect(this.m.createEdges(this.m.newGrid(5, 5), 1, 0).get(20).weight, 0);
    t.checkExpect(this.m.createEdges(this.m.newGrid(5, 5), 0, 1).get(19).weight, 0);
    t.checkExpect(this.m.createEdges(this.m.newGrid(5, 5), 0, 1).get(20).weight, 1);
  }

  void testFlatten(Tester t) {
    t.checkExpect(this.m.flatten(this.m.newGrid(5, 5)).size(), 25);
    t.checkExpect(this.m.flatten(this.m.newGrid(0, 0)).size(), 0);
  }

  void testSort(Tester t) {
    this.initMazeEdges();

    ArrayList<MazeEdge> checker = new ArrayList<MazeEdge>();
    checker.add(e6);
    checker.add(e5);
    checker.add(e1);
    checker.add(e2);
    checker.add(e3);
    checker.add(e4);

    this.m.sort(mz1, new EdgeWeightComp());
    t.checkExpect(mz1, checker);

    checker = new ArrayList<MazeEdge>();
    checker.add(e1);
    checker.add(e1);
    checker.add(e1);
    checker.add(e1);

    this.m.sort(mz2, new EdgeWeightComp());
    t.checkExpect(mz2, checker);

    checker = new ArrayList<MazeEdge>();
    this.m.sort(mz3, new EdgeWeightComp());
    t.checkExpect(mz3, checker);
  }

  void testDownheap(Tester t) {
    this.initMazeEdges();

    this.m.downheap(mz1, 0, mz1.size(), new EdgeWeightComp());
    t.checkExpect(mz1.get(0), e2);
    t.checkExpect(mz1.get(3), e1);
    t.checkExpect(mz1.get(1), e4);

    this.initMazeEdges();

    this.m.downheap(mz1, 2, mz1.size(), new EdgeWeightComp());
    t.checkExpect(mz1.get(2), e3);
    t.checkExpect(mz1.get(5), e6);

    this.m.downheap(mz1, 3, mz1.size(), new EdgeWeightComp());
    t.checkExpect(mz1.get(3), e4);

    this.m.downheap(mz2, 0, mz2.size(), new EdgeWeightComp());
    t.checkExpect(mz2.get(0), e1);

    this.m.downheap(mz3, 3, mz3.size(), new EdgeWeightComp());
    t.checkExpect(mz3, new ArrayList<MazeEdge>());
  }

  void testSwap(Tester t) {
    this.initMazeEdges();

    this.m.swap(mz1, 0, 3);
    t.checkExpect(mz1.get(0), e4);
    t.checkExpect(mz1.get(3), e1);

    this.m.swap(mz1, 2, 2);
    t.checkExpect(mz1.get(2), e3);
  }

  void testMazeGenerationProperties(Tester t) {
    t.checkExpect(new Maze(20, 4).nodes.size(), 80);
    t.checkExpect(new Maze(20, 20).nodes.size(), 400);
    t.checkExpect(new Maze(4, 4).nodes.size(), 16);
    t.checkExpect(new Maze(7, 10).nodes.size(), 70);
    t.checkExpect(new Maze(42, 35).nodes.size(), 1470);


    Maze testMaze = new Maze(2, 2); //dummy size
    t.checkExpect(testMaze.createEdges(
            testMaze.newGrid(10, 10), 1, 1).size(), 180); // 10 * 9 + 9 * 10
    t.checkExpect(testMaze.createEdges(
            testMaze.newGrid(5, 5), 1, 1).size(), 40);
    t.checkExpect(testMaze.createEdges(
            testMaze.newGrid(7, 14), 1, 1).size(), 175);
  }

  void testInputs(Tester t) {
    MazeWorld w = new MazeWorld(10, 10);
    w.onKeyEvent("d");
    t.checkExpect(w.func instanceof DepthSolver, true);
    w.onKeyEvent("n");
    t.checkExpect(w.func, null);
    w.onKeyEvent("b");
    t.checkExpect(w.func instanceof BreadthSolver, true);
    w.onKeyEvent("v");
    t.checkExpect(w.func, null);
    w.onKeyEvent("m");
    t.checkExpect(w.func instanceof ManualSolver, true);
    w.onKeyEvent("h");
    t.checkExpect(w.func, null);
    w.onKeyEvent("c");
    t.checkExpect(w.func instanceof HeatMap, true);
  }




  void testEdgeWeightComp(Tester t) {
    this.initMazeEdges();
    EdgeWeightComp e = new EdgeWeightComp();
    t.checkExpect(e.compare(this.e1, this.e2), false);
    t.checkExpect(e.compare(this.e1, this.e6), false);
    t.checkExpect(e.compare(this.e4, this.e3), true);
    t.checkExpect(e.compare(this.e2, this.e1), true);
    t.checkExpect(e.compare(this.e3, this.e4), false);
  }
}
