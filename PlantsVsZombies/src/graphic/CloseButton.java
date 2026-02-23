package graphic;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import main.Program;

public class CloseButton extends Group {
  public CloseButton() {
    super();
    Circle circle = new Circle();
    circle.setFill(Color.RED);
    circle.setCenterX(Program.screenX / 80);
    circle.setCenterY(Program.screenX / 80);
    circle.setRadius(Program.screenX / 80);
    getChildren().add(circle);
    this.setOnMouseEntered(e -> {
      circle.setFill(Color.PURPLE);
    });
    this.setOnMouseExited(e -> {
      circle.setFill(Color.RED);
    });
  }
}