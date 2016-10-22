package yamarobo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

  private String requestPath;
  private ServerSocket server;

  public void execute() {
    int port = 8080;
    String documentRoot = "./resource/public/html";

    try {
      server = new ServerSocket(port);

      while (true) {
        Socket client = server.accept();
        outputRequest(client);
        outputResponse(client, documentRoot);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void outputRequest(Socket client) throws IOException {
    System.out.println("------------------------------------------");
    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

    String inline = reader.readLine();
    if (inline != null) {
      requestPath = inline.split(" ")[1];
    } else {
      requestPath = "/";
    }
    API.call(requestPath);
    if (requestPath.endsWith("/")) {
      requestPath += "index.html";
    }

    while (reader.ready() && inline != null) {
      System.out.println(inline);
      inline = reader.readLine();
    }

    System.out.println("------------------------------------------");
  }

  private void outputResponse(Socket client, String documentRoot) throws IOException {
    PrintStream printStream = new PrintStream(client.getOutputStream());

    String responseFile = documentRoot + requestPath;
    File file = new File(responseFile);

    FileInputStream fileInputStream = null;
    try {
      fileInputStream = new FileInputStream(file);

      int bodyLength = (int) file.length();
      printStream.println("HTTP/1.1 200 OK");
      printStream.println("Content-Length: " + bodyLength);
      printStream.println("Content-Type: " + "text/html");
      printStream.println("");

      byte buffer[] = new byte[bodyLength];

      fileInputStream.read(buffer);
      printStream.write(buffer, 0, bodyLength);
      printStream.flush();
    } catch (FileNotFoundException e) {
      printStream.println("HTTP/1.1 404 Not Found");
      printStream.println("");
      printStream.println("<h1>404 Not Found</h1><p>指定されたURLは使用できません</p>");
    } finally {
      if (fileInputStream != null) {
        fileInputStream.close();
      }
    }
    printStream.close();
  }
}
