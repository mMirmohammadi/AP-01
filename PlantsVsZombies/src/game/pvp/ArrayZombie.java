package game.pvp;

import creature.being.BeingDna;
import creature.being.zombie.ZombieDna;
import exception.EndGameException;
import exception.Winner;
import game.GameEngine;
import player.zombie_player.ZombiePlayer;

public class ArrayZombie implements ZombiePlayer {

  String[][] zombies;
  GameEngine gameEngine;

  public ArrayZombie(String[][] zombies) {
    this.zombies = zombies;
    gameEngine = GameEngine.getCurrentGameEngine();
  }

  @Override
  public void nextTurn() throws EndGameException {
    if (gameEngine.getTurn() % GameEngine.getFRAME() == 0) {
      int cnt = gameEngine.getTurn() / GameEngine.getFRAME();
      if (cnt >= PvpGame.size2) {
        if (gameEngine.getZombies().size() == 0) {
          throw new EndGameException(Winner.PLANTS);
        }
        return;
      }
      for (int i = 0; i < PvpGame.size1; i++) {
        try {
          ZombieDna dna = (ZombieDna)BeingDna.getByName(zombies[i][cnt]);
          gameEngine.newZombie(dna, i);
        }
        catch(Throwable ignored) {
        }
      }
    }
  }

  @Override
  public void showHand() {
    // TODO Auto-generated method stub

  }

  @Override
  public void zombie(ZombieDna dna, int x, int y) {
    // TODO Auto-generated method stub

  }

}