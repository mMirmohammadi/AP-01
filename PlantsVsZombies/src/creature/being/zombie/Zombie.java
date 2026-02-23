package creature.being.zombie;

import creature.Creature;
import creature.Location;
import creature.ammunition.Ammunition;
import creature.being.plant.Plant;
import exception.EndGameException;
import exception.Winner;
import game.GameEngine;

import java.util.List;

public class Zombie extends Creature {
    private final ZombieDna zombieDna;
    private final Zombie whenIDie;
    private int numberOfJump;
    private int remainingStunTurnNumber;
    private int speed;

    public void reduceSpeed(int reduceSpeedRatio, int stunTurnNumber) {
        if (stunTurnNumber == 0) return;
        remainingStunTurnNumber = stunTurnNumber;
        speed = Math.min(speed, zombieDna.getSpeed() / reduceSpeedRatio);
    }

    public boolean reduceHealth(int damageAmount) {
        health -= damageAmount;
        return health <= 0;
    }

    public boolean reduceHealth(int damageAmount, int ammunitionType) {
        if (zombieDna.getCrossing().indexOf(ammunitionType) != -1) {
            if (whenIDie == null) return false;
            return whenIDie.reduceHealth(damageAmount, ammunitionType);
        }
        int currentHealth = health;
        if (reduceHealth(damageAmount)) {
            if (whenIDie == null) return true;
            whenIDie.location = location;
            gameEngine.addZombie(whenIDie);
            gameEngine.killZombie(this);
            if (!whenIDie.reduceHealth(damageAmount - currentHealth, ammunitionType)) return false;
            gameEngine.killZombie(whenIDie);
            return true;
        }
        return false;
    }

    public void damage(Plant plant) {
        plant.reduceHealth(zombieDna.getPowerOfDestruction());
        plant.damage(this);
    }
    
    private Plant move() throws EndGameException {
        // tof daram mizanam
        if (GameEngine.getCurrentGameEngine().getTurn() % 2 == 0) return null;
        for (int i = 0; i < speed; i++) {
            List<Ammunition> ammunitions = gameEngine.getAmmunition(location);
            for (Ammunition ammunition: ammunitions) {
                if (!gameEngine.alive(ammunition)) continue;
                ammunition.effect(this);
                if (!gameEngine.alive(this)) return null;
            }
            Plant plant = gameEngine.getPlant(location);
            if (plant != null) {
                if (numberOfJump < zombieDna.getMaxNumberOfJumps()) {
                    numberOfJump++;
                }
                else {
                    return plant;
                }
            }
            try {
                location = location.left();
            }
            catch(Exception exp) {
                try {
                    gameEngine.activateLawnMower(location.lineNumber);
                    return null;
                } catch (Exception ignored) {
                    throw new EndGameException(Winner.ZOMBIES);
                }
            }
        }
        return gameEngine.getPlant(location);
    }

    @Override
    public void nextTurn() throws EndGameException {
        super.nextTurn();
        Plant plant = move();
        if (!gameEngine.alive(this)) return;
        if (plant != null) damage(plant);
        if (speed != zombieDna.getSpeed()) {
            remainingStunTurnNumber--;
            if (remainingStunTurnNumber == 0) speed = zombieDna.getSpeed();
        }
    }

    @Override
    public Location getLocation() {
        return location;
    }

    
    @Override
    public String toString() {
        return "Zombie\ntype = " + zombieDna.getName() + "\n" + super.toString() + "\n\n";
    }

    public ZombieDna getZombieDna() {
        return zombieDna;
    }

    public int getNumberOfJumpingPlant() {
        return numberOfJump;
    }

    public Zombie(ZombieDna zombieDna, int lineNumber, int position) {
        super(new Location(lineNumber, position));
        this.zombieDna = zombieDna;
        if (zombieDna.getWhenIDie() != null)
            this.whenIDie = new Zombie (zombieDna.getWhenIDie(), lineNumber, position);
        else
            this.whenIDie = null;
        this.speed = zombieDna.getSpeed();
        this.health = zombieDna.getFirstHealth();
    }
}
