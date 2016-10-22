package yamarobo;

import java.awt.Color;

import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import static jp.vstone.RobotLib.CSotaMotion.SV_BODY_Y;
import static jp.vstone.RobotLib.CSotaMotion.SV_L_SHOULDER;
import static jp.vstone.RobotLib.CSotaMotion.SV_L_ELBOW;
import static jp.vstone.RobotLib.CSotaMotion.SV_R_SHOULDER;
import static jp.vstone.RobotLib.CSotaMotion.SV_R_ELBOW;
import static jp.vstone.RobotLib.CSotaMotion.SV_HEAD_Y;
import static jp.vstone.RobotLib.CSotaMotion.SV_HEAD_P;
import static jp.vstone.RobotLib.CSotaMotion.SV_HEAD_R;

public class MotionController implements Controller {
  public static final Byte[] SV_ALL = new Byte[] { SV_BODY_Y, SV_L_SHOULDER, SV_L_ELBOW, SV_R_SHOULDER, SV_R_ELBOW,
      SV_HEAD_Y, SV_HEAD_P, SV_HEAD_R, };
  public static CRobotPose pose;

  @Override
  public void execute(String action) {
    if (Main.memory.Connect()) {
      Main.motion.InitRobot_Sota();
      Main.motion.ServoOn();
      if ("happy".equals(action)) {
        happy();
      } else {
        index();
      }
    }
  }

  private void index() {
    pose = new CRobotPose();
    pose.SetPose(SV_ALL, new Short[] { 0, 0, 0, 0, 0, 0, 0, 0 });
    pose.setLED_Sota(Color.BLACK, Color.BLACK, 255, Color.BLACK);
    Main.motion.play(pose, 500);
    CRobotUtil.wait(500);

    pose = new CRobotPose();
    pose.SetPose(SV_ALL, new Short[] { 0, -900, 0, 900, 0, 0, 0, 0 });
    pose.setLED_Sota(Color.BLACK, Color.BLACK, 255, Color.BLACK);
    Main.motion.play(pose, 500);
    CRobotUtil.wait(500);
  }

  private void happy() {
    int count = 0;
    while (count < 3) {
      pose = new CRobotPose();
      pose.SetPose(SV_ALL, new Short[] { 0, 500, 0, -500, 0, 0, 0, 0 });
      pose.setLED_Sota(Color.BLACK, Color.BLACK, 255, Color.BLACK);
      Main.motion.play(pose, 500);
      CRobotUtil.wait(500);

      CPlayWave.PlayWave_wait("./resource/sound/" + "happy" + ".wav");

      pose = new CRobotPose();
      pose.SetPose(SV_ALL, new Short[] { 0, -900, 0, 900, 0, 0, 0, 0 });
      pose.setLED_Sota(Color.BLACK, Color.BLACK, 255, Color.BLACK);
      Main.motion.play(pose, 1000);
      CRobotUtil.wait(500);

      count++;
    }
  }
}
