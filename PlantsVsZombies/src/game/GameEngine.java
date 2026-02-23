package game;

import creature.Creature;
import creature.Location;
import creature.ammunition.Ammunition;
import creature.ammunition.AmmunitionDna;
import creature.being.plant.Plant;
import creature.being.plant.PlantDna;
import creature.being.zombie.Zombie;
import creature.being.zombie.ZombieDna;
import exception.EndGameException;
import exception.InvalidGameMoveException;
import graphic.GameBackground;
import graphic.game.CreatureNode;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import line.LawnMower;
import line.Line;
import line.LineState;
import main.Program;
import page.Message;
import player.plant_player.DayModeUser;
import player.plant_player.PlantPlayer;
import player.plant_player.RailModeUser;
import player.plant_player.ZombieModeAI;
import player.zombie_player.DayModeAI;
import player.zombie_player.RailModeAI;
import player.zombie_player.ZombieModeUser;
import player.zombie_player.ZombiePlayer;
import util.Effect;
import util.Unit;

import java.util.*;
import java.util.function.Consumer;

public class GameEngine {

    private PlantPlayer plantPlayer;
    private ZombiePlayer zombiePlayer;
    private static GameEngine currentGameEngine;
    private PlayGroundData DATABASE;
    private Integer turn = 0;
    private Random random;
    private Group group;
    private ArrayList<ArrayList<ZombieDna>> zombieQueue;

    private static Integer SEC_PER_TURN = 2;
    private static Integer FRAME = 60 * SEC_PER_TURN;
    private Group playerGroup;

    public static Integer getFRAME() {
        return FRAME;
    }

    public static Integer getSecPerTurn() {
        return SEC_PER_TURN;
    }

    public GameEngine() {
        currentGameEngine = this;
        random = new Random();
    }

    public static Effect<GameResult> newWaterGame(List<PlantDna> hand) {
        return new Effect<>(h-> {
            List<Line> lines = new ArrayList<>();
            for (int i = 0; i < 5; i++)
                if (i == 2)
                    lines.add(new Line(i, LineState.WATER, new LawnMower(i)));        
                else
                    lines.add(new Line(i, LineState.DRY, new LawnMower(i)));
            new GameEngine();
            commonGraphic(
                GameMode.WATER,
                Effect.syncWork(() -> h.success(new GameResult())),
                (e)->{
                h.success(new GameResult(
                    GameMode.WATER,
                    e.getWinner(),
                    getCurrentGameEngine().plantsKilled(),
                    getCurrentGameEngine().zombiesKilled()
                ));
            });
            getCurrentGameEngine().config(new GameDna(new DayModeUser(hand), new DayModeAI(), lines));
        });
    }

