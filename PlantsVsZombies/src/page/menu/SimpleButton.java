package page.menu;

import util.Effect;

public class SimpleButton<U> implements Button<U> {

  private final String name, help;
  private final Effect<U> effect;

  @Override
  public String getLabel() {
    return name;
  }

  @Override
  public String getHelp() {
    return help;
  }

  @Override
  public Effect<U> action() {
    return effect;
  }

  public SimpleButton(String name, Effect<U> effect) {
    this.name = name;
    this.help = "This button will do "+name;
    this.effect = effect;
  }

}