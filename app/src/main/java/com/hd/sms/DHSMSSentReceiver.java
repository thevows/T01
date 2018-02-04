package com.hd.sms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

public class DHSMSSentReceiver extends BroadcastReceiver {
	public static final String ACTION_SMS_SENT="ACTION_SMS_SENT";
	public static DHSMSSentReceiver SENT_RECEIVER=null;
	public static boolean IS_REG=false;

	@Override
	public void onReceive(Context context, Intent intent) {
		if(this.getResultCode()==Activity.RESULT_OK){
			Toast.makeText(context, "短信回复成功", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(context, "短信回复失败", Toast.LENGTH_SHORT).show();
		}

	}//end event onReceive

	public static void RegisterSmsSentReceiver(Context context){
		if(DHSMSSentReceiver.IS_REG) return;

		if(DHSMSSentReceiver.SENT_RECEIVER==null){
			DHSMSSentReceiver.SENT_RECEIVER=new DHSMSSentReceiver();
		}

		context.registerReceiver(DHSMSSentReceiver.SENT_RECEIVER,
				new IntentFilter(DHSMSSentReceiver.ACTION_SMS_SENT));
		DHSMSSentReceiver.IS_REG=true;
	}//end static function RegisterSmsSentReceiver

	public static void UnRegisterSmsSentReceiver(Context context){
		if(!DHSMSSentReceiver.IS_REG) return;
		if(DHSMSSentReceiver.SENT_RECEIVER==null) return;

		context.unregisterReceiver(DHSMSSentReceiver.SENT_RECEIVER);
		DHSMSSentReceiver.IS_REG=false;
	}//end static function UnRegisterSmsSentRegister
}
