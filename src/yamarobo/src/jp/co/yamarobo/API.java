package jp.co.yamarobo;

public class API {

  public static void call(String requestPath) {
    ControllerAction controllerAction = new ControllerAction(requestPath);
    
    Controller controller;
    switch(controllerAction.controller) {
    case "sing":
      controller = new SingController();
      controller.execute(controllerAction.action);
      break;
    default:
      controller = new MotionController();
      controller.execute(controllerAction.action);
      break;
    }
  }
  
  private static class ControllerAction {
    String controller;
    String action;

    ControllerAction(String requestPath) {
      if (requestPath == null) {
        this.controller = "root"; 
        this.action     = "index";
      }
      
      String[] controlllerAction = requestPath.split("/");
      this.controller = (controlllerAction.length > 1) ? controlllerAction[1] : "root";
      this.action     = (controlllerAction.length > 2) ? controlllerAction[2] : "index";
    }
  }
}
