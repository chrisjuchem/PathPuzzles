package myUtil;

import java.awt.*;

import tester.Tester;

public class MixTest {

  void testMix(Tester t) {
    ColorMixer u = new ColorMixer();
    t.checkExpect(u.mix(Color.BLACK, Color.BLUE, 0.0), Color.BLUE);
    t.checkExpect(u.mix(Color.BLACK, Color.BLUE, 1.0), Color.BLACK);
    t.checkExpect(u.mix(Color.BLACK, Color.BLUE, 0.5),
            new Color(0, 0, 127));
    t.checkExpect(u.mix(Color.RED, Color.GREEN, 0.5),
            new Color(127, 127, 0));
  }
}
