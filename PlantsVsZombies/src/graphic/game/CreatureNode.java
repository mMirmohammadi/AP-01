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
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import main.Program;

public class CreatureNode extends Group {

  Creature creature;
  Dna dna;
  double size;
  private ImageView image;
  private Rectangle health1;
  private CreatureNode onMe;

  static double marginY = Program.screenY;

  public Dna getDna() {
    return dna;
  }

  public CreatureNode(Creature creature) {
    image = new ImageView();
    health1 = new Rectangle();
    size = Program.screenY / 8;
    health1.setWidth(size);
    health1.setHeight(size * 0.08);
    health1.setFill(Color.RED);
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
    if (!(creature instanceof Ammunition)) {
      this.getChildren().add(health1);
    }
    this.update();
  }

  public static CreatureNode from(String name, int line, int pos) {
    BeingDna dna = BeingDna.getByName(name);
    if (dna instanceof PlantDna) {
      return new CreatureNode(new Plant((PlantDna)dna, line, pos));
    }
    return new CreatureNode(new Zombie((ZombieDna)dna, line, pos));
  }

  public void update() {
    if ((creature instanceof Zombie) || (creature instanceof Ammunition)) {
      this.setTranslateX(GameEngine.getCurrentGameEngine().getGraphicalX(creature) + GameEngine.getFRAME());
      this.setTranslateY(GameEngine.getCurrentGameEngine().getGraphicalY(creature));
    } else {
      this.setTranslateX(GameEngine.getCurrentGameEngine().getGraphicalX(creature));
      this.setTranslateY(GameEngine.getCurrentGameEngine().getGraphicalY(creature));
    }
    if (!(creature instanceof Ammunition)) {
      health1.setWidth(size * creature.getHealth() / ((BeingDna) dna).getFirstHealth());
    }
    if (creature instanceof Plant) {
      Plant plant = (Plant)creature;
      double lilipadMargin = Program.screenY / 50;
      if (onMe == null) {
        if (plant.plantOnMe != null) {
          health1.setWidth(0);
          onMe = new CreatureNode(plant.plantOnMe);
          this.getChildren().add(onMe);
          onMe.setTranslateX(0);
          onMe.setTranslateY(-lilipadMargin);
          // this.setTranslateY(this.getTranslateY() +lilipadMargin);
        }
      }
      else {
        if (plant.plantOnMe == null) {
          this.getChildren().remove(onMe);
          onMe = null;
        }
        else if (plant.plantOnMe == onMe.creature) {
          health1.setWidth(0);
          onMe.update();
          onMe.setTranslateX(0);
          onMe.setTranslateY(-lilipadMargin);
          // this.setTranslateY(this.getTranslateY() +lilipadMargin);
        }
        else {
          System.exit(0);
        }
      }
    }
  }
}