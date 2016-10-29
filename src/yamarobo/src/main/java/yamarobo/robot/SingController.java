package yamarobo.robot;

import java.util.Map;

import jp.vstone.RobotLib.CPlayWave;

public class SingController implements Controller {
  @Override
  public void executeAction(String action, Map<String, String> data) {
    CPlayWave.PlayWave("resources/sound/" + "queen" + ".wav");
  }
}