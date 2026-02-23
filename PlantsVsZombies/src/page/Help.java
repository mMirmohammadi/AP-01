package page;

import util.Effect;
import util.Unit;

public class Help implements Page<Unit> {

  private final String text;

  @Override
  public Effect<Unit> action() {
    return new Message(text).action();
  }

  public Help(String text) {
    this.text = text;
  }

}