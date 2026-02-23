package server;

import java.util.ArrayList;

import account.Account;
import creature.being.BeingDna;
import creature.being.plant.PlantDna;
import creature.being.zombie.ZombieDna;

public class AccountHandler {
  public static String handle(String url, ArrayList<String> body) {
    if (url.equals("logout")) {
      Account user = Account.getByToken(body.get(0));
      if (user == null) return "FAIL401";
      user.removeToken();
      return "OK";
    }
    if (url.equals("delete")) {
      Account user = Account.getByToken(body.get(0));
      if (user == null) return "FAIL401";
      user.delete();
      return "OK";
    }
    if (url.equals("rename")) {
      Account user = Account.getByToken(body.get(0));
      if (user == null) return "FAIL401";
      try{
        user.rename(body.get(1));
      }
      catch(Throwable e) {
        return "FAIL";
      }
    }
    return "404";
  }
}