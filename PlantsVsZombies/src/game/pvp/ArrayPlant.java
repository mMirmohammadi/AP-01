package game.pvp;

import creature.being.BeingDna;
import creature.being.plant.PlantDna;
import exception.EndGameException;
import game.GameEngine;
import player.plant_player.PlantPlayer;

public class ArrayPlant implements PlantPlayer {

  GameEngine gameEngine;
  String[][] plants;

  public ArrayPlant(String[][] plants) {
    gameEngine = GameEngine.getCurrentGameEngine();
    this.plants = plants;
  }

  @Override
  public void nextTurn() throws EndGameException {
    if (!gameEngine.getTurn().equals(0)) return;
    for (int i = 0; i < PvpGame.size1; i++) {
      for (int j = 0; j < PvpGame.size2; j++) {
        try {
          PlantDna dna = (PlantDna)BeingDna.getByName(plants[i][j]);
          gameEngine.newPlant2(dna, i, j);
        } catch (Exception ignored) {
        }
      }
    }
  }

  @Override
  public void addSun(int sunAmount) {
    // TODO Auto-generated method stub

  }

  @Override
  public void showHand() {
    // TODO Auto-generated method stub

  }

  @Override
  public void plant(PlantDna dna, int x, int y) {
    // TODO Auto-generated method stub

  }

}