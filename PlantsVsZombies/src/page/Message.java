package page;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.Program;
import util.Effect;
import util.Unit;

/**
 * show a message string
 */
public class Message implements Page<Unit> {

  private final String message;

  @Override
  public Effect<Unit> action() {
    return new Effect<>(h -> {
      try {
        System.out.println(message);
        Group g = new Group();
        Rectangle r = new Rectangle();
        r.setHeight(Program.screenY);
        r.setWidth(Program.screenX);
          r.setOpacity(0.8);
          Text t = new Text(message + "\n\nclick to continue!");
          t.setFont(Font.font(Program.screenY / 15));
          t.setTranslateY(Program.screenY / 3);
        t.setWrappingWidth(Program.screenX);
          t.setTextAlignment(TextAlignment.CENTER);
          t.setFill(Color.WHITESMOKE);
        g.getChildren().add(r);
        g.getChildren().add(t);
        Pane root = (Pane) Program.stage.getScene().getRoot();
        root.getChildren().add(g);
          r.setOnMouseClicked(e -> {
              root.getChildren().remove(g);
              h.success(Unit.value);
          });
          t.setOnMouseClicked(e -> {
              root.getChildren().remove(g);
              h.success(Unit.value);
          });
      } catch (Throwable e) {
        e.printStackTrace();
        System.exit(0);
      }
    });
  }

  /**
   * @param message The message to be shown
   */
  public Message(String message) {
    this.message = message;
  }

  public static Effect<Unit> show(String message) {
    return (new Message(message)).action();
  }
}