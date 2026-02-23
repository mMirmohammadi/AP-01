package server;

public class Post {
  private final String caption, photo;

  public Post(String caption, String photo) {
    this.caption = caption;
    this.photo = photo;
  }

  public String getCaption() {
    return caption;
  }

  public String getPhoto() {
    return photo;
  }

  @Override
  public String toString() {
    return caption + "\n" + photo + "\n";
  }
}