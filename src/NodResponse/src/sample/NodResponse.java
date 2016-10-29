package sample;

import static jp.vstone.RobotLib.CTelenoidMotion.SV_HEAD_P;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_HEAD_R;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_HEAD_Y;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_L_ARM;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_MOUTH;
import static jp.vstone.RobotLib.CTelenoidMotion.SV_R_ARM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.sotatalk.SpeechRecog;

public class NodResponse {
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
	private boolean runningNod = false;

	public NodResponse() {
		try {
			Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "echo 'START'  >> telenoid-app.log" });
		} catch (Exception e) {
			e.printStackTrace();
		}

		Main.GlobalVariable.motion.ServoOn();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
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
		// Main.GlobalVariable.linphonecConnector.InitLinphonecConnector("linphonec
		// -C");
		// Main.GlobalVariable.linphonecConnector.SetFrinedListPath("/etc/linphone/friendlist");
		// Main.GlobalVariable.linphonecConnector.start();
	}

	public void run() {
		this.runningNod = true;

		createNodResponseThread().start();
		int loopCount = 0;
		while(loopCount < 20) {
			CRobotUtil.wait(7000);
			loopCount++;
		}
		this.runningNod = false;
		
		exit();
	}

	private Thread createNodResponseThread() {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					List<Byte> servoList = new ArrayList<Byte>();
					servoList.addAll(Arrays.asList(SV_HEAD_P));
					if (servoList.size() > 0) {
						Main.GlobalVariable.motion.LockServoHandle(servoList.toArray(new Byte[0]));
					}
					CRobotUtil.wait(1000);
					while (NodResponse.this.runningNod) {
						Runtime.getRuntime()
				      .exec(new String[] { "/bin/sh", "-c", "echo 'runningNod...'  >> telenoid-app.log" });
						
						CRobotPose nod = new CRobotPose();

		        nod.SetPose(new Byte[] { SV_HEAD_P }, new Short[] { -400 });
		        nod.SetTorque(new Byte[] { SV_HEAD_P }, new Short[] { 100 });
						Main.GlobalVariable.motion.play(nod, 400);
						CRobotUtil.wait(200);
						
		        nod.SetPose(new Byte[] { SV_HEAD_P }, new Short[] { 400 });
		        nod.SetTorque(new Byte[] { SV_HEAD_P }, new Short[] { 100 });
						Main.GlobalVariable.motion.play(nod, 400);

						CRobotUtil.wait(6000);
						// CRobotUtil.wait(Main.GlobalVariable.random.nextInt(2500) + 7500);
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
						NodResponse.this.batteryVoltage = Main.GlobalVariable.motion.getBatteryVoltage();
						String tmpBatteryMessage = "Battery:" + Integer.toString(NodResponse.this.batteryVoltage);
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
