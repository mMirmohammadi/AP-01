package player.zombie_player;

import account.Account;
import creature.being.zombie.ZombieDna;
import game.GameEngine;

import java.util.List;
import java.util.Random;

public class RailModeAI implements ZombiePlayer {

    private GameEngine gameEngine;
    private Random random;

    public RailModeAI() {
        gameEngine = GameEngine.getCurrentGameEngine();
        random = GameEngine.getCurrentGameEngine().getRandom();
    }

    @Override
    public void nextTurn() {
        if (random.nextInt(4 * GameEngine.getFRAME()) == 0) {
            List<ZombieDna> allDnas = Account.getCurrentUserZombies();
            while (true) {
                try {
                    gameEngine.newZombie(allDnas.get(random.nextInt(allDnas.size())), random.nextInt(gameEngine.getWidth()));
                    break;
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void showHand() {
        throw new UnsupportedOperationException("zombie ai can't show hand =)!");
    }

    @Override
    public void zombie(ZombieDna dna, int x, int y) {
        // TODO Auto-generated method stub

    }
}
