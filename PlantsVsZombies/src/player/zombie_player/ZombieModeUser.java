package player.zombie_player;

import creature.being.plant.Plant;
import creature.being.zombie.ZombieDna;
import exception.EndGameException;
import exception.InvalidGameMoveException;
import exception.Winner;
import game.GameEngine;
import graphic.card.ZombieGameCard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import main.Program;
import page.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZombieModeUser implements ZombiePlayer {

    private GameEngine gameEngine;
    private Integer coin = 50;
    private Random rnd;
    private List <ZombieDna> zombieDnas;
    private Group group;
    private Label sunText;
    private List<ZombieGameCard> gameCards = new ArrayList<>();

    public ZombieModeUser(List<ZombieDna> zombieDnas) {
        gameEngine = GameEngine.getCurrentGameEngine();
        rnd = gameEngine.getRandom();
        this.zombieDnas = zombieDnas;
        this.group = gameEngine.getPlayerGroup();
        coin = 100 * GameEngine.getFRAME();
        Rectangle rectangle = new Rectangle();
        rectangle.setX(Program.screenX*0.02);
        rectangle.setY(Program.screenX*0.01);
        rectangle.setWidth(Program.screenX*0.06);
        rectangle.setHeight(Program.screenX*0.06);
        rectangle.setFill(Color.rgb(106, 71, 27));
        group.getChildren().add(rectangle);
        sunText = new Label("0");
        sunText.setTranslateX(Program.screenX*0.025);
        sunText.setTranslateY(Program.screenX*0.04);
        sunText.setPrefWidth(Program.screenX*0.05);
        sunText.setFont(Font.font(Program.screenX*0.02));
        sunText.setAlignment(Pos.CENTER);
        sunText.setBackground(new Background(
            new BackgroundFill(Color.WHEAT, CornerRadii.EMPTY, Insets.EMPTY)
        ));
        group.getChildren().add(sunText);
        int i = 0;
        for (ZombieDna dna : zombieDnas) {
            ZombieGameCard x = new ZombieGameCard(
                this,
                dna,
                (i + 1.5) * Program.screenX * 0.07,
                10,
                    Program.screenX * 0.055
            );
            group.getChildren()
                    .add(x);
            i++;
            gameCards.add(x);
        }
    }

    @Override
    public void nextTurn() throws EndGameException {
        for (Plant plant : gameEngine.getDeadPlantsLastTurn())
            coin += plant.getPlantDna().getFirstHealth() * 10;
        boolean flag = false;
        for (ZombieDna zombieDna : zombieDnas) {
            if (zombieDna.getFirstHealth() * 10 <= coin)
                flag = true;
        }
        if (!flag && gameEngine.getZombies().isEmpty()) throw new EndGameException(Winner.PLANTS);
        sunText.setText((coin/GameEngine.getFRAME())+"");
        for (int i=0;i<gameCards.size(); i++) {
            if (zombieDnas.get(i).getGamePrice() <= coin) {
                gameCards.get(i).setColor(Color.WHITE);
                gameCards.get(i).enabled = true;
            }
            else {
                gameCards.get(i).setColor(
                    Color.RED
                );
                gameCards.get(i).enabled = false;
            }
        }
    }

    private void put() {
        /*Effect<String[]> result = new Form("Enter name", "Enter X").action();
        if (result == null || result.getValue().length == 0) {
            Message.show("Enter the name :/!");
            return;
        }
        for (ZombieDna zombieDna : zombieDnas)
            if (zombieDna.getName().equals(result.getValue()[0])) {
                if (zombieDna.getFirstHealth() * 10 > coin) {
                    Message.show("Cant select this one, not enough coin!");
                    return;
                }
                try {
                    gameEngine.putZombie(zombieDna, Integer.valueOf(result.getValue()[1]));
                    coin -= zombieDna.getFirstHealth() * 10;
                } catch (InvalidGameMoveException e) {
                    Message.show(e.getMessage());
                } catch (NumberFormatException e) {
                    Message.show("invalid input format :D!");
                }
                return;
            }
        Message.show("invalid name !");*/
    }

    private void start() {
        gameEngine.startZombieQueue();
    }

    private void showLanes() {
        StringBuilder stringBuilder = new StringBuilder();
        List<ArrayList<ZombieDna>> zombieQueue = gameEngine.getZombieQueue();
        for (int i = 0; i < gameEngine.getWidth(); i++) {
            stringBuilder.append("line number ").append(i).append(":\n");
            for (ZombieDna dna : zombieQueue.get(i))
                stringBuilder.append(dna.toString()).append("\n");
            stringBuilder.append("------------------------------------------------------------------------------");
        }
        Message.show(stringBuilder.toString());
    }

    private void showLawn() {
        StringBuilder stringBuilder = new StringBuilder();
        gameEngine.getZombies().forEach(zombie -> stringBuilder.append(zombie.toString()).append('\n'));
        gameEngine.getPlants().forEach(plant -> stringBuilder.append(plant.toString()).append('\n'));
        Message.show(stringBuilder.toString());
    }

    public void showHand() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Coin: ").append(coin).append("\n");
        for (ZombieDna zombieDna : zombieDnas) {
            stringBuilder.append(zombieDna.toString()).append('\n');
        }
        Message.show(stringBuilder.toString());
    }

    @Override
    public void zombie(ZombieDna dna, int x, int y) {
        try {
            if (dna.getFirstHealth() * 10 > coin)
                throw new InvalidGameMoveException("not enough coin");
            gameEngine.newZombie(dna, x);
            coin -= dna.getFirstHealth() * 10;
        } catch (InvalidGameMoveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}

