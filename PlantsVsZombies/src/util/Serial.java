package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class Serial {
  public static String toBase64(Serializable x) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      new ObjectOutputStream(baos).writeObject(x);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new String(Base64.getEncoder().encode(baos.toByteArray()));
  }

  public static Object fromBase64(String x) {
    ByteArrayInputStream bais = new ByteArrayInputStream(
      Base64.getDecoder().decode(x)
    );
    try {
      return new ObjectInputStream(bais).readObject();
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }
}