    public static void commonGraphic(GameMode gameMode, Effect<Unit> onAbort, Consumer<EndGameException> consumer){
        Pane pane = new Pane();
        Timer timer = new Timer();
        GameBackground background = new GameBackground(
            gameMode,
            Effect.syncWork(timer::cancel).then(onAbort)
        );
        pane.getChildren().add(background);
        Group group = new Group();
        getCurrentGameEngine().group = group;
        pane.getChildren().add(group);
        getCurrentGameEngine().playerGroup = new Group();
        pane.getChildren().add(getCurrentGameEngine().playerGroup);
        timer.schedule(
        new TimerTask(){
            @Override
            public void run() {
                Platform.runLater(()->{
                    try {
                        getCurrentGameEngine().nextTurn();
                    } catch (EndGameException e) {
                        timer.cancel();
                        consumer.accept(e);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                });
            }
        }, 0, SEC_PER_TURN * 1000 / FRAME);
        Program.stage.getScene().setRoot(pane);
    }

    public static Effect<GameResult> newDayGame(List<PlantDna> hand) {
        return new Effect<>(h-> {
            List<Line> lines = new ArrayList<>();
            for (int i = 0; i < 5; i++)
                lines.add(new Line(i, LineState.DRY, new LawnMower(i)));
            new GameEngine();
            commonGraphic(
                GameMode.DAY,
                Effect.syncWork(() -> h.success(new GameResult())),
                (e)->{
                h.success(new GameResult(
                    GameMode.DAY,
                    e.getWinner(),
                    getCurrentGameEngine().plantsKilled(),
                    getCurrentGameEngine().zombiesKilled()
                ));
            });
            getCurrentGameEngine().config(new GameDna(new DayModeUser(hand), new DayModeAI(), lines));
        });
    }

    public static Effect<GameResult> newRailGame() {
        return new Effect<>(h-> {
            List<Line> lines = new ArrayList<>();
            for (int i = 0; i < 5; i++)
                lines.add(new Line(i, LineState.DRY, new LawnMower(i)));
            new GameEngine();
            commonGraphic(
                GameMode.RAIL,
                Effect.syncWork(() -> h.success(new GameResult())),
                (e)->{
                h.success(new GameResult(
                    GameMode.RAIL,
                    e.getWinner(),
                    getCurrentGameEngine().plantsKilled(),
                    getCurrentGameEngine().zombiesKilled()
                ));
            });
            getCurrentGameEngine().config(new GameDna(new RailModeUser(), new RailModeAI(), lines));
        });
    }

    public static Effect<GameResult> newZombieGame(List<ZombieDna> hand) {
        return new Effect<>(h-> {
            List<Line> lines = new ArrayList<>();
            for (int i = 0; i < 5; i++)
                lines.add(new Line(i, LineState.DRY, new LawnMower(i)));
            new GameEngine();
            commonGraphic(
                GameMode.ZOMBIE,
                Effect.syncWork(() -> h.success(new GameResult())),
                (e)->{
                h.success(new GameResult(
                    GameMode.ZOMBIE,
                    e.getWinner(),
                    getCurrentGameEngine().plantsKilled(),
                    getCurrentGameEngine().zombiesKilled()
                ));
            });
            getCurrentGameEngine().config(new GameDna(new ZombieModeAI(), new ZombieModeUser(hand), lines));
        });
    }

    public static GameResult newPVPGame(List<PlantDna> plantHand, List<ZombieDna> zombieHand) {
        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < 6; i++)
            lines.add(new Line(i, LineState.DRY, new LawnMower(i)));
        new GameEngine();
        GameEngine.getCurrentGameEngine()
                .config(new GameDna(new DayModeUser(plantHand), new ZombieModeUser(zombieHand), lines));
        try {
            while (true) {
                getCurrentGameEngine().nextTurn();
            }
        } catch (EndGameException e) {
            return new GameResult(GameMode.PVP, e.getWinner(), getCurrentGameEngine().plantsKilled(),
                    getCurrentGameEngine().zombiesKilled());
        }
    }


    public Random getRandom() {
        return random;
    }

    public Integer getTurn() {
        return turn;
    }

    public void config(GameDna gameDna) {
        plantPlayer = gameDna.plantPlayer;
        zombiePlayer = gameDna.zombiePlayer;
        DATABASE = new PlayGroundData(19 * FRAME, gameDna.lines);
        zombieQueue = new ArrayList<>();
        for (int i = 0; i < DATABASE.width; i++)
            zombieQueue.add(new ArrayList<>());
    }

    private boolean lineNumberChecker(Integer lineNumber) {
        return lineNumber >= 0 && lineNumber < DATABASE.width;
    }

    private boolean positionChecker(Integer position) {
        return position >= 0 && position < DATABASE.length;
    }

    public Integer getWidth() {
        return DATABASE.width;
    }

    public Integer getLength() {
        return DATABASE.length;
    }

    public static GameEngine getCurrentGameEngine() {
        return currentGameEngine;
    }

    public boolean locationChecker(Integer lineNumber, Integer position) {
        return !lineNumberChecker(lineNumber) || !positionChecker(position);
    }

