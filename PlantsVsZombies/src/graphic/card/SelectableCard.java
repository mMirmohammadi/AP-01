package graphic.card;

import creature.Dna;
import javafx.scene.paint.Color;
import util.Effect;
import util.Unit;

public class SelectableCard extends SimpleCard {

  private boolean selected = false;

  public SelectableCard(Dna dna, double x, double y, double size, Effect<Unit> onChange) {
    super(dna, x, y, size, "");
    recolor();
    this.setOnMouseEntered(e -> {
      rectangle.setFill(Color.CADETBLUE);
    });
    this.setOnMouseExited(e -> {
      recolor();
    });
    this.setOnMouseClicked(e -> {
      selected ^= true;
      onChange.execute();
    });
  }

  private void recolor() {
    if (selected) this.rectangle.setFill(Color.LIGHTGREEN);
    else rectangle.setFill(Color.AQUA);
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

}