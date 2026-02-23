package creature;

import java.io.Serializable;

public class Dna implements Serializable {

  private static final long serialVersionUID = -3742700220299156865L;
  private final String name;
  private final int speed;
  private final int powerOfDestruction;

  public Dna(String name, int speed, int powerOfDestruction) {
    this.name = name;
    this.speed = speed;
    this.powerOfDestruction = powerOfDestruction;
  }

  public String getName() {
    return name;
  }

  public String getCardImageUrl() {
    return "images/" + name + "/main";
  }

  public String getGameImageUrl() {
    return "images/" + name + "/main";
  }

  public int getSpeed() {
    return speed;
  }

  public int getPowerOfDestruction() {
    return powerOfDestruction;
  }

    @Override
    public String toString() {
        return "Dna{" +
                "name='" + name + '\'' +
                ", speed=" + speed +
                '}';
    }
}