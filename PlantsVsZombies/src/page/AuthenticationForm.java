package page;

import graphic.CloseButton;
import graphic.SimpleButton;
import graphic.Wallpaper;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.Program;
import util.Effect;

public class AuthenticationForm extends Form {
    public AuthenticationForm(String... asks) {
        super(asks);
    }

    @Override
    public Effect<String[]> action() {
        return new Effect<>(h -> {
            final double X = Program.screenX;
            final double Y = Program.screenY;
            Background background = Wallpaper.background();

            VBox vBox = new VBox();

            CloseButton closeButton = new CloseButton();
            closeButton.setOnMouseClicked(e -> {
                h.failure(new Error("form Aborted"));
            });
            HBox hBox = new HBox();
            hBox.getChildren().add(closeButton);
            hBox.setAlignment(Pos.CENTER_LEFT);
            vBox.getChildren().add(hBox);

            TextField username = new TextField();
            username.setMaxWidth(X / 3);
            username.setPrefHeight(Y / 20);
            // username.setPromptText("username");
            PasswordField password = new PasswordField();
            password.setMaxWidth(X / 3);
            password.setPrefHeight(Y / 20);
            //  password.setPromptText("password");

            Text A = new Text("username:");
            Text B = new Text("password:");
            A.setTextAlignment(TextAlignment.CENTER);
            B.setTextAlignment(TextAlignment.CENTER);
            A.setFont(Font.font(Y / 25));
            B.setFont(Font.font(Y / 25));

            vBox.getChildren().addAll(A, username, B, password);

            vBox.setAlignment(Pos.TOP_CENTER);
            vBox.setSpacing(Y / 20);

            vBox.getChildren().add(new SimpleButton(
                    0, 0, X / 10, Y / 20, "OK",
                    Effect.syncWork(() -> {
                        h.success(new String[]{username.getText(), password.getText()});
                    })
            ));

            vBox.setBackground(background);

            Pane pane = new Pane();
            pane.getChildren().add(vBox);
            vBox.setMinHeight(Y);
            vBox.setMaxHeight(Y);
            vBox.setMinWidth(X);
            vBox.setMaxWidth(X);
            Program.stage.getScene().setRoot(pane);

        });
    }
}
