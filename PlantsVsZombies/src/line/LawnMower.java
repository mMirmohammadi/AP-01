package line;

import creature.being.zombie.Zombie;
import game.GameEngine;

import java.util.SortedSet;

public class LawnMower {

    private Integer lineNumber;

    public LawnMower(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    void activate() {
        SortedSet<Zombie> zombies = GameEngine.getCurrentGameEngine().getZombies(lineNumber);
        for (Zombie zombie : zombies)
            GameEngine.getCurrentGameEngine().killZombie(zombie);
    }
}
