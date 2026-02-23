package game;

import creature.ammunition.Ammunition;
import creature.being.plant.Plant;
import creature.being.zombie.Zombie;
import line.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class PlayGroundData {

    final Integer width;
    final Integer length;

    SortedSet<Plant> plants = new TreeSet<>();
    SortedSet<Zombie> zombies = new TreeSet<>();
    SortedSet<Ammunition> ammunition = new TreeSet<>();
    List<Line> lines;

    List<TreeSet<Plant>> plantsPerLine;
    List<TreeSet<Zombie>> zombiesPerLine;
    List<TreeSet<Ammunition>> ammunitionPerLine;

    SortedSet<Plant> deadPlants = new TreeSet<>();
    SortedSet<Zombie> deadZombies = new TreeSet<>();
    SortedSet<Plant> deadPlantsLastTurn = new TreeSet<>();
    SortedSet<Zombie> deadZombiesLastTurn = new TreeSet<>();

    Integer zombiesKilled = 0;
    Integer plantsKilled = 0;

    PlayGroundData(Integer length, List<Line> lines) {
        this.width = lines.size();
        this.length = length;
        this.lines = lines;
        plantsPerLine = new ArrayList<>();
        zombiesPerLine = new ArrayList<>();
        ammunitionPerLine = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            ammunitionPerLine.add(new TreeSet<>());
            plantsPerLine.add(new TreeSet<>());
            zombiesPerLine.add(new TreeSet<>());
        }
    }


}
