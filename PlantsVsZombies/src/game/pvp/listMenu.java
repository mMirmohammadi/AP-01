package game.pvp;

import client.Client;
import creature.being.plant.PlantDna;
import graphic.CloseButton;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import main.Program;
import page.Collection;
import page.Page;
import util.Effect;
import util.Unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import account.Account;
import account.AccountForm;

public class listMenu implements Page<Unit> {
    @Override
    public Effect<Unit> action() {
        return Client.get("pvp/list", "").map(x -> x.split("\n")).
                map(Arrays::asList).flatMap(list -> new Effect<>(h -> {
            Image image = new Image(Program.getRes("images/wallpaper.jpg"));
            Background background = new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false)));

            final double X = Program.screenX;
            final double Y = Program.screenY;

            VBox vBox = new VBox();
            vBox.setFillWidth(true);
            vBox.setAlignment(Pos.TOP_CENTER);

            CloseButton closeButton = new CloseButton();
            closeButton.setOnMouseClicked(e -> h.failure(new Error("end menu")));
            HBox hBox = new HBox();
            hBox.getChildren().add(closeButton);
            hBox.setAlignment(Pos.CENTER_LEFT);


            HBox newGameBox = new HBox();
            newGameBox.setAlignment(Pos.CENTER);
            Button newGameBtn = new Button("new game");
            newGameBtn.setTextAlignment(TextAlignment.CENTER);
            newGameBtn.setStyle(
                    "-fx-background-color: rgba(0 , 255 , 0 , 0.2);"
            );
            newGameBtn.setTextFill(Color.WHITESMOKE);
            newGameBtn.setFont(Font.font("monospaced", Program.screenX / 60));
            newGameBtn.setOnMouseClicked(e -> Program.reloadAll().then(newGame()).execute());
            newGameBox.getChildren().add(newGameBtn);
            vBox.getChildren().addAll(hBox, newGameBox);

            vBox.getChildren().add(PvpGame.packet(null, null, null, null));
            List<String> newlist = list;
            int mx = 12;
            if (list.size() > mx)
                newlist = list.subList(list.size() - mx, list.size());
            if (newlist.size() == 1 && newlist.get(0).length() == 0) newlist = new ArrayList<>();
            for (String game : newlist) {
                String[] res = game.split("\t");
                vBox.getChildren().add(PvpGame.packet(res[0], res[1], res[2], res[3]));
            }

            Rectangle rec = new Rectangle();
            rec.setHeight(Y);
            rec.setWidth(X * 0.7);
            rec.setX((X - rec.getWidth()) / 2);
            rec.setFill(Color.rgb(0, 0, 0, 0.9));

            StackPane stackPane = new StackPane();

            stackPane.setBackground(background);
            stackPane.getChildren().addAll(rec, vBox);

            vBox.setSpacing(Y / 50);

            Program.stage.getScene().setRoot(stackPane);

        }));
    }

    private Effect<Unit> newGame() {
        return new Collection<PlantDna>(Account::getCurrentUserPlants, 7)
            .action()
            .map(x -> x.stream().map(y -> y.getName())
                .collect(Collectors.joining("\n"))+"\n"
            )
            .flatMap(x -> new AccountForm().action()
                .map(y -> Account.myToken + "\n" + y + "\n" + x)
            )
            .flatMap(x -> Client.get("pvp/create", x))
            .flatMap(x -> GameManager.init(x))
            .discardData();
    }
}
