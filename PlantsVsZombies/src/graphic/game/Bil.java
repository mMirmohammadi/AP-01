package graphic.game;

import creature.being.plant.Plant;
import exception.InvalidGameMoveException;
import game.GameEngine;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import main.Program;

class Delta {
  double x,y;
}

public class Bil extends ImageView {

  public void removePlant(int x, int y) {
    try {
      GameEngine gameEngine = GameEngine.getCurrentGameEngine();
      Plant plant = gameEngine.getPlant2(x, y);
      if (plant == null)
          throw new InvalidGameMoveException("there is no plant in that location!");
      gameEngine.killPlant(plant);
    } catch (InvalidGameMoveException e) {
    } catch (NumberFormatException e) {
    }
  }

  public Bil(double x, double y, double size) {
    this.setTranslateX(x);
    this.setTranslateY(y);
    this.setFitWidth(size);
    this.setFitHeight(size);
    this.setImage(new Image(
            Program.getRes("images/shovel.png")
    ));
    final Delta dragDelta = new Delta();
    Node label = this;
    label.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        // record a delta distance for the drag and drop operation.
        dragDelta.x = label.getLayoutX() - mouseEvent.getSceneX();
        dragDelta.y = label.getLayoutY() - mouseEvent.getSceneY();
        label.setCursor(Cursor.MOVE);
      }
    });
    label.setOnMouseReleased(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        label.setCursor(Cursor.HAND);
        double x = label.getTranslateX()+label.getLayoutX();
        double y = label.getTranslateY()+label.getLayoutY();
        x /= Program.screenX / 10;
        y /= Program.screenY/5;
        removePlant((int)x, (int)y);
        label.setLayoutX(0);
        label.setLayoutY(0);
      }
    });
    label.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        label.setLayoutX(mouseEvent.getSceneX() + dragDelta.x);
        label.setLayoutY(mouseEvent.getSceneY() + dragDelta.y);
      }
    });
    label.setOnMouseEntered(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        label.setCursor(Cursor.HAND);
      }
    });
  }
}