package sms.reply.main;

import sms.reply.constant.IntentKey;
import sms.reply.constant.TaskCode;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.hd.sms.DHSMSObserver;

public class MainService extends Service {
    public static DHSMSObserver smsObserver=null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}//end event onCreate

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int taskId=intent.getIntExtra(IntentKey.TASK, 0);
		
		switch (taskId) {
		    case TaskCode.TASK_START_OBSERVER:
			    this.ProcSmsObserver(true);
			    break;
		    case TaskCode.TASK_STOP_OBSERVER:
			    this.ProcSmsObserver(false);
			    break;
		    default:
			    break;
		}
		
		
		
		return super.onStartCommand(intent, flags, startId);
	}//end event onStartCommand

	private void ProcSmsObserver(boolean isStart){
		if(isStart){
			if(MainService.smsObserver==null){
				MainService.smsObserver=new DHSMSObserver(new Handler(),this);
			}
			MainService.smsObserver.Register();
			Toast.makeText(this, "短信监视器已运行", Toast.LENGTH_SHORT).show();
		}else{
			if(MainService.smsObserver==null) return;
			MainService.smsObserver.UnRegister();
			MainService.smsObserver=null;
			Toast.makeText(this, "短信监视器已终止", Toast.LENGTH_SHORT).show();
		}
		
	}//end function ProcSmsObserver
	
}
