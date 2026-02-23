package chat;

import account.Account;
import client.Client;
import graphic.CloseButton;
import graphic.SimpleButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import main.Program;
import page.Page;
import util.Effect;
import util.Unit;

public class ChatPage implements Page<Void> {

    private VBox vBox;
    private ScrollPane sPane;
    private final String username;

    Image image = new Image(Program.getRes("images/wallpaper.jpg"));
    Background background = new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false)));

    final double X = Program.screenX;
    final double Y = Program.screenY;

    Color normal = Color.WHITESMOKE;
    Color choosed = Color.GREEN;
    Color information = Color.RED;

    int reply = -1;
    Text choosen = null;


    private void recolor(boolean b) {
        if (choosen == null) return;
        if (b) {
            choosen.setFill(Color.AQUA);
        } else {
            choosen.setFill(Color.YELLOW);
        }
    }


    public Effect<Unit> reload() {
        return Client.get("getChat", Account.myToken + "\n" + username + "\n")
                .map(data -> data.split("\n"))
                .flatMap(data -> Effect.syncWork(() -> {
                    vBox = new VBox();
                    for (int i = 0; i < data.length / 3; i++) {
                        TextFlow textFlow = new TextFlow();
                        String username = data[3 * i];
                        int rep = Integer.parseInt(data[3 * i + 1]);
                        String matn = data[3 * i + 2];

                        {
                            Text txt = new Text(i + " -> ");
                            txt.setFill(Color.YELLOW);
                            txt.setFont(Font.font(Y / 30));
                            textFlow.getChildren().add(txt);
                            if (reply == i) {
                                choosen = txt;
                                recolor(true);
                            }

                            int finalI = i;
                            txt.setOnMouseClicked(e -> {
                                if (finalI == reply) {
                                    reply = -1;
                                    recolor(false);
                                    choosen = null;
                                    return;
                                }
                                recolor(false);
                                choosen = txt;
                                reply = finalI;
                                recolor(true);
                            });

                        }

                        if (rep != -1) {
                            Text txt = new Text("reply to message number " + rep + " ");
                            txt.setFill(Color.RED);
                            txt.setFont(Font.font(Y / 30));
                            textFlow.getChildren().add(txt);
                        }

                        {
                            Text txt = new Text(username + ":\n");
                            txt.setFill(Color.GREEN);
                            txt.setFont(Font.font(Y / 30));
                            textFlow.getChildren().add(txt);
                        }

                        {
                            String[] res = matn.split("(?<=\\G.{40})");
                            for (String s : res) {
                                Text txt = new Text(s + "\n");
                                txt.setFill(Color.WHITESMOKE);
                                txt.setFont(Font.font(Y / 30));
                                textFlow.getChildren().add(txt);
                            }
                        }


                        vBox.getChildren().add(textFlow);
                    }
                    System.out.println(data.length);
                    sPane.setContent(vBox);
                }));
    }

    @Override
    public Effect<Void> action() {
        return new Effect<Void>(h -> {
            Pane pane = new Pane();
            pane.setBackground(background);
            CloseButton closeButton = new CloseButton();
            closeButton.setOnMouseClicked(e -> {
                h.failure(new Error("closed"));
            });
            sPane = new ScrollPane();
            sPane.setStyle(
                    "-fx-background: rgba(0 , 0 , 0 , 0.7); -fx-background-color: transparent; -fx-padding: 0; -fx-background-insets: 0;");
            sPane.setTranslateX(Program.screenX * 0.1);
            sPane.setPrefWidth(Program.screenX * 0.8);
            sPane.setPrefHeight(Program.screenY * 0.9);
            reload().execute();
            Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
                reload().execute();
            }));
            fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
            fiveSecondsWonder.play();
            pane.getChildren().add(closeButton);
            pane.getChildren().add(sPane);
            TextField textField = new TextField();
            textField.setTranslateY(Program.screenY * 0.9);
            textField.setPrefWidth(Program.screenX * 0.9);
            textField.setPrefHeight(Program.screenY * 0.1);
            textField.setFont(Font.font(Program.screenY * 0.05));
            SimpleButton simpleButton = new SimpleButton(
                    Program.screenX * 0.9,
                    Program.screenY * 0.9,
                    Program.screenX * 0.1,
                    Program.screenY * 0.1,
                    "send",
                    (Effect.fromSync(() -> {
                        String s = textField.getText();
                        textField.setText("");
                        return s;
                    })).flatMap(data -> Client.get(
                            "sendMessage",
                            Account.myToken + "\n" + username + "\n" + reply + "\n" + data + "\n")
                            .then(reload()))
            );
            pane.getChildren().add(textField);
            pane.getChildren().add(simpleButton);
            Program.stage.getScene().setRoot(pane);
        });
    }

    public ChatPage(String username) {
        this.username = username;
    }

}