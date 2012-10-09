package sample.application.countdowntimer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.os.PowerManager;

public class TimerService extends Service {

	Context mContext;
	int counter;
	Timer timer;
	public PowerManager.WakeLock wl;	//ロック画面しないように設定するための変数
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO 自動生成されたメソッド・スタブ
		super.onStart(intent, startId);
		
		this.mContext = this;
		this.counter = intent.getIntExtra("counter", 0);//インテントからデータを取得
		//実行中はロック画面にならないように設定
		if(this.counter != 0){
			PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
			this.wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK+PowerManager.ON_AFTER_RELEASE, "My Tag");
			this.wl.acquire();
			this.startTimer();
		}
	}

	//ストップを押してキャンセルする場合(間接的に呼ばれる？)
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.timer.cancel();	//タイマーのキャンセル
		if(this.wl.isHeld()){
			this.wl.release();	//ロック画面にしない設定を解除
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	//カウントダウン(スレッド)
	public  void  startTimer(){
		if(this.timer != null) this.timer.cancel();
		this.timer = new Timer();
		final android.os.Handler handler = new android.os.Handler();//ハンドラー
		
		//スケジュール(一定時間ごとにスレッドを実行できる)
		this.timer.schedule(new TimerTask(){
			
			//スレッドの実装
			@Override
			public void run() {
				handler.post(new Runnable(){	//ハンドラーポスト
					public void run(){
						//カウントダウン終了したとき
						if(counter == -1){
							timer.cancel();
							if(wl.isHeld()){
								wl.release();
							}
							showAlarm();
						//カウントダウン中
						}else{
							CountdownTimerActivity.countdown(counter);
							counter = counter-1;
						}
					}
				});
			}
			
		},0,1000);
	}
	
	//カウントダウンが終わったとき
	void showAlarm(){
		//TimerServiceクラスのインテント終了
		Intent intent = new Intent(this.mContext, TimerService.class);
		this.mContext.stopService(intent);
		//AlarmDialogクラスのインテント
		intent = new Intent(this.mContext, AlarmDialog.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.mContext.startActivity(intent);
	}

}
