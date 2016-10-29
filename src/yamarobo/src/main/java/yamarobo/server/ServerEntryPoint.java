package yamarobo.server;

import java.net.URL;
import java.util.Objects;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class ServerEntryPoint {
  private static final Logger LOG = Log.getLogger(WebSocketListenerImpl.class);

  public static void boot() {
    LOG.info("WebSocket Server Init");

    Server server = new Server(3000);

    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);

    ServletHolder webServletHolder = new ServletHolder("api", new WebSocketServletImpl());
    context.addServlet(webServletHolder, "/api");

    URL urlStatics = Thread.currentThread().getContextClassLoader().getResource("index.html");
    Objects.requireNonNull(urlStatics, "Unable to find index.html in classpath");
    String urlBase = urlStatics.toExternalForm().replaceFirst("/[^/]*$", "/");
    ServletHolder defaultHolder = new ServletHolder("default", new DefaultServlet());
    defaultHolder.setInitParameter("resourceBase", urlBase);
    defaultHolder.setInitParameter("dirAllowed", "true");
    context.addServlet(defaultHolder, "/");

    try {
      server.start();
      LOG.info("WebSocket Server Start");
    } catch (InterruptedException e) {
      System.out.println(e.getMessage());
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
