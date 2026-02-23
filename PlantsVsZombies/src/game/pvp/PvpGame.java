package game.pvp;

import account.Account;
import account.AccountForm;
import client.Client;
import creature.being.plant.Plant;
import creature.being.plant.PlantDna;
import creature.being.zombie.ZombieDna;
import exception.EndGameException;
import game.GameDna;
import game.GameEngine;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import line.LawnMower;
import line.Line;
import line.LineState;
import main.Program;
import page.Collection;
import util.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class PvpGame implements Serializable {
    
    public static final long serialVersionUID = -1568760709015661149L;
    public static final Integer randomizer = 3865;
    public static final ArrayList<PvpGame> allIds = new ArrayList<>();

    public final Integer id;
    public final String plant;
    public final String zombie;
    public ArrayList<String> plantHand = new ArrayList<>();
    public ArrayList<String> zombieHand = new ArrayList<>();
    public static int size1 = 5, size2 = 9;

    String[][] plants = new String[size1][size2];
    String[][] zombies = new String[size1][size2];

    public GameState gameState;

    public PvpGame(String plant, String zombie) {
        this.plant = plant;
        this.zombie = zombie;
        gameState = GameState.WAITING_TO_JOIN;

        id = allIds.size() ^ randomizer;
        allIds.add(this);
        System.out.println(allIds.size());
    }

    public static HBox packet(String id, String plant, String zombie, String gameState) {

        String format = "%-4s %5s %-13s %5s %-13s    ";

        Text text = new Text();
        Button button = new Button();
        HBox hBox = new HBox();
        hBox.setSpacing(Program.screenX / 50);
        hBox.setAlignment(Pos.CENTER);
        text.setFill(Color.WHITESMOKE);
        text.setFont(Font.font("monospaced", Program.screenX / 60));
        button.setTextAlignment(TextAlignment.CENTER);
        button.setStyle(
                "-fx-background-color: rgba(0 , 255 , 0 , 0.2);"
        );
        button.setTextFill(Color.WHITESMOKE);
        button.setFont(Font.font("monospaced", Program.screenX / 60));

        if (gameState == null) {
            text.setText(String.format(format, "ID", "", "plants", "", "zombies"));
            button.setText("status");
        } else {
            String username = Account.getCurrentAccount().getUsername();
            if (gameState.equals("WAITING_TO_JOIN")) {
                if (zombie.equals(username)) {
                    button.setText("join");
                    button.setOnMouseClicked(e->joinZombie(id).execute());
                } else {
                    button.setText("waiting");
                }
            } else if (gameState.equals("RUNNING_ZOMBIE") || gameState.equals("RUNNING_PLANTS") ||
                    gameState.equals("PLANTS_WIN") || gameState.equals("ZOMBIES_WIN")) {
                button.setText("watch");
                button.setOnMouseClicked(e->GameManager.init(id).execute());
            } else if (gameState.equals("CANCELED")) {
                button.setText("canceled");
            }
            text.setText(String.format(format, id, "", plant, "", zombie));
        }

        hBox.getChildren().addAll(text, button);
        return hBox;
    }

    private static Effect<Unit> joinZombie(String id) {
        return new Collection<ZombieDna>(Account::getCurrentUserZombies, 7)
            .action()
            .map(x -> Account.myToken+"\n"+id+
                "\n"+x.stream().map(y -> y.getName())
                .collect(Collectors.joining("\n"))+"\n"
            )
            .flatMap(x -> Client.get("pvp/join", x))
            .then(GameManager.init(id));
    }

    private static PvpGame findGameById(String id) {
        Integer gameId = Integer.parseInt(id);
        for (PvpGame game : allIds)
            if (gameId.equals(game.id)) {
                return game;
            }
        return null;
    }

    // body's first argument is username !!!
    public static String handle(String url, ArrayList<String> body) {
        if (url.equals("list")) {
            StringBuilder res = new StringBuilder();
            for (PvpGame game : allIds)
                res.append(game.toListString()).append('\n');
            return res.toString();
        }
        if (url.equals("stateOfGame")) {
            Integer gameId = Integer.parseInt(body.get(1));
            for (PvpGame game : allIds)
                if (gameId.equals(game.id)) {
                    return game.gameState.toString();
                }
            return "No game with such ID found";
        }
        if (url.equals("create")) {
            Account user = Account.getByToken(body.get(0));
            if (user == null) return "FAIL401";      
            if (Account.getByUsername(body.get(1)) == null)
                return "Not a valid username for zombie player";
            PvpGame game =  new PvpGame(user.getUsername(), body.get(1));
            for (int i = 2; i < body.size(); i++) {
                game.plantHand.add(body.get(i));
            }
            return game.id+"";
        }
        if (url.equals("join")) {
            Account user = Account.getByToken(body.get(0));
            if (user == null) return "FAIL401";     
            PvpGame game = findGameById(body.get(1));
            for (int i = 2; i < body.size(); i++) {
                game.zombieHand.add(body.get(i));
            }
            game.gameState = GameState.RUNNING_PLANTS;
            return "OK";
        }
        if (url.equals("cancel")) {
            Integer gameId = Integer.parseInt(body.get(1));
            String username = body.get(0);
            for (PvpGame game : allIds)
                if (gameId.equals(game.id)) {
                    if (game.plant.equals(username) || game.zombie.equals(username)) {
                        game.gameState = GameState.CANCELED;
                        return "OK";
                    }
                    return "Action not allowed";
                }
            return "No game with such ID found";
        }
        if (url.equals("get")) {
            PvpGame game = findGameById(body.get(0));
            return Serial.toBase64(game); 
        }
        if (url.equals("ready")) {
            Account user = Account.getByToken(body.get(0));
            if (user == null) return "FAIL401";
            PvpGame game = findGameById(body.get(1));
            if (game.plant.equals(user.getUsername()) && game.gameState == GameState.RUNNING_PLANTS) {
                game.gameState = GameState.RUNNING_ZOMBIE;
                return "OK";
            }
            if (game.zombie.equals(user.getUsername()) && game.gameState == GameState.RUNNING_ZOMBIE) {
                ArrayList<Line> lines = new ArrayList<>();
                for (int i = 0; i < 5; i++)
                    lines.add(new Line(i, LineState.DRY, null));
                new GameEngine();
                GameEngine
                .getCurrentGameEngine()
                .config(new GameDna(new ArrayPlant(game.plants), new ArrayZombie(game.zombies), lines));      
                while (true) {
                    try {
                        GameEngine.getCurrentGameEngine().nextTurn();
                        if (GameEngine.getCurrentGameEngine().getTurn() > 20)
                        for (int i = 0; i < size1; i++) {
                            for (int j = 0; j < size2; j++) {
                                if (GameEngine.getCurrentGameEngine().getPlant2(i, j) == null) {
                                    game.plants[i][j] = null;
                                }
                            }
                        }
                    }
                    catch (EndGameException e) {
                        break;
                    }
                }
                game.zombies = new String[size1][size2];
                game.gameState = GameState.RUNNING_PLANTS;
                return "OK";
            }
            return "FAIL";
        }
        if (url.equals("put")) {
            Account user = Account.getByToken(body.get(0));
            if (user == null) return "FAIL401";
            PvpGame game = findGameById(body.get(1));
            int i = Integer.parseInt(body.get(2));
            int j = Integer.parseInt(body.get(3));
            if (user.getUsername().equals(game.plant)) {
                game.plants[i][j] = body.get(4);
                return "OK";
            }
            if (user.getUsername().equals(game.zombie)) {
                game.zombies[i][j] = body.get(4);
                return "OK";
            }
            return "FAIL";
        }
        return "404";
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < plants.length; i++) {
            for (int j = 0; j < plants[i].length; j++) {
                res += plants[i][j] + " ";
            }
            res += "\n";
        }
        return id + "\t" + plant + "\t" + zombie + "\t" + gameState + "\n" + res;
    }

    public String toListString() {
        return id + "\t" + plant + "\t" + zombie + "\t" + gameState;    
    }
}
