package com.jiaying.mediatablet.thread;

import java.io.File;
import java.io.IOException;

import android.util.Log;

import com.jiaying.mediatablet.net.softfan.FtpSenderFile;
import com.jiaying.mediatablet.net.softfan.SoftFanFTPException;
import com.jiaying.mediatablet.utils.SelfFile;


public class SendVideoThread extends Thread {

	String lPath, rPath;
	String resultStr;

	public SendVideoThread(String localPath, String remotePath) {
		this.lPath = localPath;
		this.rPath = remotePath;
	}

	@Override
	public void run() {
		super.run();
		long start = System.currentTimeMillis();

		File file = new File(this.lPath);
		while (true) {
			boolean b = file.exists();
			if (b) {
				Log.e("camera", "file exists!");
				break;
			} else {
				if ((System.currentTimeMillis() - start) < 2 * 60 * 1000) {
					Log.e("camera", "file does not exists!");
					return;
				}
			}
			synchronized (this) {
				try {
					this.wait(500);
				} catch (InterruptedException e) {
				}
			}
		}

		Log.e("camera", "SendVideoThread==run start 1");

		FtpSenderFile sender = new FtpSenderFile("192.168.0.94", 13021);

		try {
			Log.e("camera", "SendVideoThread==run start 2");
			Log.e("camera EXHAUST TIME ", lPath);
			Log.e("camera EXHAUST TIME ", rPath);
			resultStr = sender.send(lPath, rPath);

			Log.e("camera", "SendVideoThread==run start 2");

		} catch (SoftFanFTPException e) {
			Log.e("camera", "SendVideoThread==run start 3");

			e.printStackTrace();
		} catch (IOException e) {
			Log.e("camera", "SendVideoThread==run start 4");

			e.printStackTrace();
		} catch (Exception e) {
			Log.e("camera", "SendVideoThread==run start 5");
		}

		long end = System.currentTimeMillis();
		Log.e("camera EXHAUST TIME ", (end - start) + "");

		if ("传送成功".equals(resultStr)) {
			Log.e("camera EXHAUST TIME ", resultStr);

			// success and delete the video file.
			SelfFile.delFile(lPath);
		} else {
			// save the video if failure.
			File srcFile = SelfFile.createNewFile(SelfFile.generateLocalVideoName());
			File destFile = SelfFile.createNewFile(SelfFile.generateLocalBackupVideoName());
			try {
				SelfFile.copyFile(srcFile, destFile);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}
	}
}
