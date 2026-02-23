package creature.ammunition;

import creature.Creature;
import creature.Location;
import creature.being.plant.Plant;
import creature.being.zombie.Zombie;
import exception.EndGameException;

import java.util.SortedSet;

public class Ammunition extends Creature {
    private final AmmunitionDna ammunitionDna;
    private final Plant owner;
    private int timeHealth;

    public AmmunitionDna getAmmunitionDna() {
        return ammunitionDna;
    }

    private void effectAll() {
        SortedSet<Zombie> zombies = gameEngine.getZombies(location.lineNumber);
        zombies.forEach(zombie -> {
            if (!gameEngine.alive(this)) return;
            if (!gameEngine.alive(zombie)) return;
            int dis = Math.max(
                    Math.abs(location.lineNumber - zombie.getLocation().lineNumber),
                    Math.abs(location.position - zombie.getLocation().position)
            );
            if (zombie.getLocation().position == location.position ||
                    owner.getPlantDna().isExplosive() && dis <= ammunitionDna.getEffectiveRange()) {
                effect(zombie);
            }
        });
    }

    public void effect(Zombie zombie) {
        if (zombie.getZombieDna().getCrossing().indexOf(this.ammunitionDna.getType()) != -1) return;

        if (zombie.reduceHealth(ammunitionDna.getPowerOfDestruction(), ammunitionDna.getType())) {
            gameEngine.killZombie(zombie);
        }
        zombie.reduceSpeed(ammunitionDna.getReduceSpeedRatio(), ammunitionDna.getStunTurnNumber());
        health--;
        if (health <= 0) {
            gameEngine.killAmmunition(this);
        }
    }

    private void move() {
        int direction = 1;
        if (ammunitionDna.getSpeed() < 0) direction = -1;
        for (int i = 0; i != ammunitionDna.getSpeed(); i += direction) {
            effectAll();
            if (!gameEngine.alive(this)) return;
            try {
                location = location.nextLocation(direction);
            } catch (Exception exp) {
                gameEngine.killAmmunition(this);
                return;
            }
        }
        effectAll();
        return;
    }

    @Override
    public void nextTurn() throws EndGameException {
        super.nextTurn();
        if (ammunitionDna.getType() == 0) {
            gameEngine.addSun(ammunitionDna.getSunIncome());
            gameEngine.killAmmunition(this);
            return;
        }
        if (ammunitionDna.getSpeed() == 0) effectAll();
        else move();
        if (!gameEngine.alive(this)) return;
        timeHealth--;
        if (timeHealth == 0) gameEngine.killAmmunition(this);
    }

    public Ammunition(Location location, AmmunitionDna ammunitionDna, Plant owner) {
        super(location);
        this.ammunitionDna = ammunitionDna;
        this.owner = owner;
        this.health = ammunitionDna.getFirstHealth();
        this.timeHealth = ammunitionDna.getFirstTimeHealth();
        if (this.timeHealth == 0) this.timeHealth += 100000;
    }

}