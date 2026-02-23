package server;

import java.util.ArrayList;

import account.Account;
import creature.being.BeingDna;
import creature.being.plant.PlantDna;
import creature.being.zombie.ZombieDna;

public class ShopHandler {
  public static String handle(String url, ArrayList<String> body) {
    if (url.equals("getDna")){
      return PlantDna.getAllBase64()+"\n"+ZombieDna.getAllBase64();
    }
    if (url.equals("buy")){
      Account user = Account.getByToken(body.get(0));
      if (user == null) return "FAIL401";
      BeingDna dna = BeingDna.getByName(body.get(1));
      if (dna == null) return "FAIL404";
      try {
        user.getStore().buyCard(dna);
        return "OK";
      }
      catch (Throwable e) {
        return "FAIL: "+e;
      }
    }
    if (url.equals("sell")){
      Account user = Account.getByToken(body.get(0));
      if (user == null) return "FAIL401";
      BeingDna dna = BeingDna.getByName(body.get(1));
      if (dna == null) return "FAIL404";
      try {
        user.getStore().sellCard(dna);
        return "OK";
      }
      catch (Throwable e) {
        return "FAIL: "+e;
      }
    }
    if (url.equals("transfer")){
      Account user = Account.getByToken(body.get(0));
      if (user == null) return "FAIL401";
      BeingDna dna = BeingDna.getByName(body.get(1));
      if (dna == null) return "FAIL404";
      Account user2 = Account.getByUsername(body.get(2));
      if (user2 == null) return "FAIL401";
      try {
        user.getStore().transferCard(dna, user2);
        return "OK";
      }
      catch (Throwable e) {
        return "FAIL: "+e;
      }
    }
    return "404";
  }
}