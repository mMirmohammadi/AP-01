package page;

import page.menu.Menu;
import page.menu.SimpleButton;
import util.Effect;

public class QuestionForm implements Page<Integer> {

  private String[] options;

  @Override
  public Effect<Integer> action() {
    return new Menu<Integer>(
      new SimpleButton<>(options[0], Effect.ok(0)),
      new SimpleButton<>(options[1], Effect.ok(1))
    ).action();
  }

  public QuestionForm(String ...options) {
    this.options = options;
  }

}