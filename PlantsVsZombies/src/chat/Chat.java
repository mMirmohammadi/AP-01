package chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import account.Account;
import util.Serial;

public class Chat {
  public static class Message implements Serializable {
    
    private static final long serialVersionUID = -8096962699448160363L;
    String sender;
    String text;
    String reply;


    public Message(String sender, String reply, String text) {
      this.sender = sender;
      this.text = text;
      this.reply = reply;
    }

    @Override
    public String toString() {
      return sender + "\n" + reply + "\n" + text + "\n";
    }

    public String toBase64() {
      return Serial.toBase64(this);
    }

    public static Message fromBase64(String value) {
      return (Message)Serial.fromBase64(value);
    }
  }
  
  ArrayList<Message> messages = new ArrayList<>();

  public String toString() {
    return messages.stream().map(x->x.toString()).collect(Collectors.joining());
  }

  public static Map<String, Chat> BY_MEMBER = new HashMap<>();

  public static Chat get(String username1, String username2) {
    if (username1.compareTo(username2) < 0) return get(username2, username1); 
    String s = username1+"#"+username2;
    if (BY_MEMBER.containsKey(s)) return BY_MEMBER.get(s);
    Chat chat = new Chat();
    BY_MEMBER.put(s, chat);
    return chat;
  }

  public static void sendMessage(String username1, String username2, String reply, String text) {
    Chat chat = Chat.get(username1, username2);
    chat.messages.add(new Message(username1, reply , text));
  }
}