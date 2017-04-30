package maze;

import java.util.ArrayList;
import java.util.Hashtable;

import myUtil.BoolComparator;
import javalib.impworld.WorldScene;

// Represents a maze, made up of a list of maze nodes
public class Maze {
  ArrayList<MazeNode> nodes;

  int width;
  int height;

  public Maze(ArrayList<MazeNode> nodes) {
    this.nodes = nodes;
  }
  public Maze(int width, int height) {
    this(width, height, 1, 1);
  }
  public Maze(int width, int height, int horizWeight, int vertWeight) {
    if (width * height <= 1 || width < 0 || height < 0) {
      throw new IllegalArgumentException("Mazes must be big enough "
              + "to have more than 1 node.");
    }

    this.width = width;
    this.height = height;

    nodes = new ArrayList<MazeNode>();

    ArrayList<ArrayList<MazeNode>> grid =
            this.newGrid(width, height);
    ArrayList<MazeEdge> edgeList =
            this.createEdges(grid, horizWeight, vertWeight);
    nodes = this.flatten(grid);
    ArrayList<MazeEdge> edges = this.prunePaths(edgeList);

    for (MazeEdge e : edges) {
      e.node1.connect(e.node2);
      e.node2.connect(e.node1);
    }
  }

  public ArrayList<MazeNode> nodes() {
    return nodes;
  }

  //creates a grid of maze nodes of the specified size
  ArrayList<ArrayList<MazeNode>> newGrid(int width, int height) {
    ArrayList<ArrayList<MazeNode>> nodeGrid =
            new ArrayList<ArrayList<MazeNode>>();
    for (int i = 0; i < width; i += 1) {
      nodeGrid.add(new ArrayList<MazeNode>());
      for (int j = 0; j < height; j += 1) {
        nodeGrid.get(i).add(new MazeNode(i, j));
      }
    }
    return nodeGrid;
  }

  //creates a list of all edges between nodes in the given grid
  ArrayList<MazeEdge> createEdges(ArrayList<ArrayList<MazeNode>> grid,
                                  int horizWeight, int vertWeight) {

    ArrayList<MazeEdge> edgeList = new ArrayList<MazeEdge>();

    //add horizontal edges
    for (int i = 0; i < grid.size() - 1; i += 1) {
      for (int j = 0; j < grid.get(i).size(); j += 1) {
        edgeList.add(new MazeEdge(grid.get(i).get(j),
                grid.get(i + 1).get(j), horizWeight));
      }
    }

    //add vertical edges
    for (int i = 0; i < grid.size(); i += 1) {
      for (int j = 0; j < grid.get(i).size() - 1; j += 1) {
        edgeList.add(new MazeEdge(grid.get(i).get(j),
                grid.get(i).get(j + 1), vertWeight));
      }
    }

    return edgeList;
  }

  //flattens a grid into a single list, reading the grid from
  //left to right, then top down
  <T> ArrayList<T> flatten(ArrayList<ArrayList<T>> grid) {
    ArrayList<T> ret = new ArrayList<T>();
    for (ArrayList<T> list : grid) {
      ret.addAll(list);
    }
    return ret;
  }

  //creates a single path through the maze using Kruskal's Algorithm
  //EFFECT: reorders the given edgeList
  //NOTE***requires initialization of this.nodes
  ArrayList<MazeEdge> prunePaths(ArrayList<MazeEdge> edgeList) {
    this.shuffle(edgeList);
    this.sort(edgeList, new EdgeWeightComp());

    ArrayList<MazeEdge> ret = new ArrayList<MazeEdge>();
    Hashtable<MazeNode, MazeNode> reps = //A Union/Find struct
            new Hashtable<MazeNode, MazeNode>(this.nodes.size());
    this.initReps(reps);

    for (MazeEdge e : edgeList) {
      this.addIfValid(e, ret, reps);
      //abort early if done, not strictly necessary
      if (ret.size() >= this.nodes.size() - 1) {
        return ret;
      }
    }

    //if fail somehow
    throw new RuntimeException("Edge creation failed");
  }

  //EFFECT: shuffles an arraylist
  <T> void shuffle(ArrayList<T> list) {
    for (int i = 0; i < list.size() - 1; i += 1) {
      list.set(i, list.set((int) (Math.random() * (list.size() - i)),
              list.get(i)));
    }
  }

  //EFFECT: Sorts the given arraylist using heapsort and the given comparator
  <T> void sort(ArrayList<T> edgeList, BoolComparator<T> comp) {
    for (int i = edgeList.size() - 1; i >= 0; i -= 1) {
      this.downheap(edgeList, i, edgeList.size(), comp);
    }
    int heapend = edgeList.size();
    while (heapend > 0) {
      this.swap(edgeList, 0, heapend - 1);
      this.downheap(edgeList, 0, heapend - 1, comp);
      heapend -= 1;
    }
  }

  //Pushes the specified element in the heap down if it is not in place with
  //respect to its children and the given comparator
  //EFFECT: Modifies edgeList
  <T> void downheap(ArrayList<T> list, int i, int heapend,
                    BoolComparator<T> comp) {
    if (i < heapend) {
      int l = (2 * i) + 1;
      int r = (2 * i) + 2;
      if (r < heapend && comp.compare(list.get(r), (list.get(i)))
              && comp.compare(list.get(r), (list.get(l)))) {
        this.swap(list, i, r);
        this.downheap(list, r, heapend, comp);
      }
      else if (l < heapend &&
              comp.compare(list.get(l), (list.get(i)))) {
        this.swap(list, i, l);
        this.downheap(list, l, heapend, comp);
      }
    }
  }

  //EFFECT: swaps the elements at the given indices in the given list.
  <T> void swap(ArrayList<T> list, int i, int j) {
    T temp = list.get(i);
    list.set(i, list.get(j));
    list.set(j, temp);
  }

  //inializes the representatives of each node in this maze
  //EFFECT: clears the given hashtable
  //        and maps all nodes of this maze to themselves
  void initReps(Hashtable<MazeNode, MazeNode> reps) {
    reps.clear();
    for (MazeNode n : this.nodes) {
      reps.put(n, n);
    }
  }

  //adds the given edge to the given edge list if the Union/Find struct
  //shows that the two nodes that the edge connects are not already connected
  //EFFECT: potentially adds to the given hashtable
  void addIfValid(MazeEdge e, ArrayList<MazeEdge> eList,
                  Hashtable<MazeNode, MazeNode> reps) {
    MazeNode rep1 = getRep(e.node1, reps);
    MazeNode rep2 = getRep(e.node2, reps);

    if (rep1 != rep2) {
      eList.add(e);
      reps.put(rep1, rep2);
    }

  }

  //gets the representative of the given node in the given Union/find struct
  MazeNode getRep(MazeNode node, Hashtable<MazeNode, MazeNode> reps) {
    MazeNode rep = reps.get(node);
    if (node == rep) {
      return rep;
    }
    else {
      MazeNode actualRep = getRep(rep, reps);
      reps.put(node, actualRep); //keep the tree short
      return actualRep;
    }
  }

  // Draws this maze onto the given WorldScene
  // EFFECT: modifies the given WorldScene
  void drawMaze(WorldScene ws, double nodeSize) {
    for (MazeNode mz : this.nodes) {
      mz.drawNode(ws, MazeWorld.COLOR_DEFAULT, nodeSize);
    }
    this.nodes.get(0).drawNode(ws, MazeWorld.COLOR_START, nodeSize);
    this.nodes.get(this.nodes.size() - 1).drawNode(
            ws, MazeWorld.COLOR_END, nodeSize);
  }
}