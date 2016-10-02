package yamarobo;

import static jp.vstone.RobotLib.CTelenoidMotion.SV_HEAD_P;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_HEAD_R;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_HEAD_Y;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_L_ARM;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_MOUTH;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_R_ARM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.sotatalk.SpeechRecog;

public class LipSynching {
  public CRobotPose pose;
  public int batteryVoltage;
  public String linphonecMessage = "";
  public String linphonecInterruptibleMessage = "";
  public int isExit = 0;
  public int useAutoAnswer = 0;
  public String time_string;
  public SpeechRecog.RecogResult recogresult;
  private boolean isPowerOff = false;
  final Byte[] SV_ALL = new Byte[] { SV_MOUTH, SV_L_ARM, SV_R_ARM, SV_HEAD_Y, SV_HEAD_P, SV_HEAD_R };
  private boolean isPlaying = false;
  private boolean isLipSynching = false;

  public LipSynching() {
    try {
      Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "echo 'START'  >> /var/log/telenoid-app.log" });
    } catch (Exception e) {
      e.printStackTrace();
    }

    Main.GlobalVariable.motion.ServoOn();
    Runtime.getRuntime().addShutdownHook(
        new Thread(() -> {
          try {
            CRobotUtil.Log((String) this.getClass().getSimpleName(),
                (String) "Kill signal is received, start to kill linphonec");
            Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "killall linphonec" });
            process.waitFor();
            process.destroy();
            Thread.sleep(2000);
          } catch (Exception e) {
            e.printStackTrace();
          }

          try {
            CRobotUtil.Log((String) this.getClass().getSimpleName(),
                (String) "Kill signal is received, start to kill VUMeter");
            Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "systemctl stop vumeter" });
            process.waitFor();
            process.destroy();
            Thread.sleep(1000);
          } catch (Exception e) {
            e.printStackTrace();
          }
          System.out.println("shutdown..");
        }));

    try {
      Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "systemctl stop vumeter" });
      process.waitFor();
      process.destroy();
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "systemctl start vumeter" });
      process.waitFor();
      process.destroy();
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "killall linphonec" });
      process.waitFor();
      process.destroy();
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // linphone 初期設定 & 起動
    // いらない？
    // Main.GlobalVariable.linphonecConnector = new LinphonecConnector();
    // Main.GlobalVariable.linphonecConnector.SetLinphonecConfigFile("/etc/linphone/.linphonerc");
    // Main.GlobalVariable.linphonecConnector.InitLinphonecConnector("linphonec -C");
    // Main.GlobalVariable.linphonecConnector.SetFrinedListPath("/etc/linphone/friendlist");
    // Main.GlobalVariable.linphonecConnector.start();
  }

  public void run() {
    // 音声入力の大きさに応じて口の開き方を変える
    this.isLipSynching = true;
    createLipSyncThread().start();

    CRobotUtil.wait((int) 1000);

    this.isPlaying = true;
    playMotionThread().start();

    CRobotUtil.wait((int) 1000);

    CPlayWave.PlayWave_wait("sound/queen-watc-long.wav");

    this.isLipSynching = false;
    this.isPlaying = false;

    CRobotUtil.wait((int) 1000);

    exit();
  }

  private Thread createLipSyncThread() {
    return new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          List<Byte> servoList = new ArrayList<Byte>();
          servoList.addAll(Arrays.asList(SV_MOUTH));
          if (servoList.size() > 0) {
            Main.GlobalVariable.motion.LockServoHandle(servoList.toArray(new Byte[0]));
          }
          Runtime.getRuntime().exec(
              new String[] { "/bin/sh", "-c", "echo 'createLipSyncThread'  >> /var/log/telenoid-app.log" });
          int volume = 0;

          while (LipSynching.this.isLipSynching) {
            // 音声入力を受け取る処理
            volume = Main.GlobalVariable.mem.AouioDiff.get();
            if ((volume /= 10) > 4) {
              volume = 4;
            }

            lipSync(volume);
            CRobotUtil.wait((int) 30);
          }
          if (servoList.size() > 0) {
            Main.GlobalVariable.motion.UnLockServoHandle();
          }

        } catch (Exception e) {
          CRobotUtil.Err((String) "jp.vstone.block.thread",
              (String) "\u4f8b\u5916\u304c\u767a\u751f\u3057\u307e\u3057\u305f\u3002");
          e.printStackTrace();
        }
      }
    });
  }

  private Thread playMotionThread() {
    return new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          List<Byte> servoList = new ArrayList<Byte>();
          servoList.addAll(Arrays.asList(SV_L_ARM, SV_R_ARM, SV_HEAD_Y, SV_HEAD_P, SV_HEAD_R));
          if (servoList.size() > 0) {
            Main.GlobalVariable.motion.LockServoHandle(servoList.toArray(new Byte[0]));
          }
          while (LipSynching.this.isPlaying) {
            CRobotUtil.Log((String) this.getClass().getSimpleName(), (String) "playing now...");
            LipSynching.this.pose = getRandomPose();
            Main.GlobalVariable.motion.play(LipSynching.this.pose, 2500);
            CRobotUtil.wait(2500);
            CRobotUtil.wait(Main.GlobalVariable.random.nextInt(2500) + 7500);
          }

          if (servoList.size() > 0) {
            Main.GlobalVariable.motion.UnLockServoHandle();
          }
        } catch (Exception e) {
          CRobotUtil.Err((String) "jp.vstone.block.thread",
              (String) "\u4f8b\u5916\u304c\u767a\u751f\u3057\u307e\u3057\u305f\u3002");
          e.printStackTrace();
        }
      }
    });
  }
  
  private CRobotPose getRandomPose() {
    CRobotPose randomPose = new CRobotPose();
    Short[] poseList = null;
    Short[] torqueList = null;
    switch (Main.GlobalVariable.random.nextInt(3)) {
      case 0: {
        poseList = new Short[] { -500, 500, 600, 0, 0 };
        torqueList = new Short[] { 0, 0, 100, 100, 100 };
        break;
      }
      case 1: {
        poseList = new Short[] { -500, 500, -600, 0, 0 };
        torqueList = new Short[] { 0, 0, 100, 100, 100 };
        break;
      }
      case 2: {
        poseList = new Short[] { -500, 500, -600, 450, 0 };
        torqueList = new Short[] { 0, 0, 100, 100, 100 };
        break;
      }
      case 3: {
        poseList = new Short[] { -500, 500, 600, 450, 0 };
        torqueList = new Short[] { 0, 0, 100, 100, 100 };
        break;
      }
      case 4: {
        poseList = new Short[] { -500, 500, 0, 450, 0 };
        torqueList = new Short[] { 0, 0, 100, 100, 100 };
        break;
      }
      case 5: {
        poseList = new Short[] { -500, 500, 0, -450, 0 };
        torqueList = new Short[] { 0, 0, 100, 100, 100 };
        break;
      }
      case 6: {
        poseList = new Short[] { -500, 500, 0, 0, -450 };
        torqueList = new Short[] { 0, 0, 100, 100, 100 };
        break;
      }
      case 7: {
        poseList = new Short[] { -500, 500, 0, 0, 450 };
        torqueList = new Short[] { 0, 0, 100, 100, 100 };
        break;
      }
      default: {
        poseList = new Short[] { -500, 500, 0, 0, 0 };
        torqueList = new Short[] { 0, 0, 100, 100, 100 };
      }
    }
    Byte[] svlist = new Byte[] { SV_L_ARM, SV_R_ARM, SV_HEAD_Y, SV_HEAD_P, SV_HEAD_R };
    randomPose.SetPose(svlist, poseList);
    randomPose.SetTorque(svlist, torqueList);
    return randomPose;
  }

  private void lipSync(int volume) {
    switch (volume) {
      case 0: {
        LipSynching.this.pose = new CRobotPose();
        LipSynching.this.pose.SetPose(new Byte[] { SV_MOUTH }, new Short[] { 0 });
        LipSynching.this.pose.SetTorque(new Byte[] { SV_MOUTH }, new Short[] { 100 });
        Main.GlobalVariable.motion.play(LipSynching.this.pose, 600);
        break;
      }
      case 1: {
        LipSynching.this.pose = new CRobotPose();
        LipSynching.this.pose.SetPose(new Byte[] { SV_MOUTH }, new Short[] { 400 });
        LipSynching.this.pose.SetTorque(new Byte[] { SV_MOUTH }, new Short[] { 100 });
        Main.GlobalVariable.motion.play(LipSynching.this.pose, 300);
        break;
      }
      case 2: {
        LipSynching.this.pose = new CRobotPose();
        LipSynching.this.pose.SetPose(new Byte[] { SV_MOUTH }, new Short[] { 600 });
        LipSynching.this.pose.SetTorque(new Byte[] { SV_MOUTH }, new Short[] { 100 });
        Main.GlobalVariable.motion.play(LipSynching.this.pose, 300);
        break;
      }
      case 3: {
        LipSynching.this.pose = new CRobotPose();
        LipSynching.this.pose.SetPose(new Byte[] { SV_MOUTH }, new Short[] { 800 });
        LipSynching.this.pose.SetTorque(new Byte[] { SV_MOUTH }, new Short[] { 100 });
        Main.GlobalVariable.motion.play(LipSynching.this.pose, 300);
        break;
      }
      default: {
        LipSynching.this.pose = new CRobotPose();
        LipSynching.this.pose.SetPose(new Byte[] { SV_MOUTH }, new Short[] { 0 });
        LipSynching.this.pose.SetTorque(new Byte[] { SV_MOUTH }, new Short[] { 100 });
        Main.GlobalVariable.motion.play(LipSynching.this.pose, 300);
      }
    }
  }
  
  public void exit() {
    if (Main.GlobalVariable.linphonecConnector != null) {
      Main.GlobalVariable.linphonecConnector.EndLinphonecConnector();
      try {
        Main.GlobalVariable.linphonecConnector.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    try {
      Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "killall linphonec" });
      process.waitFor();
      process.destroy();
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "systemctl stop vumeter" });
      process.waitFor();
      process.destroy();
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (isPowerOff) {
      try {
        Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "poweroff" });
        process.waitFor();
        process.destroy();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void SendBatteryThread(final int frequency) throws SpeechRecog.SpeechRecogAbortException {
    if (!Main.GlobalVariable.TRUE) {
      throw new SpeechRecog.SpeechRecogAbortException("default");
    }
    Thread th = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          while (Main.GlobalVariable.linphonecConnector.IsConnected()) {
            LipSynching.this.batteryVoltage = Main.GlobalVariable.motion.getBatteryVoltage();
            String tmpBatteryMessage = "Battery:" + Integer.toString(LipSynching.this.batteryVoltage);
            Main.GlobalVariable.linphonecConnector.SendChatMessageToLinphonec(tmpBatteryMessage);
            CRobotUtil.wait((int) frequency);
          }
        } catch (Exception e) {
          CRobotUtil.Err((String) "jp.vstone.block.thread",
              (String) "\u4f8b\u5916\u304c\u767a\u751f\u3057\u307e\u3057\u305f\u3002");
          e.printStackTrace();
        }
      }
    });
    th.start();
  }
}
