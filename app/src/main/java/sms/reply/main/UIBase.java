package sms.reply.main;

import sms.reply.utils.SqlHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hd.io.file.DHFile;

public abstract class UIBase extends Activity {
	public static final int MENU_CONTACT_ME=1;
	public static final int MENU_CLEAR_SMS_REC=2;
	public static String ReplyContent=null;
	public static String REPLY_FILE_NAME="dxd/reply/reply.txt";

	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			UIBase.this.HandleMsg(msg);
			super.handleMessage(msg);
		}
	};//end member mHandler

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(!android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())){
			this.ShortHint(this.getString(R.string.no_sd_card));
		}
		super.onCreate(savedInstanceState);
	}//end event onCreate

	@Override
	protected void onResume() {
		this.Init();
		this.SetEvents();
		super.onResume();
	}//end event onResume

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, UIBase.MENU_CONTACT_ME, 1, this.getString(R.string.plz_contact_me));
		menu.add(0,UIBase.MENU_CLEAR_SMS_REC,2,this.getString(R.string.clear_sms_rec));
		return super.onCreateOptionsMenu(menu);
	}//end event onCreate OptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
			case UIBase.MENU_CONTACT_ME:
				Toast.makeText(this,
						R.string.contact_me,
						Toast.LENGTH_LONG).show();
				break;
			case UIBase.MENU_CLEAR_SMS_REC:
				this.Alert("清空记录",
						"确定要清空回复记录?",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								SqlHelper helper=new SqlHelper(UIBase.this);
								helper.ClearAllRec();
								helper.Destory();
								Toast.makeText(UIBase.this, "记录已全部清空", Toast.LENGTH_SHORT).show();
							}
						});
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}//end event onOptionsItemSelected

	public void Alert(String title,String content,DialogInterface.OnClickListener listener){
		AlertDialog d=new AlertDialog.Builder(this).create();
		d.setTitle(title);
		d.setMessage(content);

		if(listener!=null){
			d.setButton(Dialog.BUTTON_POSITIVE, this.getText(R.string.confirm), listener);
		}
		d.setButton(Dialog.BUTTON_NEGATIVE,
				this.getText(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});

		d.show();
	}//end function Alert

	public void ShortHint(String info){
		Toast.makeText(this, info, Toast.LENGTH_LONG).show();
	}//end function ShortHint

	protected void HandleMsg(Message msg){}//end function Handle

	protected void SendMsgToHandler(Message msg){
		this.mHandler.sendMessage(msg);
	}//end function SendMsgToHandler

	/********** static function **********/
	public static void ChangeReplyContent(){
		UIBase.ReplyContent=DHFile.GetContent(UIBase.REPLY_FILE_NAME);
	}//end function ChangeReplyContent

	public static void ChangeReplyContent(String cnt){
		UIBase.ReplyContent=cnt;
	}//end function ChangeReplyContent

	public static String GetReplyContent(){
		if(UIBase.ReplyContent==null || UIBase.ReplyContent.equals("")){
			UIBase.ChangeReplyContent();
		}
		return UIBase.ReplyContent;
	}//end function GetReplyContent

	/********** astrack function **********/
	abstract protected void Init();
	abstract protected void SetEvents();

}
