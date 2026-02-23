package game;

import line.Line;
import player.plant_player.PlantPlayer;
import player.zombie_player.ZombiePlayer;

import java.util.List;

public class GameDna {

    final PlantPlayer plantPlayer;
    final ZombiePlayer zombiePlayer;
    final List<Line> lines;

    public GameDna(PlantPlayer plantPlayer, ZombiePlayer zombiePlayer, List<Line> lines) {
        this.plantPlayer = plantPlayer;
        this.zombiePlayer = zombiePlayer;
        this.lines = lines;
    }
}
