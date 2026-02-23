package player.plant_player;

import creature.being.plant.Plant;
import creature.being.plant.PlantDna;
import exception.EndGameException;
import exception.InvalidGameMoveException;
import game.GameEngine;
import graphic.card.GameCard;
import graphic.game.Bil;
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
import page.Form;
import page.Message;
import util.Effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DayModeUser implements PlantPlayer {

    private GameEngine gameEngine;
    private Integer sun = 2;
    private Random rnd;
    private List<PlantDna> plantDans;
    private List<Integer> coolDownTimeLeft;
    private Integer selected;
    private Group group;
    private Label sunText;
    private ArrayList<GameCard> gameCards = new ArrayList<>();

    public DayModeUser(List<PlantDna> plantDans) {
        gameEngine = GameEngine.getCurrentGameEngine();
        rnd = gameEngine.getRandom();
        group = gameEngine.getPlayerGroup();
        this.plantDans = plantDans;
        coolDownTimeLeft = new ArrayList<>();
        int i = 0;
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
        for (PlantDna dna : plantDans) {
            coolDownTimeLeft.add(0);
            GameCard x = new GameCard(
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
        Bil bil = new Bil(
            (7 + 1.5) * Program.screenX * 0.07,   
            10,
            Program.screenX * 0.06
        );
        group.getChildren().add(bil);
    }

    private void randomSunAdder() {
        if (gameEngine.getTurn() % GameEngine.getFRAME() != 0) return;
        Integer added = rnd.nextInt(2) * (2 + rnd.nextInt(4));
        sun += added;
    }

    @Override
    public void nextTurn() throws EndGameException {
        selected = null;
        randomSunAdder();
        for (int i = 0; i < coolDownTimeLeft.size(); i++) {
            int k = coolDownTimeLeft.get(i) - 1;
            if (k < 0) k = 0;
            coolDownTimeLeft.set(i, k);
            if (k == 0 && plantDans.get(i).getGamePrice() <= sun) {
                gameCards.get(i).setColor(Color.WHITE);
                gameCards.get(i).enabled = true;
            }
            else {
                gameCards.get(i).setColor(
                    Color.RED,
                    Color.GREEN,
                    1-(k*1.0/plantDans.get(i).getCooldown())
                );
                gameCards.get(i).enabled = false;
            }
        }
        sunText.setText(sun+"");
    }

    private void selectNum(int i) {
        if (coolDownTimeLeft.get(i) > 0) {
            Message.show("Cant select this one, cool down time left !");
            return;
        }
        if (plantDans.get(i).getGamePrice() > sun) {
            Message.show("Cant select this one, not enough sun!");
            return;
        }
        selected = i;
    }

    private void select() {
        /*
         * Effect<String[]> result = new Form("Enter name").action(); if (result == null
         * || result.getValue().length == 0) { Message.show("Enter the name :/!");
         * return; } for (int i = 0; i < plantDans.size(); i++) if
         * (plantDans.get(i).getName().equals(result.getValue()[0])) { selectNum(i);
         * return; } Message.show("invalid name !");
         */
    }

    private void removePlant() {
        Effect<String[]> result = new Form("Enter X", "Enter Y").action();
        try {
            Integer x = 0;// Integer.valueOf(result.getValue()[0]);
            Integer y = 0;// Integer.valueOf(result.getValue()[1]);
            Plant plant = gameEngine.getPlant(x, y);
            if (plant == null)
                throw new InvalidGameMoveException("there is no plant in that location!");
            gameEngine.killPlant(plant);
        } catch (InvalidGameMoveException e) {
            Message.show(e.getMessage());
        } catch (NumberFormatException e) {
            Message.show("invalid input format :D!");
        }
    }

    private void createPlant() {
        Effect<String[]> result = new Form("Enter X", "Enter Y").action();
        try {
            if (selected == null)
                throw new InvalidGameMoveException("Select a card first! =)");
            Integer x = 0;// Integer.valueOf(result.getValue()[0]);
            Integer y = 0;// Integer.valueOf(result.getValue()[1]);
            PlantDna p = plantDans.get(selected);
            gameEngine.newPlant(p, x, y);
            sun -= p.getGamePrice();
            coolDownTimeLeft.set(selected, p.getCooldown());
            selected = null;
        } catch (InvalidGameMoveException e) {
            Message.show(e.getMessage());
        } catch (NumberFormatException e) {
            Message.show("invalid input format :D!");
        }
    }

    @Override
    public void addSun(int sunAmount) {
        sun += sunAmount;

    }

    @Override
    public void plant(PlantDna dna, int x, int y) {
        try {
            if (dna.getGamePrice() > sun)
                throw new InvalidGameMoveException("not enough sun");
            int i = plantDans.indexOf(dna);
            gameEngine.newPlant2(dna, x, y);
            coolDownTimeLeft.set(i, dna.getCooldown());
            sun -= dna.getGamePrice();
        } catch (InvalidGameMoveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void showHand() {
        // TODO Auto-generated method stub

    }
}
