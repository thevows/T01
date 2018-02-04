package sms.reply.main;

import java.util.ArrayList;

import sms.reply.constant.IntentKey;
import sms.reply.constant.TaskCode;
import sms.reply.constant.TaskStatus;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hd.sms.DHSMS;

public class SMSAutoReplyActivity extends UIBase {
	private DHSMS mSmsBox=new DHSMS(DHSMS.TYPE_RECEIVE, this);
	private DHSMS.SMSContent mSms=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
	}//end event onCreate

	@Override
	protected void Init() {
		new Thread(new TaskFetchSMS()).start();
	}//end function Init

	@Override
	protected void HandleMsg(Message msg) {
		super.HandleMsg(msg);

		if(msg.arg2!=TaskStatus.TASK_FINISHED) return;
		switch(msg.arg1){
			case TaskCode.TASK_FETCH_SMS:
				SmsAdapter adapter=new SmsAdapter(this.mSmsBox.GetSMSList());
				ListView lvSms=(ListView)this.findViewById(R.id.lst_sms);
				lvSms.setAdapter(adapter);

				break;
			default:
				break;
		}
	}//end function HandleMsg

	@Override
	protected void SetEvents() {
		Button btnQuickSend=(Button)this.findViewById(R.id.btn_refresh);
		btnQuickSend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SMSAutoReplyActivity.this.ShortHint(SMSAutoReplyActivity.this.getString(R.string.sms_refreshing));
				SMSAutoReplyActivity.this.Init();
			}
		});//end btnRefresh click


		Button btnAutoSettings=(Button)this.findViewById(R.id.btn_auto_settings);
		btnAutoSettings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(SMSAutoReplyActivity.this,PrefWin.class);
				SMSAutoReplyActivity.this.startActivity(intent);
			}
		});//end btnAutoSettings click

		Button btnTxtSettings=(Button)this.findViewById(R.id.btn_txt_settings);
		btnTxtSettings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(SMSAutoReplyActivity.this,ReplyTxtWin.class);
				SMSAutoReplyActivity.this.startActivity(intent);
			}
		});//end btnTxtSettings click

		ListView lvSms=(ListView)this.findViewById(R.id.lst_sms);
		lvSms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				SMSAutoReplyActivity.this.mSms=(DHSMS.SMSContent)arg0.getAdapter().getItem(arg2);
				if(SMSAutoReplyActivity.this.mSms==null){
					SMSAutoReplyActivity.this.ShortHint("短信信息获取失败");
					return;
				}

				SMSAutoReplyActivity.this.Alert("确认快捷回复",
						"确认给"+SMSAutoReplyActivity.this.mSms.address+"("+SMSAutoReplyActivity.this.mSms.person+")进行快捷回复?",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								Intent intent=new Intent(SMSAutoReplyActivity.this,SMSSendService.class);
								intent.putExtra(IntentKey.SMS_ADDRESS, SMSAutoReplyActivity.this.mSms.address);
								SMSAutoReplyActivity.this.startService(intent);
							}

						});

			}
		});//end lsSms item click


	}//end function SetEvent


	private class TaskFetchSMS implements Runnable{
		public void run() {
			SMSAutoReplyActivity.this.mSmsBox.FetchSMSFromDb();
			Message msg=Message.obtain();
			msg.arg1=TaskCode.TASK_FETCH_SMS;
			msg.arg2=TaskStatus.TASK_FINISHED;
			SMSAutoReplyActivity.this.SendMsgToHandler(msg);
		}
	}//end inner class TaskFetchSMS

	public class SmsAdapter extends BaseAdapter{
		public ArrayList<DHSMS.SMSContent> mList=null;
		public SmsAdapter(ArrayList<DHSMS.SMSContent> l){
			this.mList=l;
		}

		public int getCount() {
			return this.mList.size();
		}

		public Object getItem(int position) {
			return this.mList.get(position);
		}

		public long getItemId(int position) {
			return this.mList.get(position)._id;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView=View.inflate(SMSAutoReplyActivity.this,
						R.layout.sms_row,
						null);
			}
			View v=convertView;

			TextView tvAddress=(TextView)v.findViewById(R.id.tv_address);
			String txtTmp=this.mList.get(position).address+
					" ("+this.mList.get(position).person+")";
			if(this.mList.get(position).isReply) txtTmp+="--已回复";

			tvAddress.setText(txtTmp);

			TextView tvBody=(TextView)v.findViewById(R.id.tv_body);
			tvBody.setText(this.mList.get(position).body);

			return v;
		}
	}//end inner class SMSAdpater
}