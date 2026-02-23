package page;

import java.util.ArrayList;

import main.Program;
import graphic.CloseButton;
import graphic.SimpleButton;
import graphic.SimpleLabel;
import graphic.Wallpaper;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import util.Effect;

/**
 * ask some questions from user
 */
public class Form implements Page<String[]> {

  private String[] asks;

  public Effect<String[]> action() {
    return new Effect<>(h -> {
      Pane myPane = new Pane();
      Wallpaper.addTo(myPane);
      int cnt = 0, n = asks.length+1;
      double sizeY = Program.screenY/(2*n+1)/3;
      double sizeX = Program.screenX/2;
      double xMargin = Program.screenX/4;
      CloseButton closeButton = new CloseButton();
      closeButton.setOnMouseClicked(e -> {
        h.failure(new Error("form aborted"));
      });
      myPane.getChildren().add(closeButton);
      ArrayList<TextField> myTextFields = new ArrayList<>();
      for (final String ask: asks) {
        SimpleLabel r = new SimpleLabel(
          xMargin,
          (2*cnt+1)*Program.screenY/(2*n+1),
          sizeY,
          ask
        );
        TextField textField = new TextField();
        textField.setTranslateX(xMargin);
        textField.setTranslateY((2*cnt+1)*Program.screenY/(2*n+1));
        textField.setPrefHeight(sizeY);
        textField.setPrefWidth(sizeX);
        textField.setFont(Font.font(sizeY));
        myTextFields.add(textField);
        myPane.getChildren().add(r);
        myPane.getChildren().add(textField);
        cnt++;
      }
      myPane.getChildren().add(new SimpleButton(
        xMargin, (2*n-1)*Program.screenY/(2*n+1), sizeX, sizeY*2, "OK",
        Effect.syncWork(()->{
          h.success(myTextFields
            .stream()
            .map(textField -> textField.getText())
            .toArray(String[]::new)
          );
        })
      ));
      Program.stage.getScene().setRoot(myPane);
    });
  }

  public Form(String ...asks) {
    this.asks = asks;
  }
}