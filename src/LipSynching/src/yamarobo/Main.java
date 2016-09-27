package yamarobo;

import java.util.Random;

import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CTelenoidMotion;
import jp.vstone.camera.CRoboCamera;
import jp.vstone.camera.FaceDetectLib;
import jp.vstone.camera.FaceDetectResult;
import jp.vstone.linphone.LinphonecConnector;
import jp.vstone.sotatalk.MotionAsSotaWish;
import jp.vstone.sotatalk.SpeechRecog;

public class Main {
  public static void main(String[] args) {
    if (GlobalVariable.mem.Connect()) {
      GlobalVariable.motion.InitRobot_Telenoid();
      try {
        LipSynching lipSynching = new LipSynching();
        Runtime.getRuntime().exec(
            new String[] { "/bin/sh", "-c", "echo 'lipSynching.run()'  >> /var/log/telenoid-app.log" });
        lipSynching.run();
      } catch (Exception e) {
        System.out.println("\u4f8b\u5916\u304c\u767a\u751f\u3057\u307e\u3057\u305f:");
        e.printStackTrace();
      }
      System.exit(0);
    }
  }

  public static class GlobalVariable {
    public static CRobotMem mem = new CRobotMem();
    public static CTelenoidMotion motion = new CTelenoidMotion(mem);
    public static boolean TRUE = true;
    public static Object recvReturnValue;
    public static CRecordMic mic;
    public static FaceDetectResult faceresult;
    public static int detectCount;
    public static String recvString;
    public static Random random;
    public static LinphonecConnector linphonecConnector;
    public static CRoboCamera robocam;
    public static FaceDetectLib.FaceUser faceuse;
    public static SpeechRecog recog;
    public static SpeechRecog.RecogResult abortresult;
    public static MotionAsSotaWish sotawish;
    public static boolean booleanResult;

    static {
      // mic = new CRecordMic();
      // detectCount = 0;
      // random = new Random();
      // linphonecConnector = new LinphonecConnector();
      // robocam = new CRoboCamera("/dev/video0", (CRobotMotion)motion);
      // recog = new SpeechRecog((CRobotMotion)motion);
      // sotawish = new MotionAsSotaWish((CRobotMotion)motion);
    }
  }

}
