package page.menu;

import page.Help;
import page.Page;
import util.Effect;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import main.Program;
import graphic.CloseButton;
import graphic.SimpleButton;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Menu is a page that show some {@link Button} to user
 * and when user click on some button, run action of that button
 *
 * @param <U> type of result of Menu
 */
public class Menu<U> implements Page<U> {

  private final ArrayList<Button<U>> buttons;
  private String message = "Choose an option, or e for exit and h for help";

  public Menu(final ArrayList<Button<U>> buttons) {
    this.buttons = buttons;
  }

  @SafeVarargs
  public Menu(final Button<U>... buttons) {
    this.buttons = Stream.of(buttons).collect(Collectors.toCollection(ArrayList::new));
  }

  @SafeVarargs
  public Menu(String description, final Button<U>... buttons) {
    this.buttons = Stream.of(buttons).collect(Collectors.toCollection(ArrayList::new));
  }

  public Effect<U> action() {
    return new Effect<>((h) -> {
      Pane myPane = new Pane();
      ImageView imageView = new ImageView(new Image(
        Program.getRes("images/menu/background.png")
      ));
      imageView.setFitHeight(Program.screenY);
      imageView.setFitWidth(Program.screenX);
      myPane.getChildren().add(imageView);
      int cnt = 0, n = buttons.size();
      CloseButton closeButton = new CloseButton();
      closeButton.setOnMouseClicked(e -> {
        h.failure(new Error("end menu"));
      });
      myPane.getChildren().add(closeButton);
      for (final Button<U> button: buttons) {
        SimpleButton r = new SimpleButton(
          Program.screenX*0.6,
          (1.5*cnt+1.5)*Program.screenY/(2*n+1),
          Program.screenX*0.22, 
          0.6*Program.screenY/(2*n+1),
          button.getLabel(),
          button.action().consume(h::success).catchThen(e -> Effect.syncWork(()->{
            Program.stage.getScene().setRoot(myPane);
          }))
        );
        myPane.getChildren().add(r);
        cnt++;
      }
      Program.stage.getScene().setRoot(myPane);
    });
  }

  private void printHelp() {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < buttons.size(); i++) {
      s.append(i).append(". ").append(buttons.get(i).getLabel()).append("\n");
      s.append("    ").append(buttons.get(i).getHelp()).append("\n");
    }
    new Help(s.toString()).action();
  }

  @Override
  public String toString() {
    return "Menu [buttons=" + buttons + "]";
  }
}