package account;

import creature.being.plant.PlantDna;
import creature.being.zombie.ZombieDna;
import graphic.CloseButton;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.Program;
import page.ErrorMessage;
import page.Form;
import page.Message;
import page.Page;
import page.menu.ActionButton;
import page.menu.Button;
import page.menu.Menu;
import util.Effect;
import util.Serial;
import util.Unit;

import javax.naming.NamingException;
import javax.security.sasl.AuthenticationException;

import client.Client;

import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Account implements Serializable {
  private static final long serialVersionUID = -5582985394951882515L;
  private static final String BACKUP_ADDRESS = Program.getBackupPath("account.ser");
  static Account current;
  private static Map<String, Account> ALL = new HashMap<>();
  private static Map<String, Account> BY_TOKEN = new HashMap<>();
  //private String passwordSalt;
  public int score;
  Store store;
  private String username;
  private String passwordHash;
  private String token = null;
  public static String myToken = null;

  private Account(String username, String password) {
    this.username = username;
    this.passwordHash = password;
    this.store = new Store(this);
    ALL.put(username, this);
  }

  private Account() {}

  public static Effect<Account> getAccountFromServer(String username) {
    return Client.get("getUser", username+"\n").map(x -> Account.fromBase64(x));
  }

  private static Account fromBase64(String x) {
    return (Account)Serial.fromBase64(x);
  }

  public static Account getCurrentAccount() {
    return current;
  }

  public static void backupAll() {
    try {
      FileOutputStream fileOut =
              new FileOutputStream(BACKUP_ADDRESS);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(ALL);
      out.close();
      fileOut.close();
    } catch (IOException i) {
      ErrorMessage.from(i).action();
    }
  }

  public static Page<Unit> profilePage() {
    return new Menu<Unit>(
            new Button<Unit>() {
              @Override
              public String getLabel() {
                return "Delete account";
              }

              @Override
              public String getHelp() {
                return "This button will delete your account";
              }

              @Override
              public Effect<Unit> action() {
                return Client.get("account/delete", myToken+"\n").then(
                Message.show("your account deleted successfully"));
              }

            },
            new ActionButton<>("change",
                    (new Form("Enter username", "Enter password"))
                            .action()
                            .flatMap(data -> Account.login(data[0], data[1]))
                            .map(x -> "Account changed successfully")
                            .show().catchThen(e->
                              Message.show("ghalate ramz ya username")
                            )
            ),
            new ActionButton<>("rename",
                    (new Form("Enter new username"))
                            .action()
                            .flatMap(data -> 
                              Client.get("account/rename", myToken, data[0])
                            )
                            .map(x -> "Your username changed successfully")
                            .show().catchThen(e->
                              Message.show("tekrarie username")
                            )
            ),
            new ActionButton<>("create",
                    (new Form("Enter new username", "Enter password"))
                            .action()
                            .flatMap(data -> 
                              Account.create(data[0], data[1])
                            )
                            .map((x) -> "Account created successfully")
                            .show()
                            .showError()
            ),
            new ActionButton<>("Show", Effect.ok()
                    .map(x -> "Username: " + Account.current.username).show())
    );
  }

  @SuppressWarnings("unchecked")
  public static void recoverAll() {
    try {
      FileInputStream fileIn = new FileInputStream(BACKUP_ADDRESS);
      ObjectInputStream in = new ObjectInputStream(fileIn);
      ALL = (HashMap<String, Account>) in.readObject();
      in.close();
      fileIn.close();
    } catch (Exception i) {
      ErrorMessage.from(i).action();
    }
  }

  public static Account getByUsername(String username) {
    return ALL.get(username);
  }

  public static Effect<Unit> create(String username, String password) {
    return Effect.syncWork(() -> {
      if (getByUsername(username) != null)
          throw new NamingException("Username used before");
      new Account(username, password);
    });
  }

  public static boolean createSync(String username, String password) {
    if (ALL.containsKey(username)) return false;
    new Account(username, password);
    return true;
  }

  public static Effect<Unit> login(String username, String password) {
    return Client.get("login", username+"\n"+password+"\n").flatMap(r->{
      String[] rs = r.split("\n");
      if (rs[0].equals("OK")) {
        Account.myToken = rs[1];
        return Effect.ok();
      }
      throw new AuthenticationException();
    }).then(getAccountFromServer(username)
      .flatMap(account -> Effect.syncWork(()->{
      current = account;
    })))
    .then(Program.reloadAll());
  }

  public static ArrayList<PlantDna> getCurrentUserPlants() {
    return Account.current.getPlants();
  }

  public static ArrayList<ZombieDna> getCurrentUserZombies() {
    return Account.current.getZombies();
  }

  public Store getStore() {
    return store;
  }

  public ArrayList<PlantDna> getPlants() {
    ArrayList<PlantDna> res = new ArrayList<>();
    for (PlantDna dna : PlantDna.getAllDnas())
      if (store.haveCard(dna))
        res.add(dna);
    return res;
  }

  public ArrayList<ZombieDna> getZombies() {
    ArrayList<ZombieDna> res = new ArrayList<>();
    for (ZombieDna dna : ZombieDna.getAllDnas())
      if (store.haveCard(dna))
        res.add(dna);
    return res;
  }

  public Effect<Unit> rename(String string) {
    if (getByUsername(string) != null) return Effect.error("invalid username");
    ALL.remove(username);
    username = string;
    ALL.put(username, this);
    return Effect.ok();
  }

  public void delete() {
    ALL.remove(username);
  }

  private boolean matchPassword(String password) {
    return this.passwordHash.equals(password);
  }

  public static class LeaderBoardPage implements Page<Void> {
    String format = "    %-15s %5s %7s %5s %7s";
    String header = String.format(
            format, "Name", "|", "Score", "|", "Money"
    );
    String between = "----------------------------------------------------------";

    Text txt(String s) {
      Text txt = new Text(s);
      txt.setFill(Color.WHITESMOKE);
      txt.setFont(Font.font("monospaced", Program.screenX / 60));
      return txt;
    }

    @Override
    public Effect<Void> action() {
      return new Effect<>(h -> {
        Image image = new Image(Program.getRes("images/wallpaper.jpg"));
        Background background = new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false)));

        VBox pane = new VBox();
        pane.setAlignment(Pos.TOP_CENTER);

        CloseButton closeButton = new CloseButton();
        closeButton.setOnMouseClicked(e -> {
          h.failure(new Error("end menu"));
        });
        HBox hBox = new HBox();
        hBox.getChildren().add(closeButton);
        hBox.setAlignment(Pos.CENTER_LEFT);
        pane.getChildren().add(hBox);


        // pane.getChildren().addAll(txt(""));
        pane.getChildren().add(txt(header));
        pane.getChildren().add(txt(between));
        ALL.values().stream().sorted((y, x) -> {
          if (x.score != y.score) return x.score - y.score;
          if (x.store.money != y.store.money) return x.store.money - y.store.money;
          return 0;
        }).forEachOrdered(x -> pane.getChildren().add(txt(String.format(
                format, x.username, "|", "" + x.score, "|", "" + x.store.money
        ))));
//        ScrollPane scrollPane = new ScrollPane();
//        pane.setBackground(background);
//        scrollPane.setContent(pane);
//        scrollPane.setFitToWidth(true);
//        scrollPane.setFitToHeight(true);
//        scrollPane.setBackground(background);


        final double X = Program.screenX;
        final double Y = Program.screenY;
        Rectangle rec = new Rectangle();
        rec.setHeight(Y);
        double len = X * 2 / 3;
        rec.setWidth(len);
        rec.setX(X - len / 2);
        rec.setFill(Color.rgb(0, 0, 0, 0.9));

        StackPane stackPane = new StackPane();

        stackPane.setBackground(background);
        stackPane.getChildren().addAll(rec, pane);

        Program.stage.getScene().setRoot(stackPane);
      });
    }

  }

  @Override
  public String toString() {
    return "Account [passwordHash=" + passwordHash + ", score=" + score + ", store=" + store + ", username=" + username
        + "]";
  }

  public String getUsername() {
    return username;
  }

  public static String athenticate(String username, String password) {
    if (!ALL.containsKey(username)) return null;
    Account user = ALL.get(username);
    if (user.matchPassword(password)) {
      return user.generateToken();
    }
    return null;
  }
  
  private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static SecureRandom rnd = new SecureRandom();
  
  private String randomString( int len ){
    StringBuilder sb = new StringBuilder( len );
    for( int i = 0; i < len; i++ ) 
       sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
    return sb.toString();
  }
  
  private String generateToken() {
    if (this.token != null) {
      BY_TOKEN.remove(this.token);
    }
    this.token = randomString(100);
    BY_TOKEN.put(this.token, this);
    return this.token;
  }

  public String toBase64() {
    return Serial.toBase64(this);
  }

  public static Collection<Account> all() {
    return ALL.values();
  }

  public static Account getByToken(String token) {
    return BY_TOKEN.get(token);
  }

  public static Effect<Unit> reloadCurrent() {
    if (current == null) return Effect.noOp;
    return getAccountFromServer(current.username)
      .flatMap(account -> Effect.syncWork(()->{
        current = account;
      }));
  }

  public String getToken() {
    return token;
  }

  public static Effect<Unit> logout() {
    return Client.get("account/logout", myToken+"\n").discardData();
  }

  public void removeToken() {
    BY_TOKEN.remove(token);
    token = null;
  }
}