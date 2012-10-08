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
	public PowerManager.WakeLock wl;
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO 自動生成されたメソッド・スタブ
		super.onStart(intent, startId);
		
		mContext = this;
		counter=intent.getIntExtra("counter", 0);
		if(counter!=0){
		  PowerManager pm=(PowerManager) getSystemService(Context.POWER_SERVICE);
		  wl=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
		      +PowerManager.ON_AFTER_RELEASE, "My Tag");
		  wl.acquire();
		  startTimer();
		}
	}

	@Override
	public void onDestroy() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDestroy();
		
		timer.cancel();
		if(wl.isHeld()) wl.release();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	
	public  void  startTimer(){
		   if(timer!=null) timer.cancel();
		   timer=new Timer();
		   final android.os.Handler handler=new android.os.Handler();
		   timer.schedule(
		    new TimerTask(){
		      @Override
		      public void run() {
		        handler.post(new Runnable(){
		          public void run(){
		            if(counter==-1){
		              timer.cancel();
		              if(wl.isHeld()) wl.release();
		              showAlarm();
		            }else{
		              CountdownTimerActivity.countdown(counter);
		              counter=counter-1;
		            }
		          }
		        });
		      }
		    },0,1000);
		}
	
	void showAlarm(){
		Intent intent = new Intent(mContext, TimerService.class);
		mContext.stopService(intent);
		intent=new Intent(mContext, AlarmDialog.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}
	
		
}
