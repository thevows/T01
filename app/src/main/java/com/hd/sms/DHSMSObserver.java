package com.hd.sms;

import sms.reply.constant.IntentKey;
import sms.reply.main.SMSSendService;
import sms.reply.utils.SqlHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.hd.numeric.NumberConvert;

public class DHSMSObserver extends ContentObserver {
	private Context context=null;
	public static boolean isObserverReg=false;
	private long lastSmsDate=0;

	public DHSMSObserver(Handler handler) {
		super(handler);

		this.lastSmsDate=System.currentTimeMillis();
	}//end constructor DHSMSObserver

	public DHSMSObserver(Handler handler,Context c){
		super(handler);

		this.lastSmsDate=System.currentTimeMillis();
		this.context=c;
	}//end function

	@Override
	public void onChange(boolean selfChange) {
		if(this.context==null) return;
		DHSMS oSms=new DHSMS(DHSMS.TYPE_RECEIVE,this.context);
		oSms.FetchSMSFromDb(1);
		if(oSms.GetSMSList().isEmpty()) return;
		if(this.lastSmsDate>=oSms.GetSMSList().get(0).date) return;
		if(oSms.GetSMSList().get(0).address==null) return;
		if(oSms.GetSMSList().get(0).address.length()!=11) return;    //不是普通的手机号
		if(oSms.GetSMSList().get(0).type!=1) return;    //不是inbox里的短信

		SqlHelper helper=new SqlHelper(this.context);
		boolean isSent=helper.IsAddressExist(oSms.GetSMSList().get(0).address);
		helper.Destory();
		if(isSent) return;    //已经发送过了

		this.lastSmsDate=oSms.GetSMSList().get(0).date;

		if(this.JudgeBody(oSms.GetSMSList().get(0).body)){
			Intent intent=new Intent(this.context,SMSSendService.class);
			intent.putExtra(IntentKey.SMS_ADDRESS, oSms.GetSMSList().get(0).address);
			this.context.startService(intent);
		}

		super.onChange(selfChange);
	}//end event onChange

	public void Register(){
		if(this.context==null) return;
		if(DHSMSObserver.isObserverReg) return;

		this.context.getContentResolver().registerContentObserver(
				Uri.parse("content://sms/"),
				true,
				this
		);
		DHSMSObserver.isObserverReg=true;
	}//end function Register

	public void UnRegister(){
		if(this.context==null) return;
		if(!DHSMSObserver.isObserverReg) return;

		this.context.getContentResolver().unregisterContentObserver(this);
		DHSMSObserver.isObserverReg=false;
	}//end function UnRegister

	public boolean JudgeBody(String body){
		if(this.context==null) return false;

		SharedPreferences prefs=this.context.getSharedPreferences("sms.reply.main_preferences", 0);
		if(!prefs.getBoolean("auto_reply", false)) return false;

		//判断body是否为拜年短信
		String strKeys=prefs.getString("auto_reply_keys", "");
		String strThrshold=prefs.getString("check_threshold", "");
		int thrshold=NumberConvert.ConvertToInt(strThrshold);
		if(strKeys.equals("")){    //如果没有设置关键字,则内置
			strKeys="新,年,春,喜,祝,拜,康,乐,安,蛇,意,顺,欢,瑞,功,吉,运,祥,旺,财," +
					"亨通,福,富,贵,鸿";
		}
		if(thrshold<=0) thrshold=3;
		if(thrshold>10) thrshold=10;
		String[] arrKeys=strKeys.split(",");

		int checkNum=0;
		for(int i=0;i<arrKeys.length;i++){
			if(checkNum>=thrshold) break;

			if(body.contains(arrKeys[i])){
				checkNum++;
			}
		}

		return checkNum>=thrshold;
	}//end function JudgeBody
}
