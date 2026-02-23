package server;

import account.Account;
import chat.Chat;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import creature.being.plant.PlantDna;
import creature.being.zombie.ZombieDna;
import main.Program;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class App {

  private static int port = 8000;
  public static void main(String[] args) throws Exception {
    preload();
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    printStatus();
    server.createContext("/", (t) -> {
      Scanner scanner = new Scanner(t.getRequestBody());
      String response;
      try {
        response = handle(t.getRequestURI().getPath(), scanner);
      } 
      catch (Throwable e) {
        response = "ERROR:"+e;
        e.printStackTrace();
      }
      printStatus();
      scanner.close();
      t.sendResponseHeaders(200, response.length());
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
    });
    server.setExecutor(null); // creates a default executor
    server.start();
  }

  public static void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  private static void printStatus() {
    clearScreen();
    port++;
    System.out.println("Listening on port "+port);
    for (Account a: Account.all()) {
      if (a.getToken() == null)
        System.out.println(a.getUsername()+": offline");
      else
        System.out.println(a.getUsername()+": online");
    }
  }

  public static String getBackupPath(String file) {
    String workingDirectory;
    String OS = (System.getProperty("os.name")).toUpperCase();
    if (OS.contains("WIN")) {
      workingDirectory = System.getenv("AppData");
    } else {
      workingDirectory = System.getProperty("user.home");
      workingDirectory += "/Library/Application Support/PVZ4/";
    }
    return workingDirectory + file;
  }

  private static void preload() {
    try {
      PlantDna.loadFromData(Program.streamToString(Program.getRes("plantDna.json")));
      ZombieDna.loadFromData(Program.streamToString(Program.class.getResourceAsStream("resource/zombieDna.json")));
    } catch (Exception e) {
      e.printStackTrace();
    }
    Account.createSync("x", "x");
    Account.createSync("y", "y");
  }

  public static ArrayList<String> scannerToStringArray(Scanner sc) {
    ArrayList<String> result = new ArrayList<>();
    while (sc.hasNextLine()) {
      result.add(sc.nextLine());
    }
    return result;
  }

  static String handle(String url, Scanner body) {
    System.out.println("request: "+url);
    if (url.startsWith("/pvp")) {
      return game.pvp.PvpGame.handle(url.substring(5), scannerToStringArray(body));
    }
    if (url.startsWith("/shop")) {
      return ShopHandler.handle(url.substring(6), scannerToStringArray(body));
    }
    if (url.startsWith("/account")) {
      return AccountHandler.handle(url.substring(9), scannerToStringArray(body));
    }
    if (url.equals("/login")) {
      if (!body.hasNextLine()) return "BAD";
      String username = body.nextLine();
      if (!body.hasNextLine()) return "BAD";
      String password = body.nextLine();
      String token = Account.athenticate(username, password);
      if (token != null) {
        return "OK\n"+token+"\n";
      }
      else {
        return "FAIL";
      }
    }
    if (url.equals("/createAccount")) {
      if (!body.hasNextLine()) return "BAD";
      String username = body.nextLine();
      if (!body.hasNextLine()) return "BAD";
      String password = body.nextLine();
      if (Account.createSync(username, password)) {
        return "OK\n";
      }
      else{
        return "FAIL\n";
      }
    }
    if (url.equals("/getUser")) {
      if (!body.hasNextLine()) return "BAD";
      String username = body.nextLine();
      Account user = Account.getByUsername(username);
      if (user == null) return "FAIL404\n";
      return user.toBase64();
    }
    if (url.equals("/getAllUsers")) {
      return Account.all().stream()
        .map(x->x.getUsername() + "\n").collect(Collectors.joining());
    }
    if (url.equals("/getChat")) {
      if (!body.hasNextLine()) return "BAD";
      String token = body.nextLine();
      if (!body.hasNextLine()) return "BAD";
      String username = body.nextLine();
      Account user = Account.getByToken(token);
      if (user == null) return "FAIL401\n";
      return Chat.get(user.getUsername(), username).toString();
    }
    if (url.equals("/sendMessage")) {
      if (!body.hasNextLine()) return "BAD";
      String token = body.nextLine();
      if (!body.hasNextLine()) return "BAD";
      String username = body.nextLine();
      if (!body.hasNextLine()) return "BAD";
      String reply = body.nextLine();
      if (!body.hasNextLine()) return "BAD";
      String text = body.nextLine();
      Account user = Account.getByToken(token);
      if (user == null) return "FAIL401\n";
      Chat.sendMessage(user.getUsername(), username, reply , text);
      return "OK\n";
    }
    return "404\n";
  }

  static class MyHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {

    }
  }

}