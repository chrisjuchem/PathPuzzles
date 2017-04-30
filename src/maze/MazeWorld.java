package maze;

import java.awt.Color;

import javalib.impworld.World;
import javalib.impworld.WorldScene;
import solver.BreadthSolver;
import solver.DepthSolver;
import solver.HeatMap;
import solver.ManualSolver;
import solver.MazeFunction;

// Represents the world for the maze game
public class MazeWorld extends World {
  static final int MAX_SCENE_WIDTH = 1200;
  static final int MAX_SCENE_HEIGHT = 900;

  //drawing colors
  public static final Color COLOR_DEFAULT = new Color(255, 255, 255);
  public static final Color COLOR_SEEN = new Color(85, 212, 250);
  public static final Color COLOR_SEMI_ACTIVE = new Color(180, 180, 255);
  public static final Color COLOR_ACTIVE = new Color(44, 105, 212);
  public static final Color COLOR_START = new Color(76, 210, 61);
  public static final Color COLOR_END = new Color(176, 43, 28);

  int width;
  int height;

  int cellSize;

  Maze maze;
  MazeFunction func;

  public MazeWorld(int width, int height) {
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

  public void start() {
    this.bigBang(width * cellSize, height * cellSize , .01);
  }
}

