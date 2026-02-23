package page.menu;

import util.Effect;
import util.Unit;

public class ExitButton implements Button<Unit> {

  private final String label;

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public String getHelp() {
    return "This button will "+label;
  }

  @Override
  public Effect<Unit> action() {
    return Effect.ok();
  }

  public ExitButton(String label) {
    this.label = label;
  }
  
}