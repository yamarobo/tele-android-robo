package yamarobo;

import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CSotaMotion;

public class Main {
  public static CRobotMem memory;
  public static CSotaMotion motion;

  public static void main(String[] args) {
    setUpRobot();
    HttpServer application = new HttpServer();
    application.execute();
    terminateRobot();

  }

  private static void terminateRobot() {
    // TODO Auto-generated method stub

  }

  private static void setUpRobot() {
    System.out.println("Begin set up!");
    memory = new CRobotMem();
    motion = new CSotaMotion(memory);
    System.out.println("Finish set up!");
  }

}
