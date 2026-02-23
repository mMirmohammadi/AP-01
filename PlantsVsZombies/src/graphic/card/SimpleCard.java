package graphic.card;

import creature.Dna;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.Program;

public class SimpleCard extends Group {

  protected Rectangle rectangle, progress, rec;
  protected ImageView imageView;
  protected double size;
  protected Text description;
  protected Text tName;

  public void setColor(Color c) {
    progress.setHeight(0);
    rectangle.setFill(c);
  }

  public void setColor(Color cUp, Color cDown, double percent) {
    progress.setHeight(size * 1.4 * percent);
    rectangle.setFill(cDown);
    progress.setFill(cUp);
  }

  public SimpleCard(Dna dna, double x, double y, double size, String des) {
    super();
    this.size = size;
    this.setTranslateX(x);
    this.setTranslateY(y);
    rectangle = new Rectangle();
    rectangle.setWidth(size);
    rectangle.setHeight(size * 1.4);
    progress = new Rectangle();
    progress.setWidth(size);
    progress.setHeight(0);
    this.getChildren().add(rectangle);
    this.getChildren().add(progress);
    Text tName = new Text();
    tName.setText(dna.getName());
    tName.setWrappingWidth(size);
    tName.setTextAlignment(TextAlignment.CENTER);
    tName.setX(0);
    tName.setY(size / 8);
    tName.setFont(Font.font(size / 8));
    this.tName = tName;
    this.getChildren().add(tName);
    try {
      double imageMargin = size * 0.03;
      imageView = new ImageView(
              new Image(
                      Program.getRes(dna.getCardImageUrl())
              )
      );
      imageView.setFitHeight(size-2*imageMargin);
      imageView.setFitWidth(size-2*imageMargin);
      imageView.setY(size / 8 + size / 10 + imageMargin);
      imageView.setX(imageMargin);
      this.getChildren().add(imageView);
    } catch (Throwable e) {
      System.out.println(e.getMessage());
      System.out.println(dna.getCardImageUrl());
    }
    description = new Text(des);
    description.setFont(Font.font("monospaced", FontWeight.BOLD, size * 0.2));
    description.setWrappingWidth(size);
    description.setTextAlignment(TextAlignment.CENTER);
    description.setY(rectangle.getHeight() - size * 0.01);
    this.getChildren().addAll(description);
    setDescrition(des);
  }

  public void setDescrition(String newDescription) {
    description.setText(newDescription);
  }

  public void setDescription(String des, Paint paint) {
    setDescrition(des);
    description.setFill(paint);
  }
}