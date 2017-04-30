package maze;


import java.awt.Color;
import java.util.Iterator;

import javalib.impworld.WorldScene;
import javalib.worldimages.AlignModeX;
import javalib.worldimages.AlignModeY;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayOffsetAlign;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;

// Represents a node of the maze
public class MazeNode implements Iterable<MazeNode> {

  private int x; //top left 0 0
  private int y;

  //references to adjacent connected nodes.
  //points to this if not connected in a particular direction
  public MazeNode above;
  public MazeNode below;
  public MazeNode left;
  public MazeNode right;

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
  public void drawNode(WorldScene ws, Color c, double nodeSize) {
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
