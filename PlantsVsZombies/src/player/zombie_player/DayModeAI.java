package player.zombie_player;

import creature.being.zombie.ZombieDna;
import exception.EndGameException;
import exception.Winner;
import game.GameEngine;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import main.Program;

import java.util.List;
import java.util.Random;

enum AttackState {
    ATTACKING,
    WAITING
}

public class DayModeAI implements ZombiePlayer {
    private GameEngine gameEngine;
    private Random random;
    private Integer waveNumber = 1;
    private Integer counter = 0;
    private AttackState attackState = AttackState.WAITING;
    private Group group;
    private Rectangle progress;
    private double margin;

    public DayModeAI() {
        gameEngine = GameEngine.getCurrentGameEngine();
        random = GameEngine.getCurrentGameEngine().getRandom();
        this.group = gameEngine.getPlayerGroup();
        Rectangle rec = new Rectangle();
        rec.setHeight(Program.screenY / 50);
        rec.setWidth(Program.screenX * 75 / 100);
        margin = Program.screenY / 200;
        progress = new Rectangle();
        progress.setHeight(rec.getHeight() - 2 * margin);
        progress.setWidth(0);
        rec.setX((Program.screenX - rec.getWidth()) / 2);
        rec.setY(Program.screenY - rec.getHeight());
        progress.setY(Program.screenY - rec.getHeight() + margin);
        progress.setX((Program.screenX - rec.getWidth() + margin) / 2);
        rec.setFill(Color.BROWN);
        progress.setFill(Color.GREEN);
        rec.setOpacity(0.8);
        group.getChildren().addAll(rec, progress);
    }

    private void attack() {
        List<ZombieDna> allDnas = ZombieDna.getAllDnas();
        int send = 1 + random.nextInt(2 * waveNumber);
        for (int i = 0; i < send; i++) {
            int pos = random.nextInt(gameEngine.getWidth());
            while (true) {
                try {
                    gameEngine.newZombie(allDnas.get(random.nextInt(allDnas.size())), pos);
                    break;
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void nextTurn() throws EndGameException {
        if (attackState == AttackState.ATTACKING) {
            if (counter % GameEngine.getFRAME() == 0)
                attack();
            counter++;
            if (counter == GameEngine.getFRAME() * 3) {
                attackState = AttackState.WAITING;
                counter = 0;
            }
        } else {
            counter++;
            if (!gameEngine.getZombies().isEmpty())
                counter = 0;
            if (waveNumber == 1) {
                if (counter == GameEngine.getFRAME() * 3) {
                    attackState = AttackState.ATTACKING;
                    waveNumber++;
                    counter = 0;
                }
            } else if (waveNumber < 4) {
                if (counter == GameEngine.getFRAME() * 7) {
                    attackState = AttackState.ATTACKING;
                    waveNumber++;
                    counter = 0;
                }
            } else if (waveNumber == 4 && counter > 0)
                throw new EndGameException(Winner.PLANTS);
        }
        updateProgress();
    }

    @Override
    public void showHand() {
        throw new UnsupportedOperationException("zombie ai can't show hand =)!");
    }

    @Override
    public void zombie(ZombieDna dna, int x, int y) {
        // TODO Auto-generated method stub

    }

    private void updateProgress() {
        progress.setWidth((Program.screenX * 75 / 100 - 2 * margin) * (waveNumber - 1) / 3);
    }
}
