package creature.being.plant;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import creature.ammunition.AmmunitionDna;
import creature.being.BeingDna;
import game.GameEngine;
import line.LineState;
import util.Serial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlantDna extends BeingDna implements Serializable {

  private static final long serialVersionUID = 3612493169475278488L;
  private static ArrayList<PlantDna> allDnas;
  private final ArrayList<AmmunitionDna> ammunitionDna;
  private final int cooldown;
  private final LineState contain;
  private final boolean Explosive;

  public static List<PlantDna> getAllDnas() {
    return allDnas;
  }

  public static void loadFromData(String json) {
    Gson gson = new Gson();
    allDnas = gson.fromJson(json, new TypeToken<List<PlantDna>>() {
    }.getType());
  }

  public List<AmmunitionDna> getAmmunitionDna() {
    return ammunitionDna;
  }

  public PlantDna(String name, int speed, boolean leftToRight, int powerOfDestruction, int shopPrice,
                  int gamePrice, int firstHealth, LineState lineState, ArrayList<AmmunitionDna> ammunitionDna, int cooldown,
                  LineState contain, boolean explosive) {
    super(name, speed, leftToRight, powerOfDestruction, shopPrice, gamePrice, firstHealth, lineState);
    this.ammunitionDna = ammunitionDna;
    this.cooldown = cooldown;
    this.contain = contain;
    Explosive = explosive;
  }

  public boolean isExplosive() {
    return Explosive;
  }

  @Override
  public String toString() {
    return "-------------------------------------------------------------------------------" + "\n" +
            "name: " + getName() + "\n" +
            "Health: " + getFirstHealth() + "\n" +
            "cool down: " + getCooldown() + "\n" +
            "price: " + getGamePrice();
  }

  public int getCooldown() {
    return cooldown * GameEngine.getFRAME();
  }

  public LineState getContain() {
    return contain;
  }

  public static String getAllBase64() {
    return Serial.toBase64(allDnas);
  }

  @SuppressWarnings("unchecked")
  public static void loadFromBase64(String string) {
    allDnas = (ArrayList<PlantDna>)Serial.fromBase64(string);
  }
  
}