package yamarobo.robot;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CSotaMotion;
import yamarobo.Message;
import yamarobo.server.WebSocketListenerImpl;

public class RobotAPI {
  private static final Logger LOG = Log.getLogger(WebSocketListenerImpl.class);
  protected static CRobotMem memory;
  protected static CSotaMotion motion;
 
  public static void call(Message json) {
    boolean properlySetUp = setUpRobot();
    if (!properlySetUp) return;

    Controller controller;

    switch(json.controller) {
    case "motion":
      controller = new MotionController();
      break;
    case "sing":
      controller = new SingController();
      break;
    default:
      controller = new MotionController();
      break;
    }

    controller.executeAction(json.action, json.data);
  }

  public static void terminateRobot() {
    LOG.info("Terminate Robot Start");
    RobotAPI.motion.ServoOff();
    LOG.info("Terminate Robot Finish");
  }

  public static boolean setUpRobot() {
    LOG.info("Start Set Up Robot");
    if(memory != null && memory.isConected()) {
      LOG.info("Already Properly Set Up");
      LOG.info("Finish Set Up Robot");
      return true;
    }
    memory = new CRobotMem();
    motion = new CSotaMotion(memory);

    if (!memory.Connect()) {
      LOG.info("Fail connect memory");
      return false;
    }

    RobotAPI.motion.InitRobot_Sota();
    RobotAPI.motion.ServoOn();
    LOG.info("Finish Set Up Robot");
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      RobotAPI.terminateRobot();
    }));
    return true;
  }
}
