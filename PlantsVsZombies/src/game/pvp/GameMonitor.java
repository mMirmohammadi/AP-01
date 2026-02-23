package game.pvp;

import java.util.ArrayList;

import game.GameDna;
import game.GameEngine;
import game.GameMode;
import game.GameResult;
import line.LawnMower;
import line.Line;
import line.LineState;
import util.Effect;
import util.Unit;

public class GameMonitor {
  public static Effect<GameResult> run(PvpGame game) {
    try{
      return new Effect<>(h -> {
        ArrayList<Line> lines = new ArrayList<>();
        for (int i = 0; i < 5; i++)
          lines.add(new Line(i, LineState.DRY, null));
        new GameEngine();
        GameEngine.commonGraphic(
          GameMode.DAY,
          Effect.syncWork(() -> h.success(new GameResult())),
          (e) -> {
            h.success(
              new GameResult(
                GameMode.DAY, e.getWinner(),
                GameEngine.getCurrentGameEngine().plantsKilled(),
                GameEngine.getCurrentGameEngine().zombiesKilled()
              )
            );
          }
        );
        GameEngine
          .getCurrentGameEngine()
          .config(new GameDna(new ArrayPlant(game.plants), new ArrayZombie(game.zombies), lines));
      });
    }
    catch (Throwable e) {
      e.printStackTrace();
      System.exit(0);
      return null;
    }
  }
}