    public Plant getPlant(Integer lineNumber, Integer position) {
        if (locationChecker(lineNumber, position))
            return null;
        for (Plant plant : DATABASE.plantsPerLine.get(lineNumber))
            if (plant.getLocation().equals(new Location(lineNumber, position)))
                return plant;
        return null;
    }

    public Plant getPlant(Location location) {
        return getPlant(location.lineNumber, location.position);
    }

    public List<Zombie> getZombies(Integer lineNumber, Integer position) {
        if (locationChecker(lineNumber, position))
            return null;
        SortedSet<Zombie> zombies = getZombies(lineNumber);
        ArrayList<Zombie> answer = new ArrayList<>();
        for (Zombie zombie : zombies)
            if (zombie.getLocation().equals(new Location(lineNumber, position)))
                answer.add(zombie);
        return answer;
    }

    public List<Zombie> getZombies(Location location) {
        return getZombies(location.lineNumber, location.position);
    }

    public void newPlant(PlantDna dna, Integer lineNumber, Integer position) throws InvalidGameMoveException {
        if (locationChecker(lineNumber, position))
            throw new InvalidGameMoveException("can't plant out of lawn!");
        Plant parent = getPlant(lineNumber, position);
        if (parent != null) {
            if (!parent.createPlantOnMe(dna)) {
                throw new InvalidGameMoveException("can't plant on this!");
            }
            return;
        }
        if (!DATABASE.lines.get(lineNumber).getLineState().equals(dna.getLineState()))
            throw new InvalidGameMoveException("can't plant here!");
        Plant plant = new Plant(dna, lineNumber, position);
        DATABASE.plantsPerLine.get(lineNumber).add(plant);
        DATABASE.plants.add(plant);
        CreatureNode creatureNode = new CreatureNode(plant);
        group.getChildren().add(creatureNode);
        plant.creatureNode = creatureNode;
    }

    public void newPlant2(PlantDna dna, Integer lineNumber, Integer position) throws InvalidGameMoveException {
        newPlant(dna, lineNumber, position * getFRAME() * 2 + getFRAME());
    }

    // TODO handling first position of zombie considering 0-base
    public void newZombie(ZombieDna dna, Integer lineNumber) throws InvalidGameMoveException {
        if (!lineNumberChecker(lineNumber))
            throw new InvalidGameMoveException("can't insert zombie here");
        if (!DATABASE.lines.get(lineNumber).getLineState().equals(dna.getLineState())) {
            if (DATABASE.lines.get(lineNumber).getLineState().equals(LineState.WATER)) {
                // TODO inja check konim ordak dare
                throw new InvalidGameMoveException("ordak not supported :D");

            } else {
                throw new InvalidGameMoveException("can't insert zombie here");
            }
        }
        System.out.println(dna.getName());
        Zombie zombie = new Zombie(dna, lineNumber, getLength());
        DATABASE.zombiesPerLine.get(lineNumber).add(zombie);
        DATABASE.zombies.add(zombie);
        CreatureNode creatureNode = new CreatureNode(zombie);
        group.getChildren().add(creatureNode);
        zombie.creatureNode = creatureNode;
    }

    public void newAmmunition(Location location, AmmunitionDna ammunitionDna, Plant plant) {
        Ammunition ammunition = new Ammunition(location, ammunitionDna, plant);
        DATABASE.ammunitionPerLine.get(location.lineNumber).add(ammunition);
        DATABASE.ammunition.add(ammunition);
        CreatureNode creatureNode = new CreatureNode(ammunition);
        group.getChildren().add(creatureNode);
        ammunition.creatureNode = creatureNode;
    }

    public void addZombie(Zombie zombie) {
        DATABASE.zombiesPerLine.get(zombie.getLocation().lineNumber).add(zombie);
        DATABASE.zombies.add(zombie);
        CreatureNode creatureNode = new CreatureNode(zombie);
        group.getChildren().add(creatureNode);
        zombie.creatureNode = creatureNode;
    }

