package sms.reply.main;

import sms.reply.constant.IntentKey;
import sms.reply.constant.TaskCode;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class PrefWin extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.pref);
		
	}//end event onCreate

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if(preference.getKey().equals("auto_reply")){
			SharedPreferences pref=this.getSharedPreferences("sms.reply.main_preferences", 0);
			if(pref.getBoolean("auto_reply", false)){
				Intent intent=new Intent(this,MainService.class);
				intent.putExtra(IntentKey.TASK, TaskCode.TASK_START_OBSERVER);
				this.startService(intent);
			}else{
				Intent intent=new Intent(this,MainService.class);
				intent.putExtra(IntentKey.TASK, TaskCode.TASK_STOP_OBSERVER);
				this.startService(intent);
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}//end event onPeferenceTreeClick
}
