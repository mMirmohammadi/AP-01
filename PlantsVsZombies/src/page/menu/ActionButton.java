package page.menu;

import util.Effect;
import util.Unit;

public class ActionButton<U> implements Button<U> {
  private String label;
  private Effect<Unit> task;

  public String getLabel(){
    return label;
  }

  public Effect<U> action() {
    return task.flatMap(x -> Effect.error(new Error("end action")));
  }

  public ActionButton(String label, Effect<Unit> task) {
    this.label = label;
    this.task = task;
  }

  @Override
  public String toString() {
    return "LinkButton [label=" + label + "]";
  }

  @Override
  public String getHelp() {
    return "This button will " + label;
  }
}