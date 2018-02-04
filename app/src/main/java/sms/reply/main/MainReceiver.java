package sms.reply.main;

import sms.reply.constant.IntentKey;
import sms.reply.constant.TaskCode;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MainReceiver extends BroadcastReceiver {
    public static final String BOOT_ACTION="android.intent.action.BOOT_COMPLETED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent!=null && intent.getAction()!=null && 
		   intent.getAction().equalsIgnoreCase(MainReceiver.BOOT_ACTION)){
			
			SharedPreferences prefs=context.getSharedPreferences("sms.reply.main_preferences", 0);
			if(!prefs.getBoolean("auto_reply", false)) return;
			
			Intent i = new Intent(context, MainService.class);
			i.putExtra(IntentKey.TASK, TaskCode.TASK_START_OBSERVER);
			context.startService(i);
		}
	}//end event onReceive

}
