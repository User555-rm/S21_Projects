package org.example;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import java.awt.*;
import org.example.infrastructure.controller.MenuController;

public class Main {

  private static final int FONT_SIZE = 18;
  private static final int WINDOW_SIZE_VERTICAL = 60;
  private static final int WINDOW_SIZE_HORIZONTAL = 160;

  public static void main(String[] args) throws Exception {

    SwingTerminalFontConfiguration fontConfig = createFontConfig();

    DefaultTerminalFactory factory =
        new DefaultTerminalFactory()
            .setInitialTerminalSize(
                new com.googlecode.lanterna.TerminalSize(
                    WINDOW_SIZE_HORIZONTAL, WINDOW_SIZE_VERTICAL))
            .setTerminalEmulatorFontConfiguration(fontConfig);

    Screen screen = factory.createScreen();
    screen.startScreen();

    MenuController menuController = new MenuController();
    menuController.runMenu(screen);

    screen.stopScreen();
  }

  // на винде и линуксе нужны разные шрифты
  private static SwingTerminalFontConfiguration createFontConfig() {
    try {
      return SwingTerminalFontConfiguration.newInstance(
          new Font("Consolas", Font.PLAIN, FONT_SIZE));
    } catch (IllegalArgumentException e) {
      return SwingTerminalFontConfiguration.newInstance(
          new Font("Monospaced", Font.PLAIN, FONT_SIZE));
    }
  }
}
