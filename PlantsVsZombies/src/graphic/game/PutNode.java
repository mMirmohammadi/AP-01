package graphic.game;

import creature.Creature;
import creature.Dna;
import creature.ammunition.Ammunition;
import creature.being.BeingDna;
import creature.being.plant.Plant;
import creature.being.plant.PlantDna;
import creature.being.zombie.Zombie;
import creature.being.zombie.ZombieDna;
import game.GameEngine;
import game.pvp.PvpGame;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import main.Program;

public class PutNode extends Group {

  Creature creature;
  Dna dna;
  double size;
  private ImageView image;

  static double marginY = Program.screenY;

  public Dna getDna() {
    return dna;
  }

  public PutNode(Creature creature) {
    image = new ImageView();
    size = Program.screenY / 8;
    if (creature instanceof Plant) {
      dna = ((Plant) creature).getPlantDna();
      if (dna.getName().equals("Lily Pad"))
        size *= 1.3;
    }
    if (creature instanceof Zombie) {
      dna = ((Zombie) creature).getZombieDna();
      size *= 1.1;
    }
    if (creature instanceof Ammunition) {
      dna = ((Ammunition) creature).getAmmunitionDna();
      size /= 2.6;
    }
    try {
      image.setImage(new Image(Program.getRes(dna.getGameImageUrl())));
    } catch (Throwable e) {
      image.setImage(new Image(Program.getRes("images/lanat.png")));
    }
    this.creature = creature;
    image.setFitHeight(size);
    image.setFitWidth(size);
    this.getChildren().add(image);
    this.setTranslateY(creature.getLocation().lineNumber*Program.screenY/PvpGame.size1);
    this.setTranslateX(creature.getLocation().position*Program.screenX/PvpGame.size2);
  }

  public static PutNode from(String name, int line, int pos) {
    BeingDna dna = BeingDna.getByName(name);
    if (dna instanceof PlantDna) {
      return new PutNode(new Plant((PlantDna)dna, line, pos));
    }
    return new PutNode(new Zombie((ZombieDna)dna, line, pos));
  }
}