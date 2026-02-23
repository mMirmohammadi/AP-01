package game.pvp;

import client.Client;
import game.GameMode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.Program;
import page.Message;
import page.Page;
import util.Effect;
import util.Serial;
import util.Unit;

public class GameManager {

  static String id;
  static PvpGame game, lastGame;
  static GameState state = null;
  static Timeline timer;

  static class WaitingPage implements Page<Unit> {

    @Override
    public Effect<Unit> action() {
      return new Effect<>(h-> {
        Label l = new Label("Wait for other player...");
        l.setStyle("-fx-font: 3cm arial");
        Program.stage.getScene().setRoot(new StackPane(l));
      });
    }

  }

  static PutPage putPage;

  public static Effect<Unit> reload(){
    return Client.get("pvp/get", id+"\n")
      .map(x -> (PvpGame)Serial.fromBase64(x))
      .flatMap(newGame -> Effect.syncWork(()->{
        lastGame = game;
        game = newGame;
      }))
      .then(Effect.syncWork(()->{
        if (!game.gameState.equals(state)) {
          if (game.gameState == GameState.RUNNING_PLANTS ) {
            if (state == GameState.RUNNING_ZOMBIE) {
              timer.pause();
              GameMonitor.run(lastGame).then(Effect.syncWork(()->{
                state = game.gameState;
                putPage = new PutPage(GameMode.DAY, game);
                putPage.action().execute();
                timer.play();
              })).execute();
              return;
            }
            putPage = new PutPage(GameMode.DAY, game);
            putPage.action().execute();
            return;
          }
          state = game.gameState;
          if (game.gameState == GameState.WAITING_TO_JOIN) {
            new WaitingPage().action().execute();
            return;
          }
          if (game.gameState == GameState.RUNNING_ZOMBIE ) {
            putPage = new PutPage(GameMode.ZOMBIE, game);
            putPage.action().execute();
            return;
          }
        }
        else {
          if (game.gameState == GameState.RUNNING_PLANTS ) {
            putPage.update(game);
          }
          if (game.gameState == GameState.RUNNING_ZOMBIE ) {
            putPage.update(game);
          }
        }
      }));
  }

  public static Effect<Unit> init(String myId) {
    id = myId;
    return reload().then(Effect.syncWork(()->{
      timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
        reload().execute();
      }));
      timer.setCycleCount(Timeline.INDEFINITE);
      timer.play();
    }));
  }
  
}