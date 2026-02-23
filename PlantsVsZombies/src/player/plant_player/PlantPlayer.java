package player.plant_player;

import creature.Location;
import creature.being.plant.PlantDna;
import exception.EndGameException;

public interface PlantPlayer {
    void nextTurn() throws EndGameException;
    void addSun(int sunAmount);
    void showHand();
    void plant(PlantDna dna, int x, int y);
}
