package page.menu;

import util.Effect;

/**
 * Button is an object that provide a name, a work and a description
 * @param <U>
 */
public interface Button<U> {
  /**
   * @return name of button
   */
  String getLabel();
  /**
   * @return description of button
   */
  String getHelp();
  /**
   * run button action
   * @return result of action
   */
  Effect<U> action();
}
