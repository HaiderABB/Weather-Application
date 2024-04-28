package Src.WeatherAppRunner;

import Src.AppUI.App;
import Src.AppUI.TerminalUI;
import Src.BusinessLogic.DUIFiller;
import Src.BusinessLogic.TUIFiller;

public class WeatherAppRunner {
  public static void main(String[] args) {
    // IF TerminalUI
    char input = 'T';

    if (input == 'T') {
      TUIFiller TUI = new TUIFiller();
      TerminalUI terminal = new TerminalUI(TUI);
      terminal.RunF();
      // TUI.RunTerminal();
    } else if (input == 'D') {
      launchAppUI();
    } else {
      System.out.println("Invalid input. Please enter 'T' or 'D'.");
    }

  }

  private static void launchAppUI() {
    // Launch JavaFX Application (AppUI)
    App.launch(App.class);
  }

}
