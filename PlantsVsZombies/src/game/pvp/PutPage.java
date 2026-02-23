package game.pvp;

import java.util.ArrayList;

import account.Account;
import client.Client;
import creature.Dna;
import creature.being.BeingDna;
import game.GameMode;
import graphic.GameBackground;
import graphic.SimpleButton;
import graphic.card.SimpleGameCard;
import graphic.game.PutNode;
import javafx.scene.layout.Pane;
import main.Program;
import page.Page;
import util.Effect;
import util.Unit;

public class PutPage implements Page<Unit> {

  private GameMode gameMode;
  private PvpGame game;
  private PutNode[][] nodes = new PutNode[PvpGame.size1][PvpGame.size2];
  private Pane pane;
  private String[][] objs;
  private boolean myTurn = false;

  @Override
  public Effect<Unit> action() {
    return new Effect<>(h->{
      pane = new Pane();
      GameBackground background = new GameBackground(
          gameMode,
          Effect.noOp
      );
      pane.getChildren().add(background);
      update(game);
      myTurn = game.zombie.equals(Account.getCurrentAccount().getUsername()) && gameMode == GameMode.ZOMBIE || 
               game.plant .equals(Account.getCurrentAccount().getUsername()) && gameMode == GameMode.DAY;
      if (myTurn) {
        SimpleButton ready = new SimpleButton(
          Program.screenX*0.6, 0, Program.screenX*0.2,
          Program.screenX*0.04, "ready",
          Client.get("pvp/ready", Account.myToken, game.id+"").discardData());
        pane.getChildren().add(ready);
        ArrayList<String> dnas = gameMode == GameMode.DAY ? game.plantHand : game.zombieHand;
        int i = 0;
        for (String dnaName: dnas) {
          BeingDna dna = BeingDna.getByName(dnaName);
          SimpleGameCard c = new SimpleGameCard(
            (d, x, y) -> {
              put(d, x, y).execute();
            },
            dna,
            (i + 1.5) * Program.screenX * 0.07,
            10,
            Program.screenX * 0.055
          );
          pane.getChildren().add(c);
          i++;
        }
      }
      Program.stage.getScene().setRoot(pane);
    });
  }

  public Effect<Unit> put(Dna dna, int i, int j) {
    return Client.get("pvp/put", Account.myToken, game.id+"",
      i+"", j+"", dna.getName()).discardData();
  }

  public void update(PvpGame newGame) {
    game = newGame;
    objs = gameMode == GameMode.DAY ? game.plants : game.zombies;
    //System.out.println(game);
    try {
      for (int i = 0; i < PvpGame.size1; i++) {
        for (int j = 0; j < PvpGame.size2; j++) {
          if (objs[i][j] == null) remove(i,j);
          else if (nodes[i][j] == null) add(i,j);
          else if (!objs[i][j].equals(nodes[i][j].getDna().getName())) { 
            remove(i,j);
            add(i,j);
          }
        }
      }
    }
    catch(Throwable e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  private void add(int i, int j) {
    System.out.println("add "+objs[i][j]);
    if (objs[i][j] == null) return;
    nodes[i][j] = PutNode.from(objs[i][j], i, j);
    pane.getChildren().add(nodes[i][j]);
  }

  private void remove(int i, int j) {
    System.out.println("remove "+i+" "+j);
    if (nodes[i][j] == null) return;
    pane.getChildren().remove(nodes[i][j]);
  }

  public PutPage(GameMode gameMode, PvpGame game) {
    this.gameMode = gameMode;
    this.game = game;
  }

}