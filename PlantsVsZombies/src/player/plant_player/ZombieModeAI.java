package player.plant_player;

import account.Account;
import creature.being.plant.PlantDna;
import game.GameEngine;

import java.util.List;
import java.util.Random;

public class ZombieModeAI implements PlantPlayer {


    private GameEngine gameEngine;
    private Random random;

    public ZombieModeAI() {
        gameEngine = GameEngine.getCurrentGameEngine();
        random = GameEngine.getCurrentGameEngine().getRandom();
    }

    @Override
    public void nextTurn() {
        if (!gameEngine.getTurn().equals(0)) return;
        List<PlantDna> allDnas = Account.getCurrentUserPlants();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < gameEngine.getWidth(); j++) {
                while (true) {
                    try {
                        PlantDna dd = allDnas.get(random.nextInt(allDnas.size()));
                        if (dd.isExplosive()) continue;
                        gameEngine.newPlant2(dd, j, i);
                        break;
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    @Override
    public void showHand() {
        throw new UnsupportedOperationException("there is no hand =)!");
    }

    @Override
    public void addSun(int sunAmount) {

    }

    @Override
    public void plant(PlantDna dna, int x, int y) {
        // TODO Auto-generated method stub

    }
}
