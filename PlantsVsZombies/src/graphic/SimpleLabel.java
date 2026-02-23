package graphic;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class SimpleLabel extends Text {
  public SimpleLabel(double x, double y, double size, String text) {
    super(text);
    this.setTranslateX(x);
    this.setTranslateY(y);
    this.setFont(Font.font(size));
  }
}