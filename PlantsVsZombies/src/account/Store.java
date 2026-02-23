package account;

import creature.Dna;
import creature.being.BeingDna;
import creature.being.plant.PlantDna;
import creature.being.zombie.ZombieDna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Store implements Serializable {
  private static final long serialVersionUID = 5608599943348289833L;
  // private Account owner;
  private Set<String> myCard = new HashSet<>();
    public int money = 10000;
  public void buyCard(BeingDna card) {
    if (myCard.contains(card.getName())) throw new Error("darish");
    if (money < card.getShopPrice()) throw new Error("pool nadari");
    myCard.add(card.getName());
    money -= card.getShopPrice();
    card.shopCount--;
  }

  public boolean haveCard(Dna card) {
    return myCard.contains(card.getName());
  }

  public Store(Account owner) {
    List<BeingDna> allDna = new ArrayList<>();
    allDna.addAll(PlantDna.getAllDnas());
    allDna.addAll(ZombieDna.getAllDnas());
    for (BeingDna x: allDna) {
      if (x.getShopPrice() == 0) myCard.add(x.getName());
    }
  }

  public void sellCard(BeingDna card) {
    if (!myCard.contains(card.getName())) throw new Error("nadarish");
    myCard.remove(card.getName());
    money += card.getShopPrice();
    card.shopCount++;
  }

  public void transferCard(BeingDna card, Account user2) {
    if (!myCard.contains(card.getName())) throw new Error("nadarish");
    if (user2.store.myCard.contains(card.getName())) throw new Error("oon dare");
    myCard.remove(card.getName());
    user2.store.myCard.add(card.getName());
  }
}