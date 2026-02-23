package graphic;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import main.Program;

public class Wallpaper extends ImageView {
  public Wallpaper() {
    this.setImage(new Image(
      Program.getRes("images/wallpaper.jpg")
    ));
    this.setFitWidth(Program.screenX);
    this.setFitHeight(Program.screenY);
  }

  public static void addTo(Pane pane) {
    pane.getChildren().add(new Wallpaper());
  }

    public static Background background() {
        javafx.scene.image.Image image = new Image(Program.getRes("images/wallpaper.jpg"));
        return new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false)));
    }
}