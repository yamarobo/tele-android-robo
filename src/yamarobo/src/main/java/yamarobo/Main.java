package yamarobo;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CSotaMotion;
import yamarobo.servlet.WebSocketServletImpl;

public class Main {
  public static CRobotMem memory;
  public static CSotaMotion motion;

  public static void main(String[] args) {
    setUpRobot();
    System.out.println("WebSocket Server Init");
    
    String resourceBase = "public/html";
    if (args.length == 1) resourceBase  = args[0] + resourceBase;
    System.out.println(resourceBase);
    
    // resource handler & context handler
    ContextHandler contextHandler = new ContextHandler();
    contextHandler.setContextPath("/static");
    contextHandler.setResourceBase(resourceBase);
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setCacheControl("no-cache");
    contextHandler.setHandler(resourceHandler);

    // websocket handler
    ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    WebSocketServletImpl wsservlet = new WebSocketServletImpl();
    servletContextHandler.addServlet(new ServletHolder(wsservlet), "/");

    // collect handlers
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { contextHandler, servletContextHandler });

    Server server = new Server(8080);
    server.setHandler(handlers);
    
    URL urlStatics = Thread.currentThread().getContextClassLoader().getResource("index.html");
    Objects.requireNonNull(urlStatics,"Unable to find index.html in classpath");
    String urlBase = urlStatics.toExternalForm().replaceFirst("/[^/]*$","/");
    ServletHolder defHolder = new ServletHolder("default",new DefaultServlet());
    defHolder.setInitParameter("resourceBase",urlBase);
    defHolder.setInitParameter("dirAllowed","true");
    context.addServlet(defHolder,"/");

    try {
      server.start();
      System.out.println("Server Start");
    } catch (InterruptedException e) {
      System.out.println(e.getMessage());
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private static void terminateRobot() {
    System.out.println("Begin:\tterminate robot!");
    System.out.println("Finish:\tterminate robot!");
  }

  private static void setUpRobot() {
    System.out.println("Begin:\tset up robot!");
    // memory = new CRobotMem();
    // motion = new CSotaMotion(memory);
    System.out.println("Finish:\tset up robot!");
  }

}
