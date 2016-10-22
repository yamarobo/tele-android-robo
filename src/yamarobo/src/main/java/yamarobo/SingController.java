package yamarobo;

import jp.vstone.RobotLib.CPlayWave;

public class SingController implements Controller {

  @Override
  public void execute(String action) {
    CPlayWave.PlayWave_wait("./resource/sound/" + action + ".wav");
  }
}