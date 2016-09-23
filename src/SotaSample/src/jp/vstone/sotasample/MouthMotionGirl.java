package jp.vstone.sotasample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CTelenoidMotion;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_MOUTH;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_L_ARM;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_R_ARM;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_HEAD_Y;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_HEAD_P;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_HEAD_R;
import jp.vstone.linphone.LinphonecConnector;
import jp.vstone.sotatalk.TextToSpeechSota;

public class MouthMotionGirl {
  private static final String TAG = "MouthMotion";
  private static CRobotMem memory = new CRobotMem();
  private static CTelenoidMotion motion = new CTelenoidMotion(memory);
  private static LinphonecConnector linphonecConnector;
  private static CRobotPose pose = new CRobotPose();
  private static boolean lipSync = true;

  public static void main(String[] args) {

    if (memory.Connect()) {
      CRobotUtil.Log(TAG, (String) "Init robot telenoid");
      motion.InitRobot_Telenoid();
      CRobotUtil.Log(TAG, (String) "Servo on");
      motion.ServoOn();

      // 各プロセスの再起動
      // いらない？
      //initialize();

      CRobotUtil.Log(TAG, (String) "execute");
      execute();
      
      //exit();
    }

  }

  private static void execute() {
    final Byte[] SV_ALL = new Byte[] { SV_MOUTH, SV_L_ARM, SV_R_ARM, SV_HEAD_Y,
        SV_HEAD_P, SV_HEAD_R };

    pose.SetPose(SV_ALL, new Short[] { 0, -500, 500, 0, -50, 0 });
    pose.SetTorque(SV_ALL, new Short[] { 100, 0, 0, 100, 100, 100 });
    motion.play(pose, 1000);
    CRobotUtil.wait((int) 1000);

    CRobotUtil.Log(TAG, (String) "start lip sync thread");
    Thread thread = createLipSyncThread();
    thread.start();

    CPlayWave.PlayWave_wait("sound/sexy-voice.wav");

    CRobotUtil.Log(TAG, (String) "stop lip sync thread");
    lipSync = false;
  }
  
  private static Thread createLipSyncThread() {
    return new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          // ポーズ用のリストの宣言と初期化
          ArrayList<Byte> svList = new ArrayList<Byte>();
          svList.addAll(Arrays.asList(SV_MOUTH));
          // > 0 なはずがない
          if (svList.size() > 0) {
              motion.LockServoHandle(svList.toArray(new Byte[0]));
          }

          int volume = 0;

          motion.LockServoHandle(svList.toArray(new Byte[0]));
          while (lipSync) {
            // 音声入力を受け取る処理
            volume = memory.AouioDiff.get();
            if ((volume /= 10) > 4) {
              volume = 4;
            }

            lipSync(volume);
            CRobotUtil.wait((int) 30);
          }
          motion.UnLockServoHandle();

        } catch (Exception e) {
          CRobotUtil.Err(
              (String) "jp.vstone.block.thread",
              (String) "\u4f8b\u5916\u304c\u767a\u751f\u3057\u307e\u3057\u305f\u3002"
            );
          e.printStackTrace();
        }
      }
    });
  }

  // 音声入力の大きさに応じて口の開き方を変える
  private static void lipSync(int volume) {
    switch (volume) {
      case 0: {
        pose = new CRobotPose();
        pose.SetPose(new Byte[] { SV_MOUTH }, new Short[] { 0 });
        pose.SetTorque(new Byte[] { SV_MOUTH }, new Short[] { 100 });
        motion.play(pose, 600);
        break;
      }
      case 1: {
        pose = new CRobotPose();
        pose.SetPose(new Byte[] { SV_MOUTH }, new Short[] { 400 });
        pose.SetTorque(new Byte[] { SV_MOUTH }, new Short[] { 100 });
        motion.play(pose, 300);
        break;
      }
      case 2: {
        pose = new CRobotPose();
        pose.SetPose(new Byte[] { SV_MOUTH }, new Short[] { 600 });
        pose.SetTorque(new Byte[] { SV_MOUTH }, new Short[] { 100 });
        motion.play(pose, 300);
        break;
      }
      case 3: {
        pose = new CRobotPose();
        pose.SetPose(new Byte[] { SV_MOUTH }, new Short[] { 800 });
        pose.SetTorque(new Byte[] { SV_MOUTH }, new Short[] { 100 });
        motion.play(pose, 300);
        break;
      }
      default: {
        pose = new CRobotPose();
        pose.SetPose(new Byte[] { SV_MOUTH }, new Short[] { 0 });
        pose.SetTorque(new Byte[] { SV_MOUTH }, new Short[] { 100 });
        motion.play(pose, 300);
      }
    }
  }

  public static void initialize() {
    Runtime.getRuntime().addShutdownHook(
      new Thread(() -> {
        try {
          CRobotUtil.Log(TAG, (String) "Kill signal is received, start to kill linphonec");
          String[] command = new String[] { "/bin/sh", "-c", "killall linphonec" };
          Process process = Runtime.getRuntime().exec(command);
          process.waitFor();
          process.destroy();
          Thread.sleep(2000);
        } catch (Exception e) {
          e.printStackTrace();
        }

        try {
          CRobotUtil.Log(TAG, (String) "Kill signal is received, start to kill VUMeter");
          String[] command = new String[] { "/bin/sh", "-c", "systemctl stop vumeter" };
          Process process = Runtime.getRuntime().exec(command);
          process.waitFor();
          process.destroy();
          Thread.sleep(1000);
        } catch (Exception e) {
          e.printStackTrace();
        }
        System.out.println("shutdown..");
      }));

    // 音声入力サービス -- 一旦停止
    try {
      String[] command = new String[] { "/bin/sh", "-c", "systemctl stop vumeter" };
      Process process = Runtime.getRuntime().exec(command);
      process.waitFor();
      process.destroy();
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 音声入力サービス -- 起動
    try {
      String[] command = new String[] { "/bin/sh", "-c", "systemctl start vumeter" };
      Process process = Runtime.getRuntime().exec(command);
      process.waitFor();
      process.destroy();
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // linphone process の停止
    try {
      String[] command = new String[] { "/bin/sh", "-c", "killall linphonec" };
      Process process = Runtime.getRuntime().exec(command);
      process.waitFor();
      process.destroy();
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // linphone 初期設定 & 起動
    // いらない？
    linphonecConnector = new LinphonecConnector();
    linphonecConnector.SetLinphonecConfigFile("/etc/linphone/.linphonerc");
    linphonecConnector.InitLinphonecConnector("linphonec -C");
    linphonecConnector.SetFrinedListPath("/etc/linphone/friendlist");
    linphonecConnector.start();
  }
  
  // 停止処理
  public static void exit() {
    if (linphonecConnector != null) {
      linphonecConnector.EndLinphonecConnector();
      try {
        linphonecConnector.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    try {
      String[] command = new String[] { "/bin/sh", "-c", "killall linphonec" };
      Process process = Runtime.getRuntime().exec(command);
      process.waitFor();
      process.destroy();
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      String[] command = new String[] { "/bin/sh", "-c", "systemctl stop vumeter" };
      Process process = Runtime.getRuntime().exec(command);
      process.waitFor();
      process.destroy();
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}