package creature.being;

import java.io.Serializable;

import creature.Dna;
import creature.being.plant.PlantDna;
import creature.being.zombie.ZombieDna;
import game.GameEngine;
import line.LineState;

public class BeingDna extends Dna implements Serializable {

  private static final long serialVersionUID = 3795559829997184761L;
  private final int shopPrice;
  private final int gamePrice;
  private final int firstHealth;
  private final LineState lineState;
  public int shopCount;

  public int getShopPrice() {
      return shopPrice * 100;
  }
  public int getGamePrice() {
    return gamePrice;
  }
  public int getFirstHealth() {
    return firstHealth * GameEngine.getFRAME();
  }

  public BeingDna(String name, int speed, boolean leftToRight, int powerOfDestruction, int shopPrice,
      int gamePrice, int firstHealth, LineState lineState) {
    super(name, speed, powerOfDestruction);
    this.shopPrice = shopPrice;
    this.gamePrice = gamePrice;
    this.firstHealth = firstHealth;
    this.lineState = lineState;
  }

  public LineState getLineState() {
    return lineState;
  }
  
  public static BeingDna getByName(String name) {
    for (PlantDna x: PlantDna.getAllDnas()) {
      if (x.getName().equals(name)) return x;
    }
    for (ZombieDna x: ZombieDna.getAllDnas()) {
      if (x.getName().equals(name)) return x;
    }
    return null;
  }


}