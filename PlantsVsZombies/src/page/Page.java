package page;

import util.Effect;

/**
 * the base of all pages
 * pages should provide an action function for showing and providing result
 * @param <U> Type of result of page
 */
public interface Page<U> {
  /**
   * Show this page to user and wait for obtain a result
   * @return result of page
   */
  Effect<U> action();
}