    public void killPlant(Plant plant) {
        DATABASE.plants.remove(plant);
        DATABASE.plantsPerLine.get(plant.getLocation().lineNumber).remove(plant);
        DATABASE.plantsKilled += 1;
        DATABASE.deadPlants.add(plant);
        group.getChildren().remove(plant.creatureNode);
    }

    public void killZombie(Zombie zombie) {
        DATABASE.zombies.remove(zombie);
        DATABASE.zombiesPerLine.get(zombie.getLocation().lineNumber).remove(zombie);
        DATABASE.zombiesKilled += 1;
        DATABASE.deadZombies.add(zombie);
        group.getChildren().remove(zombie.creatureNode);
    }

    public void killAmmunition(Ammunition ammunition) {
        DATABASE.ammunition.remove(ammunition);
        DATABASE.ammunitionPerLine.get(ammunition.getLocation().lineNumber).remove(ammunition);
        group.getChildren().remove(ammunition.creatureNode);
    }

    public SortedSet<Plant> getPlants() {
        return new TreeSet<>(DATABASE.plants);
    }

    public SortedSet<Plant> getPlants(Integer lineNumber) {
        if (lineNumberChecker(lineNumber))
            return new TreeSet<>(DATABASE.plantsPerLine.get(lineNumber));
        return null;
    }

    public SortedSet<Zombie> getZombies() {
        return new TreeSet<>(DATABASE.zombies);
    }

    public SortedSet<Zombie> getZombies(Integer lineNumber) {
        if (lineNumberChecker(lineNumber))
            return new TreeSet<>(DATABASE.zombiesPerLine.get(lineNumber));
        return null;
    }

    public Integer zombiesKilled() {
        return DATABASE.zombiesKilled;
    }

    public Integer plantsKilled() {
        return DATABASE.plantsKilled;
    }

    public SortedSet<Plant> getDeadPlnats() {
        return new TreeSet<>(DATABASE.deadPlants);
    }

    public SortedSet<Zombie> getDeadZombies() {
        return new TreeSet<>(DATABASE.deadZombies);
    }

    public SortedSet<Plant> getDeadPlantsLastTurn() {
        return new TreeSet<>(DATABASE.deadPlantsLastTurn);
    }

    public SortedSet<Zombie> getDeadZombiesLastTurn() {
        return new TreeSet<>(DATABASE.deadZombiesLastTurn);
    }

    public void addSun(int sunAmount) {
        plantPlayer.addSun(sunAmount);
    }

    public void putZombie(ZombieDna dna, Integer lineNumber) throws InvalidGameMoveException {
        if (!lineNumberChecker(lineNumber) || zombieQueue.get(lineNumber).size() >= 2
                || !DATABASE.lines.get(lineNumber).getLineState().equals(dna.getLineState()))
            throw new InvalidGameMoveException("can't insert zombie here");
        zombieQueue.get(lineNumber).add(dna);
    }

    public List<ArrayList<ZombieDna>> getZombieQueue() {
        return zombieQueue;
    }

    public void startZombieQueue() {
        // TODO: poshte ham naran !
        for (int i = 0; i < DATABASE.width; i++)
            for (ZombieDna dna : zombieQueue.get(i))
                try {
                    newZombie(dna, i);
                } catch (Exception ignored) {
                }

        zombieQueue = new ArrayList<>();
        for (int i = 0; i < DATABASE.width; i++)
            zombieQueue.add(new ArrayList<>());
    }

