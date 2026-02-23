package page;

public class ErrorMessage extends Message {

  public ErrorMessage(String message) {
    super("Error: "+message);
  }

  public static ErrorMessage from(Throwable e) {
    return new ErrorMessage(e.toString());
  }
}