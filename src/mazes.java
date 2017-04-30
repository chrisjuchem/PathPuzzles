//Assignment 10
//Juchem, Chris
//chrisjuchem15
//Day, Trevor
//trevday

/* 
 * README:
 * 
 * Controls:
 * 
 * n: new random maze
 * h: new horizontally biased maze
 * v: new vertically biased maze
 * 
 * c: color by distance from start
 * 
 * b: (re)start breath first search
 * d: (re)start depth first search
 * m: begin manual solve (use arrow keys)
 * 
 * 
 * Extra Credit Features:
 * Restarting without closing
 * Biased mazes(press 'v' or 'h')
 * Color by distance from start
 * 
 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;
import tester.Tester;
import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.*;

class Util {
    
    // Mixes the given colors, with 0 being all color c2 and 1 being all color c1,
    // according to the given factor
    Color mix(Color c1, Color c2, double factor) {
        if (factor > 1 || factor < 0) {
            throw new IllegalArgumentException("Factor must be between 0 & 1.");
        }
        int r = (int) (c1.getRed() * factor + c2.getRed() * (1 - factor));
        int g = (int) (c1.getGreen() * factor + c2.getGreen() * (1 - factor));
        int b = (int) (c1.getBlue() * factor + c2.getBlue() * (1 - factor));
        
        return new Color(r, g, b);
    }
}

//============================================================================

// Represents a node of the maze
class MazeNode implements Iterable<MazeNode> {
    
    int x; //top left 0 0
    int y;
    
    //references to adjacent connected nodes.
    //points to this if not connected in a particular direction 
    MazeNode above;
    MazeNode below;
    MazeNode left;
    MazeNode right;
    
    MazeNode(int x, int y) {
        this.x = x;
        this.y = y;
        
        this.above = this;
        this.below = this;
        this.left = this;
        this.right = this;
    }
    
    // Draws this node according to the given color onto the given WorldScene,
    // correctly placing its edges
    // EFFECTS: modifies the given WorldScene
    void drawNode(WorldScene ws, Color c, double nodeSize) {
        // double for specific placement,
        // int for convenient drawing
        int size = (int) Math.ceil(nodeSize) + 1; 
        WorldImage img = new RectangleImage(size, size, OutlineMode.SOLID, c);
        if (this.above == this) {
            img = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP,
                    new RectangleImage(size, 1, OutlineMode.SOLID,
                    Color.BLACK), 0, 0, img);
        }
        if (this.below == this) {
            img = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM,
                    new RectangleImage(size, 1, OutlineMode.SOLID,
                    Color.BLACK), 0, 0, img);
        }
        if (this.left == this) {
            img = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.MIDDLE,
                    new RectangleImage(1, size, OutlineMode.SOLID,
                    Color.BLACK), 0, 0, img);
        }
        if (this.right == this) {
            img = new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE,
                    new RectangleImage(1, size, OutlineMode.SOLID,
                    Color.BLACK), 0, 0, img);
        }
        
        ws.placeImageXY(img, (int) ((this.x * nodeSize) + (nodeSize / 2)),
                (int) ((this.y * nodeSize) + (nodeSize / 2)));
    }
    
    //Assuming the given node is adjacent to this one, updates the appropriate
    //reference to reflect the connection
    //EFFECT: modifies one of the MazeNode references
    //NOTE: call twice, once on each node to make a mutual connection
    void connect(MazeNode that) {
        if (this.x == that.x && this.y == that.y + 1) {
            this.above = that;
        } 
        else if (this.x == that.x && this.y + 1 == that.y) {
            this.below = that;
        } 
        else if (this.x == that.x + 1 && this.y == that.y) {
            this.left = that;
        } 
        else if (this.x + 1 == that.x && this.y == that.y) {
            this.right = that;    
        } 
        else {
            throw new RuntimeException("Cannot connect a non-adjacent node.");
        } 
    }

    // Returns an iterator for maze nodes
    public Iterator<MazeNode> iterator() {
        return new NodeIterator(this);
    }
}

//returns a nodes adjacencies in this order: above, left, below, right
//(clockwise from top)
class NodeIterator implements Iterator<MazeNode> {
    
    int counter; // the number of items that have been given
    MazeNode node;
   
    NodeIterator(MazeNode node) {
        this.node = node;
        this.counter = 0;
    }

    // Checks of this NodeIterator has a next
    public boolean hasNext() {
        return this.counter < 4;
    }

    // Returns the current next and moves the iterator forward
    // EFFECTS: changes counter
    public MazeNode next() {
        counter += 1;
        if (counter == 1) {
            return node.above;
        }
        else if (counter == 2) {
            return node.left;
        }
        else if (counter == 3) {
            return node.below; 
        }
        else if (counter == 4) {
            return node.right;
        }
        else {
            throw new RuntimeException("No next.");
        }
    }
   
    public void remove() {
        throw new UnsupportedOperationException(); 
    }
}

//==============================================================================

// Represents an edge of the maze from one node to another, with a weight
class MazeEdge {
    MazeNode node1;
    MazeNode node2;
    int weight;
    
    MazeEdge(MazeNode node1, MazeNode node2, int weight) {
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
    }
    
    // Returns whether this node's weight is greater than that node's weight
    boolean hasGreaterWeight(MazeEdge that) {
        return this.weight > that.weight;
    }
}

// Represents a maze, made up of a list of maze nodes
class Maze {
    ArrayList<MazeNode> nodes;

    int width;
    int height;
    
    Maze(ArrayList<MazeNode> nodes) {
        this.nodes = nodes;
    }
    Maze(int width, int height) {
        this(width, height, 1, 1);
    }
    Maze(int width, int height, int horizWeight, int vertWeight) {
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
    <T> void sort(ArrayList<T> edgeList, Comparator<T> comp) {
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
            Comparator<T> comp) {
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

// Represents the world for the maze game
class MazeWorld extends World {
    static final int MAX_SCENE_WIDTH = 1200;
    static final int MAX_SCENE_HEIGHT = 900;
    
    //drawing colors
    static final Color COLOR_DEFAULT = new Color(255, 255, 255);
    static final Color COLOR_SEEN = new Color(85, 212, 250);
    static final Color COLOR_SEMI_ACTIVE = new Color(180, 180, 255);
    static final Color COLOR_ACTIVE = new Color(44, 105, 212);
    static final Color COLOR_START = new Color(76, 210, 61);
    static final Color COLOR_END = new Color(176, 43, 28);
    
    int width;
    int height;
    
    int cellSize;
    
    Maze maze;
    MazeFunction func;
    
    MazeWorld(int width, int height) {
        this.width = width;
        this.height = height;
        this.maze = new Maze(width, height);
        this.func = null;
        
        this.cellSize = Math.min(MAX_SCENE_WIDTH / width,
            MAX_SCENE_HEIGHT / height);
    }
    
    //handle inputs
    public void onKeyEvent(String key) {
        key = key.toLowerCase();
        if (key.equals("b")) {
            this.func = new BreadthSolver(this.maze);
        }
        else if (key.equals("d")) {
            this.func = new DepthSolver(this.maze);
        }
        else if (key.equals("c")) {
            this.func = new HeatMap(this.maze);
        }
        else if (key.equals("m")) {
            this.func = new ManualSolver(this.maze);
        }
        else if (key.equals("n")) {
            this.maze = new Maze(this.width, this.height);
            this.func = null;
        }
        else if (key.equals("v")) {
            this.maze = new Maze(this.width, this.height, 1, 0);
            this.func = null;
        }
        else if (key.equals("h")) {
            this.maze = new Maze(this.width, this.height, 0, 1);
            this.func = null;
        }
        else if (key.equals("left") || key.equals("down") || 
                key.equals("right") || key.equals("up")) {
            if (func instanceof ManualSolver) {
                ((ManualSolver) func).updateInput(key);
            }
        }
    }
    
    //progresses the solver if present and not yet finished
    public void onTick() {
        if (this.func != null && this.func.canStep()) {
            this.func.step();
        }
    }
    
    // Draws this MazeWorld
    public WorldScene makeScene() {
        WorldScene temp = new WorldScene(width * cellSize, height * cellSize);
        this.maze.drawMaze(temp, cellSize);
        if (this.func != null) { 
            this.func.draw(temp, cellSize);
        }
        return temp;
    }
    
    void start() {
        this.bigBang(width * cellSize, height * cellSize , .05);
    }
}

//=============================================================================

//a binary comparator between 2 objects of the same type
interface Comparator<T> {
    boolean compare(T one, T two);
} 

//compares edges based on their weights
class EdgeWeightComp implements Comparator<MazeEdge> {
    public boolean compare(MazeEdge one, MazeEdge two) {
        return one.hasGreaterWeight(two);
    }
}

//===================================================================

// Represents a maze traversal function
abstract class MazeFunction {
    Maze maze;    
    MazeNode start;

    MazeFunction(Maze maze) {
        this.maze = maze;
        this.start = maze.nodes.get(0);
    }

    // Draws this MazeFunction accordingly onto the given WorldScene
    // using the given nodeSize
    // EFFECTS: modifies the given WorldScene
    abstract void draw(WorldScene ws, double nodeSize);
    
    // Checks if this MazeFunction can perform another step in the current maze
    abstract boolean canStep();
    
    // Steps this MazeFunction
    abstract void step();
}


// Traverses the maze and creates a heat map of the maze
class HeatMap extends MazeFunction {
    ArrayList<ArrayList<MazeNode>> tiers;
    
    ArrayList<MazeNode> worklist;
    
    HeatMap(Maze maze) {
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
        Util u = new Util(); 
    
        for (int i = 0; i < tiers.size(); i += 1) {
            for (MazeNode n : tiers.get(i)) {
                n.drawNode(ws, u.mix(MazeWorld.COLOR_END, MazeWorld.COLOR_START,
                    ((double) i / tiers.size())), nodeSize);
            }
        }
        
    }

}

// Represents a MazeFunction that traverses and solves the maze
abstract class MazeSolver extends MazeFunction {
    MazeNode end;

    //map of links from nodes to the nodes those nodes were reached from
    Hashtable<MazeNode, MazeNode> paths;
    
    MazeSolver(Maze maze) {
        super(maze);
    
        this.end = maze.nodes.get(maze.nodes.size() - 1);
        
        this.paths = new Hashtable<MazeNode, MazeNode>();
        paths.put(this.start, this.start);
    }
    
    // Checks if this MazeSolver can perform another step in the current maze
    public boolean canStep() {
        return !this.paths.containsKey(this.end);
    }
    
    // Returns a list of all paths from the given node
    ArrayList<MazeNode> pathFrom(MazeNode node) {
        if (!this.paths.containsKey(node)) {
            throw new RuntimeException("Path to this node not yet found.");
        }
        
        ArrayList<MazeNode> ret = new ArrayList<MazeNode>();
        
        MazeNode to = node;
        MazeNode from = this.paths.get(node);
        ret.add(node);
        
        while (to != from) {
            to = from;
            from = this.paths.get(to);
            
            ret.add(to);
        }
        
        return ret;
    }
}

// Represents a MazeSolver for manual solving of the maze
class ManualSolver extends MazeSolver {
    
    MazeNode active;
 
    String input;
 
    ManualSolver(Maze maze) {
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
    void updateInput(String i) {
        this.input = i;
    }
    
}

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

// Represents an automatic solving of the maze using breadth first search
class BreadthSolver extends AutoSolver {
    BreadthSolver(Maze maze) {
        super(maze);
    }

    //adds the node to the correct end of the worklist for this type of search
    //EFFECT: modifies this.worklist
    void addToWorklist(MazeNode n) {
        this.worklist.addToTail(n);
    }
}

// Represents an automatic solving of the maze using depth first search
class DepthSolver extends AutoSolver {
    DepthSolver(Maze maze) {
        super(maze);
    }
    
    //adds the node to the correct end of the worklist for this type of search
    //EFFECT: modifies this.worklist
    void addToWorklist(MazeNode n) {
        this.worklist.addToHead(n);
    }
}

//=============================================================================

class Deque<T> implements Iterable<T> {
    Sentinel<T> header;
    
    Deque() {
        this.header = new Sentinel<T>();
    }
    

    //determines if this deque contains no data
    boolean isEmpty() {
        return this.header.next == this.header 
            && this.header.prev == this.header;
    }

    //adds the given data to the head of this deque
    void addToHead(T data) {
        new Node<T>(data, this.header.next, this.header);
    }
    
    //adds the given data to the tail of this deque
    void addToTail(T data) {
        new Node<T>(data, this.header, this.header.prev);
    }

    //removes the node from the head of the deque and returns that node's value
    T removeFromHead() {
        if (this.isEmpty()) {
            throw new RuntimeException("Nothing to remove.");
        }
        else {
            Node<T> node = (Node<T>) this.header.next;
            node.remove();
            return node.data;
        }
    }
    
    //removes the node from the tail of the deque and returns that node's value
    T removeFromTail() {
        if (this.isEmpty()) {
            throw new RuntimeException("Nothing to remove.");
        }
        else {
            Node<T> node = (Node<T>) this.header.prev;
            node.remove();
            return node.data;
        }
    }

    //returns the data of the node at the head, if present
    T peek() {
        return this.header.next.peek();
    }
    
    public Iterator<T> iterator() {
        return new DequeIter<T>(this);
    }
}
abstract class ANode<T> {
    ANode<T> next;
    ANode<T> prev;

    //returns true if the node is a sentinel
    abstract boolean isSentinel();
    //returns the data of the node at the head, if present
    abstract T peek();
}
class Sentinel<T> extends ANode<T> {
    public Sentinel() {
        this.next = this;
        this.prev = this;
    }

    //returns true if the node is a sentinel
    boolean isSentinel() {
        return true;
    }

    //returns the data of the node at the head, if present
    T peek() {
        throw new RuntimeException("Nothing to peek.");
    }
}
class Node<T> extends ANode<T> {
    T data;
    
    Node(T data, ANode<T> next, ANode<T> prev) {
        this.data = data;
        this.next = next;
        this.prev = prev;
        
        prev.next = this;
        next.prev = this;
    }
    
    //removes this node from its deque
    void remove() {
        this.next.prev = this.prev;
        this.prev.next = this.next;
    }

    //returns true if the node is a sentinel
    boolean isSentinel() {
        return false;
    }

    //returns the data of the node at the head, if present
    T peek() {
        return this.data;
    }
}

// Represents an iterator over Deques
class DequeIter<T> implements Iterator<T> {
    ANode<T> next;
    public DequeIter(Deque<T> d) {
        this.next = d.header.next;
    }
    public boolean hasNext() {
        return !this.next.isSentinel();
    }
    public T next() {
        if (this.hasNext()) {
            T ret = ((Node<T>) next).data;
            this.next = this.next.next;
            return ret;
        }
        else {
            throw new RuntimeException("No Next.");
        }
        
    }
    public void remove() {
        throw new UnsupportedOperationException(); 
    }
}


//=============================================================================

class MazeExamples {

    public static void main(String[] args) {
        MazeWorld w = new MazeWorld(400, 300);
        w.start();
    }
    void testGame(Tester t) {
        MazeWorld w = new MazeWorld(40, 30);
        w.start();
    }
  
    // Image functions are not tested as Professor Lerner said they do not have to be tested
  
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
    ArrayList<MazeNode> simpleMaze() {
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
    
    void testMix(Tester t) {
        Util u = new Util();
        t.checkExpect(u.mix(Color.BLACK, Color.BLUE, 0.0), Color.BLUE);
        t.checkExpect(u.mix(Color.BLACK, Color.BLUE, 1.0), Color.BLACK);
        t.checkExpect(u.mix(Color.BLACK, Color.BLUE, 0.5),
                new Color(0, 0, 127));
        t.checkExpect(u.mix(Color.RED, Color.GREEN, 0.5), 
                new Color(127, 127, 0));
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
    
    void testEdgeWeightComp(Tester t) {
        this.initMazeEdges();
        EdgeWeightComp e = new EdgeWeightComp();
        t.checkExpect(e.compare(this.e1, this.e2), false);
        t.checkExpect(e.compare(this.e1, this.e6), false);
        t.checkExpect(e.compare(this.e4, this.e3), true);
        t.checkExpect(e.compare(this.e2, this.e1), true);
        t.checkExpect(e.compare(this.e3, this.e4), false);
    }    
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
        ArrayList<MazeNode> nodes = this.simpleMaze();
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
        ArrayList<MazeNode> nodes = this.simpleMaze();
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
    void testDeque(Tester t) {
        Deque<Integer> d1 = new Deque<Integer>();
        t.checkExpect(d1.isEmpty(), true);
        d1.addToHead(4);
        d1.addToHead(2);
        d1.addToHead(7);
        d1.addToHead(6);
        
        Iterator<Integer> i1 = new DequeIter<Integer>(d1);
        t.checkExpect(i1.hasNext(), true);
        t.checkExpect(i1.next(), 6);
        t.checkExpect(i1.next(), 7);
        t.checkExpect(i1.next(), 2);
        t.checkExpect(i1.hasNext(), true);
        t.checkExpect(i1.next(), 4);
        t.checkExpect(i1.hasNext(), false);
        
        
        t.checkExpect(d1.isEmpty(), false);
        t.checkExpect(d1.peek(), 6);
        d1.removeFromHead();
        t.checkExpect(d1.peek(), 7);
        d1.removeFromTail();
        t.checkExpect(d1.peek(), 7);
        d1.removeFromHead();
        t.checkExpect(d1.peek(), 2);
        t.checkExpect(d1.isEmpty(), false);
        d1.removeFromHead();
        t.checkExpect(d1.isEmpty(), true);
    }
}