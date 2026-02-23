package creature.being.plant;

import java.util.SortedSet;

import creature.Creature;
import creature.Location;
import creature.being.zombie.Zombie;
import exception.EndGameException;

public class Plant extends Creature {
    private final PlantDna plantDna;
    private int remainingFirstCooldown;
    private int remainingAmmunitionCooldown;
    public Plant plantOnMe;
    //remainingCooldown unuse but good :)

    public void reduceHealth(int damageAmount) {
        if (plantOnMe != null) {
            if (plantOnMe.health <= damageAmount) {
                damageAmount -= plantOnMe.health;
                plantOnMe = null;
            }
            else {
                plantOnMe.reduceHealth(damageAmount);
                return;
            }
        }
        health -= damageAmount;
        if (health <= 0) gameEngine.killPlant(this);
    }

    public void createAmmunition() {
        if (plantDna.getAmmunitionDna().isEmpty()) return;
        plantDna.getAmmunitionDna().forEach(ammunitionDna -> {
            int Number = ammunitionDna.getProductionNumberOfAmmunitionPerUse();
            if (Number == 3) {
                for (int dx = -1; dx <= 1; dx++) {
                    try {
                        gameEngine.newAmmunition(this.location.moveBy(dx, 0), ammunitionDna, this);
                    }
                    catch(Exception exception) {
                        
                    }
                }
            }
            else if (Number == 9) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        try {
                            gameEngine.newAmmunition(
                                this.location.moveBy(dx, dy), ammunitionDna, this);
                        }
                        catch(Exception exception) {
                            
                        }
                    }
                }
            }
            else {
                SortedSet <Zombie> zombies = gameEngine.getZombies(this.location.lineNumber);
                int nearest = 100000;
                for (Zombie zombie: zombies) {
                    if (zombie.getLocation().position > this.location.position) {
                        nearest = Math.min(nearest, zombie.getLocation().position);
                    }
                }
                if (nearest - this.location.position >= ammunitionDna.getMinimumDistanceForShoot()) {
                    for (int num = 0; num < Number; ++num) {
                        gameEngine.newAmmunition(this.location, ammunitionDna, this);
                    }
                }
            }
        });
        if (plantDna.isExplosive()) gameEngine.killPlant(this);
    }

    public void damage(Zombie zombie) {
        if (plantDna.isExplosive()) {
            createAmmunition();
            return;
        }
        if (zombie.reduceHealth(plantDna.getPowerOfDestruction())) {
            gameEngine.killZombie(zombie);
        }
    }

    @Override
    public void nextTurn() throws EndGameException {
        if (plantOnMe != null) plantOnMe.nextTurn();
        super.nextTurn();
        if (remainingAmmunitionCooldown == 0) {
            if (!plantDna.getAmmunitionDna().isEmpty()) {
                remainingAmmunitionCooldown = plantDna.getAmmunitionDna().get(0).getCooldown();
                createAmmunition();
            }
        }
        else remainingAmmunitionCooldown--;
    }

    public boolean createPlantOnMe(PlantDna plantDna) {
        if (this.plantOnMe != null) return false;
        if (this.plantDna.getContain() != plantDna.getLineState()) return false;
        this.plantOnMe = new Plant(plantDna, this.location.lineNumber, this.location.position);
        return true;
    }

    public PlantDna getPlantDna() {
        return plantDna;
    }

    public int getRemainingFirstCooldown() {
        return remainingFirstCooldown;
    }

    public int getRemainingAmmunitionCooldown() {
        return remainingAmmunitionCooldown;
    }

    public void setRemainingAmmunitionCooldown(int remainingAmmunitionCooldown) {
        this.remainingAmmunitionCooldown = remainingAmmunitionCooldown;
    }

    public Plant(PlantDna plantDna, int lineNumber, int position) {
        super(new Location(lineNumber, position));
        this.plantDna = plantDna;
        if (!plantDna.getAmmunitionDna().isEmpty())
            this.remainingAmmunitionCooldown = plantDna.getAmmunitionDna().get(0).getCooldown();
        else
            this.remainingAmmunitionCooldown = 0;
        this.health = plantDna.getFirstHealth();
    }

    @Override
    public String toString() {
        return "Plant\ntype = " + plantDna.getName() + "\n" + super.toString() + "remainingAmmunitionCooldown=" + remainingAmmunitionCooldown + "\n\n";
    }

}
