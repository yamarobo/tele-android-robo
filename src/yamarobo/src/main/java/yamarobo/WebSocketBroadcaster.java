package yamarobo;

import java.util.ArrayList;
import java.util.List;

public class WebSocketBroadcaster {
  private static WebSocketBroadcaster INSTANCE = new WebSocketBroadcaster();
  private List<RobotSocket> clients = new ArrayList<RobotSocket>();

  private WebSocketBroadcaster(){}

  protected static WebSocketBroadcaster getInstance(){
      return INSTANCE;
  }

  /**
   * Add Client
   * */
  protected void join(RobotSocket socket){
      clients.add(socket);
  }
  /**
   * Delete Client
   * */
  protected void bye(RobotSocket socket){
      clients.remove(socket);
  }

  /**
   * BroadCast to joined member
   * */
  protected void sendToAll(String message){
      for(RobotSocket member: clients){
          member.getSession().getRemote().sendStringByFuture(message);
      }
  }
}
