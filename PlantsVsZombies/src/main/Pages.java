package main;

import account.Account;
import account.ShopPage;
import chat.ChatPage;
import client.Client;
import creature.being.plant.PlantDna;
import creature.being.zombie.ZombieDna;
import game.GameEngine;
import game.GameResult;
import page.*;
import page.menu.ActionButton;
import page.menu.LinkButton;
import page.menu.Menu;
import page.menu.SimpleButton;
import util.Effect;
import util.Unit;

import javax.naming.NamingException;
import javax.security.sasl.AuthenticationException;

public class Pages {
  public static final Page<GameResult> chooseGameType = new Menu<GameResult>(
          new SimpleButton<>("Day",
                  new Collection<PlantDna>(Account::getCurrentUserPlants, 7).action()
                          .flatMap(GameEngine::newDayGame)),
          new SimpleButton<>("Water",
                  new Collection<PlantDna>(Account::getCurrentUserPlants, 7)
                          .action().flatMap(GameEngine::newWaterGame)),
          new SimpleButton<>("Rail", GameEngine.newRailGame()),
          new SimpleButton<>("Zombie",
                  new Collection<ZombieDna>(Account::getCurrentUserZombies, 7).action()
                          .flatMap(GameEngine::newZombieGame)));

  public static final Menu<Unit> mainMenu = new Menu<Unit>(
          new ActionButton<Unit>("play", chooseGameType.action().flatMap(
                  GameResult::action
          )),
          new LinkButton<>("online", new game.pvp.listMenu()),
          new LinkButton<Unit>("profile", Account.profilePage()),
          new LinkButton<Unit>("shop", new ShopPage()),
          new ActionButton<Unit>("chat", new Form("Enter oon yeki username:")
          .action().map(x->x[0])
          .flatMap(user -> new ChatPage(user).action()).discardData())
          );
  public static final Menu<Void> loginMenu = new Menu<Void>(new ActionButton<Void>("create account",
          (new AuthenticationForm("Enter new username", "Enter password"))
                  .action()
                  .flatMap(data -> Client.get("createAccount", data))
                  .map((x) -> x.equals("OK\n") ?
                        "Account created successfully"
                        : "Username used before").show().catchThen(e -> {
              if (e instanceof NamingException) {
                  return Message.show("username used before");
              }
              return Effect.noOp;
          })
  ), new ActionButton<Void>("login",
          (new AuthenticationForm("Enter username", "Enter password"))
                  .action()
                  .flatMap(data -> Account.login(data[0], data[1]))
                  .flatMap(x -> mainMenu.action())
                  .catchThen(e -> {
                    if (e instanceof AuthenticationException) {
                        return Message.show("username or password in not correct");
                    }
                    return Account.logout();
                  })
  ), new LinkButton<Void>("leaderboard", new Account.LeaderBoardPage()));

  public static <U> Page<U> notImplemented() {
    return new Page<U>() {
      @Override
      public Effect<U> action() {
        new Message("ishalla in future").action();
        return Effect.error("end");
      }
    };
  }
}