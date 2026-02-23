package page;

import creature.Dna;
import graphic.CloseButton;
import graphic.SimpleButton;
import graphic.card.SelectableCard;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.Program;
import util.Effect;

import java.util.ArrayList;
import java.util.function.Supplier;

public class Collection<U extends Dna> implements Page<ArrayList<U>> {
  private Supplier<ArrayList<U>> optionsSupplier;
  private int count;

  public Collection(Supplier<ArrayList<U>> optionsSupplier, int count) {
    this.optionsSupplier = optionsSupplier;
    this.count = count;
  }

  @Override
  public Effect<ArrayList<U>> action() {
    return new Effect<>(h -> {
      ArrayList<U> result = new ArrayList<>();
      ArrayList<U> options = optionsSupplier.get();

      javafx.scene.image.Image image = new Image(Program.getRes("images/wallpaper.jpg"));
      Background background = new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
              BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false)));

      final double X = Program.screenX;
      final double Y = Program.screenY;

      VBox vBox = new VBox();
      vBox.setFillWidth(true);
      vBox.setAlignment(Pos.TOP_CENTER);

      CloseButton closeButton = new CloseButton();
      closeButton.setOnMouseClicked(e -> {
        h.failure(new Error("form aborted"));
      });
      HBox hBox = new HBox();
      hBox.getChildren().add(closeButton);
      hBox.setAlignment(Pos.CENTER_LEFT);
      vBox.getChildren().add(hBox);

      Text A = new Text("Click on card to select");
      A.setWrappingWidth(X);
      A.setTextAlignment(TextAlignment.CENTER);
      A.setFill(Color.WHITESMOKE);
      A.setFont(Font.font(Y / 50));

      GridPane gridPane = new GridPane();

      gridPane.setHgap(X / 25);
      gridPane.setVgap(Y / 15);
      gridPane.setAlignment(Pos.CENTER);

      SimpleButton next = new SimpleButton(
              0, 0,
              Program.screenX / 10,
              Program.screenY / 15,
              "0/" + count,
              Effect.syncWork(() -> {
                if (result.size() > count) return;
                h.success(result);
              })
      );

      final int col = 8;
      int cnt = 0;

      for (U option : options) {
        SelectableCard card = new SelectableCard(
                option,
                0, 0,
                Program.screenX / 18,
                Effect.syncWork(() -> {
                  if (!result.contains(option)) {
                    result.add(option);
                  } else {
                    result.remove(option);
                  }
                  next.setText(result.size() + "/" + count);
                })
        );
        gridPane.add(card, cnt % col, cnt / col);
        cnt++;
      }

      vBox.getChildren().addAll(A, next, gridPane);

      Rectangle rec = new Rectangle();
      rec.setHeight(Y);
      rec.setWidth(col * (X / 18) + (col + 1) * (X / 25));
      rec.setX((X - rec.getWidth()) / 2);
      rec.setFill(Color.rgb(0, 0, 0, 0.9));

      StackPane stackPane = new StackPane();

      stackPane.setBackground(background);
      stackPane.getChildren().addAll(rec, vBox);

      vBox.setSpacing(Y / 20);


      Program.stage.getScene().setRoot(stackPane);
    });
  }

}