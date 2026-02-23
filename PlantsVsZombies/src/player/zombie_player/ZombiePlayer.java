package player.zombie_player;

import creature.being.zombie.ZombieDna;
import exception.EndGameException;

public interface ZombiePlayer {
    void nextTurn() throws EndGameException;
    void showHand();
    void zombie(ZombieDna dna, int x, int y);
}
