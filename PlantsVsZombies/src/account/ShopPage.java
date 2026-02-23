package account;

import creature.being.BeingDna;
import creature.being.plant.PlantDna;
import creature.being.zombie.ZombieDna;
import graphic.CloseButton;
import graphic.card.ShopCard;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.Program;
import page.Page;
import util.Effect;
import util.Unit;

import java.util.ArrayList;
import java.util.List;

public class ShopPage implements Page<Unit> {

  private Text moneyTxt;
  private ArrayList<ShopCard> cards = new ArrayList<>();
  public StackPane root;

  public void update() {
    moneyTxt.setText(Account.getCurrentAccount().getStore().money + "$");
    for (ShopCard card : cards)
      card.update();
  }

  @Override
  public Effect<Unit> action() {
    return Program.reloadAll().then(new Effect<>(h -> {
      javafx.scene.image.Image image = new Image(Program.getRes("images/wallpaper.jpg"));
      Background background = new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
              BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false)));

      final double X = Program.screenX;
      final double Y = Program.screenY;

      moneyTxt = new Text();
      moneyTxt.setWrappingWidth(X);
      moneyTxt.setTextAlignment(TextAlignment.CENTER);
      moneyTxt.setFill(Color.RED);
      moneyTxt.setFont(Font.font("monospaced", FontWeight.EXTRA_BOLD, Y / 60));

      VBox vBox = new VBox();
      vBox.setFillWidth(true);
      vBox.setAlignment(Pos.TOP_CENTER);

      CloseButton closeButton = new CloseButton();
      closeButton.setOnMouseClicked(e -> {
        h.failure(new Error("end menu"));
      });
      HBox hBox = new HBox();
      hBox.getChildren().add(closeButton);
      hBox.setAlignment(Pos.CENTER_LEFT);
      vBox.getChildren().add(hBox);

      Text A = new Text("Click on card to buy");
      Text B = new Text("Your money is :");
      A.setWrappingWidth(X);
      B.setWrappingWidth(X);
      A.setTextAlignment(TextAlignment.CENTER);
      B.setTextAlignment(TextAlignment.CENTER);
      A.setFill(Color.WHITESMOKE);
      B.setFill(Color.WHITESMOKE);
      A.setFont(Font.font(Y / 50));
      B.setFont(Font.font(Y / 50));

      GridPane gridPane = new GridPane();

      gridPane.setHgap(X / 25);
      gridPane.setVgap(Y / 25);
      gridPane.setAlignment(Pos.CENTER);

      final int col = 8;

      List<BeingDna> allDna = new ArrayList<>();
      allDna.addAll(PlantDna.getAllDnas());
      allDna.addAll(ZombieDna.getAllDnas());
      int cnt = 0;
      for (BeingDna x : allDna) {
        ShopCard shopCard;
        if (Account.getCurrentAccount().getStore().haveCard(x))
          shopCard = new ShopCard(x, X / 18, 0, this);
        else
          shopCard = new ShopCard(x, X / 18, 1, this);

        cards.add(shopCard);
        gridPane.add(shopCard, cnt % col, cnt / col);
        cnt++;
      }

      update();

      vBox.getChildren().addAll(A, B, moneyTxt, gridPane);

      //vBox.setBackground(background);

      Rectangle rec = new Rectangle();
      rec.setHeight(Y);
      rec.setWidth(col * (X / 18) + (col + 1) * (X / 25));
      rec.setX((X - rec.getWidth()) / 2);
      rec.setFill(Color.rgb(0, 0, 0, 0.9));

      root = new StackPane();

      root.setBackground(background);
      root.getChildren().addAll(rec, vBox);

      vBox.setSpacing(Y / 50);


      Program.stage.getScene().setRoot(root);

    }));
  }
}