    public void nextTurn() throws EndGameException {
        DATABASE.deadPlantsLastTurn = DATABASE.deadPlants;
        DATABASE.deadZombiesLastTurn = DATABASE.deadZombies;
        DATABASE.deadPlants = new TreeSet<>();
        DATABASE.deadZombies = new TreeSet<>();
        plantPlayer.nextTurn();
        zombiePlayer.nextTurn();

        TreeSet<Plant> local2 = new TreeSet<>(DATABASE.plants);
        for (Plant plant : local2)
            if (DATABASE.plants.contains(plant))
                plant.nextTurn();

        TreeSet<Ammunition> local = new TreeSet<>(DATABASE.ammunition);
        for (Ammunition ammunition : local) {
            if (DATABASE.ammunition.contains(ammunition)) {
                ammunition.nextTurn();
            }
        }

        TreeSet<Zombie> local3 = new TreeSet<>(DATABASE.zombies);
        for (Zombie zombie : local3)
            if (DATABASE.zombies.contains(zombie))
                zombie.nextTurn();

        turn += 1;
    }

    public String pretty() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < getWidth(); i++) {
            if (DATABASE.lines.get(i).lawnMower()) {
                res.append(" X ");
            } else {
                res.append(" - ");
            }
            for (int j = 0; j < getLength(); j++) {
                Plant plant = getPlant(i, j);
                if (plant == null)
                    res.append(".");
                else {
                    res.append("" + plant.getPlantDna().getName().charAt(0));
                }
                int cnt = 0;
                for (Ammunition ammunition : DATABASE.ammunitionPerLine.get(i)) {
                    if (ammunition.getLocation().position == j)
                        cnt++;
                }
                if (cnt == 0)
                    res.append(".");
                else
                    res.append("" + cnt);
                cnt = 0;
                char nm = 'A';
                for (Zombie zombie : getZombies(i)) {
                    if (zombie.getLocation().position == j) {
                        cnt++;
                        nm = zombie.getZombieDna().getName().charAt(0);
                    }
                }
                if (cnt == 0)
                    res.append(".");
                else if (cnt == 1)
                    res.append("" + nm);
                else
                    res.append("" + cnt);
                res.append(" ");
            }
            res.append("\n");
        }
        return res.toString();
    }

    public void prettyPrint() {
        Message.show(pretty());
    }

    public List<Ammunition> getAmmunition(Integer lineNumber, Integer position) {
        if (locationChecker(lineNumber, position))
            return new ArrayList<>();
        SortedSet<Ammunition> ammunitions = DATABASE.ammunitionPerLine.get(lineNumber);
        ArrayList<Ammunition> answer = new ArrayList<>();
        for (Ammunition ammunition : ammunitions)
            if (ammunition.getLocation().equals(new Location(lineNumber, position)))
                answer.add(ammunition);
        return answer;
    }

    public List<Ammunition> getAmmunition(Location location) {
        return getAmmunition(location.lineNumber, location.position);
    }

    public void activateLawnMower(Integer lineNumber) throws InvalidGameMoveException {
        if (!lineNumberChecker(lineNumber))
            throw new InvalidGameMoveException("invalid line number for activating lawn mower!");
        DATABASE.lines.get(lineNumber).activateLawnMower();
    }

    public boolean alive(Plant plant) {
        return DATABASE.plants.contains(plant);
    }

    public boolean alive(Zombie zombie) {
        return DATABASE.zombies.contains(zombie);
    }

    public boolean alive(Ammunition ammunition) {
        return DATABASE.ammunition.contains(ammunition);
    }

    public SortedSet<Ammunition> getAmmunitions() {
        return new TreeSet<>(DATABASE.ammunition);
    }

	public double getGraphicalX(Creature plant) {
        return Program.screenX * (plant.getLocation().position * 2) / (DATABASE.length * 2 + 2 * FRAME);
	}

    public double getGraphicalY(Creature plant) {
        return Program.screenY * (plant.getLocation().lineNumber * 2 + 1.5) / (DATABASE.lines.size() * 2 + 2);
	}

	public Group getPlayerGroup() {
		return playerGroup;
	}

	public Plant getPlant2(int x, int y) {
		return getPlant(y, x * getFRAME() * 2 + getFRAME());
	}
}
