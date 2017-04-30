package myUtil;

import java.awt.Color;

public class ColorMixer {

  // Mixes the given colors, with 0 being all color c2 and 1 being all color c1,
  // according to the given factor
  public static Color mix(Color c1, Color c2, double factor) {
    if (factor > 1 || factor < 0) {
      throw new IllegalArgumentException("Factor must be between 0 & 1.");
    }
    int r = (int) (c1.getRed() * factor + c2.getRed() * (1 - factor));
    int g = (int) (c1.getGreen() * factor + c2.getGreen() * (1 - factor));
    int b = (int) (c1.getBlue() * factor + c2.getBlue() * (1 - factor));

    return new Color(r, g, b);
  }
}