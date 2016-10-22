package yamarobo;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.camera.CRoboCamera;
import jp.vstone.camera.CameraCapture;
import jp.vstone.camera.FaceDetectResult;

public class FaceDetect {
  static final String TAG = "FaceTrackingSample";
  static final int SMILE_POINT = 45;

  public void execute() {
    CRobotUtil.Log(TAG, "Start " + TAG);
    
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      CRobotPose defaultPose = new CRobotPose();
      // 頭を動かさずに撮影する -> 頭の角度を指定しない
      defaultPose.SetPose(new Byte[] { 1, 2, 3, 4, 5 }, new Short[] { 0, -900, 0, 900, 0, 0, 0, 0 });
      defaultPose.setLED_Sota(Color.BLACK, Color.BLACK, 255, Color.BLACK);
      Main.motion.play(defaultPose, 500);
      CRobotUtil.wait(500);
    }));

    CRobotPose pose;
    // VSMDと通信ソケット・メモリアクセス用クラス
    CRobotMem memory = Main.memory;
    // Sota用モーション制御クラス
    CSotaMotion motion = Main.motion;
    
    CRoboCamera camera = new CRoboCamera("/dev/video0", motion);

    if (memory.Connect()) {
      // Sota仕様にVSMDを初期化
      motion.InitRobot_Sota();

      CRobotUtil.Log(TAG, "Rev. " + memory.FirmwareRev.get());

      // サーボモータを現在位置でトルクOnにする
      CRobotUtil.Log(TAG, "Servo On");
      motion.ServoOn();

      // すべての軸を動作
      pose = new CRobotPose();
      pose.SetPose(new Byte[] { 1, 2, 3, 4, 5, 6, 7, 8 } // id
          , new Short[] { 0, -900, 0, 900, 0, 0, 0, 0 } // target pos
      );
      // LEDを点灯（左目：赤、右目：赤、口：Max、電源ボタン：赤）
      pose.setLED_Sota(Color.BLUE, Color.BLUE, 255, Color.GREEN);

      motion.play(pose, 500);
      CRobotUtil.wait(500);

      // 笑顔推定有効
      camera.setEnableSmileDetect(true);
      // 顔検索有効
      camera.setEnableFaceSearch(true);
      // フェイストラッキング開始
      camera.StartFaceTraking();
      // cam.StartFaceDetect();

      while (true) {
        FaceDetectResult result = camera.getDetectResult();
        if (result.isDetect()) {
          CRobotUtil.Log(TAG, "[Detect] Smile:" + result.getSmile());
          //
          if (result.getSmile() > SMILE_POINT) {

            // LEDだけ先に変更
            pose.setLED_Sota(Color.ORANGE, Color.ORANGE, 255, Color.GREEN);
            // playに任意のKeyを指定すると、
            motion.play(pose, 100, "FACE_LED");

            // 写真を取る前のポーズ＋音声
            CPlayWave.PlayWave("./resource/sound/take_a_photo.wav");
            pose = new CRobotPose(); // @<BlockInfo>jp.vstone.block.pose,208,80,208,80,False,2,コメント@</BlockInfo>
            pose.SetPose(new Byte[] { 1, 2, 3, 4, 5 }, new Short[] { -1, 71, -895, 1, 769 });
            motion.play(pose, 1000);

            // フェイストラッキング停止
            camera.StopFaceTraking();
            // 撮影用に初期化
            camera.initStill(new CameraCapture(CameraCapture.CAP_IMAGE_SIZE_5Mpixel, CameraCapture.CAP_FORMAT_MJPG));

            // 撮影時のポーズ＋音声
            CPlayWave.PlayWave("./resource/sound/pasha.wav");
            pose = new CRobotPose();
            // 頭を動かさずに撮影する -> 頭の角度を指定しない
            // //@<BlockInfo>jp.vstone.block.pose,272,80,272,80,False,1,コメント@</BlockInfo>
            pose.SetPose(new Byte[] { 1, 2, 3, 4, 5 }, new Short[] { 1, 69, -21, 3, -35 });
            motion.play(pose, 1000);
            // 撮影
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentTime = sdf.format(Calendar.getInstance().getTime());
            camera.StillPicture("./facedetect/photo" + currentTime);

            CPlayWave.PlayWave("./resource/sound/nice_photo.wav");
            pose = new CRobotPose();
            // 頭を動かさずに撮影する -> 頭の角度を指定しない
            pose.SetPose(new Byte[] { 1, 2, 3, 4, 5 }, new Short[] { 0, -900, 0, 900, 0 });
            motion.play(pose, 1000);
            // フェイストラッキング開始
            camera.StartFaceTraking();
          } else {
            pose.setLED_Sota(Color.GREEN, Color.GREEN, 255, Color.GREEN);
            motion.play(pose, 500);
          }
        } else {
          CRobotUtil.Log(TAG, "[Not Detect]");
          pose.setLED_Sota(Color.BLUE, Color.BLUE, 255, Color.GREEN);
          motion.play(pose, 500);
        }
        CRobotUtil.wait(500);
      }
    }

    motion.ServoOff();

  }
}
