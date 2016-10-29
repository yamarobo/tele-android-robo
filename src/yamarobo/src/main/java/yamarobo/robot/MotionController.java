package yamarobo.robot;

import static jp.vstone.RobotLib.CSotaMotion.SV_BODY_Y;
import static jp.vstone.RobotLib.CSotaMotion.SV_HEAD_P;
import static jp.vstone.RobotLib.CSotaMotion.SV_HEAD_R;
import static jp.vstone.RobotLib.CSotaMotion.SV_HEAD_Y;
import static jp.vstone.RobotLib.CSotaMotion.SV_L_ELBOW;
import static jp.vstone.RobotLib.CSotaMotion.SV_L_SHOULDER;
import static jp.vstone.RobotLib.CSotaMotion.SV_R_ELBOW;
import static jp.vstone.RobotLib.CSotaMotion.SV_R_SHOULDER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;

public class MotionController implements Controller {
  private static final Logger LOG = Log.getLogger(MotionController.class);
  public static final Byte[] SV_ALL = new Byte[] { SV_BODY_Y, SV_L_SHOULDER, SV_L_ELBOW, SV_R_SHOULDER, SV_R_ELBOW,
      SV_HEAD_Y, SV_HEAD_P, SV_HEAD_R, };
  public static CRobotPose pose;

  @Override
  public void executeAction(String action, Map<String, String> data) {
    Thread thread = new Thread(new Runnable(){
      @Override
      public void run() {
        RobotAPI.motion.LockServoHandle(SV_ALL);
        switch (action) {
        case "manual":
          manual(data);
          break;
        case "happy":
          happy();
          break;
        default:
          index();
          break;
        }
        RobotAPI.motion.UnLockServoHandle();
      }
    });

    thread.run();
  }

  private void manual(Map<String, String> data) {
    List<Byte> idsList = new ArrayList<Byte>();
    List<Short> posList = new ArrayList<Short>();

    Byte key;
    Short val;
    for (Map.Entry<String, String> entry : data.entrySet()) {
      LOG.info("key: " + entry.getKey() + ", value: " + entry.getValue());
      try {
        Integer.parseInt(entry.getKey());
        Integer.parseInt(entry.getValue());
      } catch (NumberFormatException e) {
        continue;
      }

      key = Byte.valueOf(entry.getKey());
      val = Short.valueOf(entry.getValue());
      switch (key) {
      case SV_BODY_Y:
      case SV_L_SHOULDER:
      case SV_L_ELBOW:
      case SV_R_SHOULDER:
      case SV_R_ELBOW:
      case SV_HEAD_Y:
      case SV_HEAD_P:
      case SV_HEAD_R:
        idsList.add(key);
        posList.add(val);
        break;
      }
    }

    Byte[] ids = idsList.toArray(new Byte[idsList.size()]);
    Short[] pos = posList.toArray(new Short[posList.size()]);

    pose = new CRobotPose();
    pose.SetPose(ids, pos);
    RobotAPI.motion.play(pose, 500);
    CRobotUtil.wait(500);
  }

  private void index() {
    pose = new CRobotPose();
    pose.SetPose(SV_ALL, new Short[] { 0, -500, 0, 500, 0, 0, 50, 0 });
    RobotAPI.motion.play(pose, 1000);
    CRobotUtil.wait(1000);
  }

  private void happy() {
    int count = 0;
    while (count < 3) {
      pose = new CRobotPose();
      pose.SetPose(SV_ALL, new Short[] { 0, 500, 0, -500, 0, 0, 0, 0 });
      RobotAPI.motion.play(pose, 500);
      CRobotUtil.wait(500);

      CPlayWave.PlayWave("resources/sound/" + "happy" + ".wav");

      pose = new CRobotPose();
      pose.SetPose(SV_ALL, new Short[] { 0, -900, 0, 900, 0, 0, 0, 0 });
      RobotAPI.motion.play(pose, 1000);
      CRobotUtil.wait(1000);

      count++;
    }
  }
}
