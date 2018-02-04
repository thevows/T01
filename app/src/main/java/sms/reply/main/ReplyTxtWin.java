package sms.reply.main;

import com.hd.io.file.DHFile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ReplyTxtWin extends UIBase {
    private EditText mEt=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.reply_txt);
	}//end event onCreate

	@Override
	protected void Init() {
		this.mEt=(EditText)this.findViewById(R.id.et_cnt);
		String sCnt=DHFile.GetContent(UIBase.REPLY_FILE_NAME);
		if(sCnt!=null && !sCnt.equals("")){
			this.mEt.setText(sCnt);
			this.mEt.setSelection(this.mEt.getText().length());
		}
	}//end function Init

	@Override
	protected void SetEvents() {
		Button btnClear=(Button)this.findViewById(R.id.btn_clear);
		btnClear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ReplyTxtWin.this.Alert(
						ReplyTxtWin.this.getText(R.string.confirm_clear).toString(), 
						ReplyTxtWin.this.getText(R.string.confirm_clear).toString(), 
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ReplyTxtWin.this.mEt.setText("");
							}
						});
				
			}
		});//end btnClear click
		
		Button btnSave=(Button)this.findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if(ReplyTxtWin.this.mEt.getText().toString().length()<=0){
					ReplyTxtWin.this.ShortHint("您还未输入回复信息");
					return;
				}
				if(ReplyTxtWin.this.mEt.getText().toString().length()>70){
					ReplyTxtWin.this.ShortHint("回复的信息不得大于70个字");
					return;
				}

				if(DHFile.PutContent(UIBase.REPLY_FILE_NAME,
						ReplyTxtWin.this.mEt.getText().toString())){
					ReplyTxtWin.this.ShortHint("信息保存成功");
					UIBase.ChangeReplyContent(ReplyTxtWin.this.mEt.getText().toString());
				}else{
					ReplyTxtWin.this.ShortHint("信息保存失败");
				}
				
			}
		});//end btnSave click
	}//end function SetEvents

}
