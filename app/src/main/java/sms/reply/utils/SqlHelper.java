package sms.reply.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

public class SqlHelper {
	private static final String DB_PATH=Environment.getExternalStorageDirectory().getAbsolutePath()+
			"/dxd/reply/reply.db";
	private static final String DB_NAME="reply.db";

	private Context context=null;
	private SQLiteDatabase mDb=null;

	public SqlHelper(Context context){
		this.context=context;

		File fDb=new File(SqlHelper.DB_PATH);
		File pDir=new File(fDb.getParent());

		if(!pDir.exists()) pDir.mkdirs();
		if(!fDb.exists()){    //db不存在
			this.mDb=SQLiteDatabase.openDatabase(
					SqlHelper.DB_PATH,
					null,
					SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.OPEN_READWRITE);
			this.mDb.execSQL(
					"CREATE TABLE sms_rec(" +
							"id INTEGER PRIMARY KEY AUTOINCREMENT," +
							"address VARCHAR(20) UNIQUE)");

		}

		this.InitDb();
	}//end constructor SqlHelper

	public boolean IsAddressExist(String address){
		if(!this.IsDbExists()) return false;
		if(this.mDb==null) return false;
		if(address==null) return false;

		boolean isFind=false;
		Cursor c=this.mDb.rawQuery(
				"SELECT address FROM sms_rec WHERE address='"+address+"'",
				null
		);
		isFind=c.moveToFirst();
		c.close();

		return isFind;
	}//end function IsAddressExist

	public void InsertAddress(String address){
		if(!this.IsDbExists()) return;
		if(this.mDb==null) return;
		if(address==null) return;

		if(address.length()>20) address=address.substring(0, 19);

		this.mDb.execSQL("REPLACE INTO sms_rec (address) VALUES ('"+address+"')");
	}//end function InsertAddress

	public void ClearAllRec(){
		if(!this.IsDbExists()) return;
		if(this.mDb==null) return;

		this.mDb.execSQL("DELETE FROM sms_rec");
	}//end function ClearAllRec

	public void Destory(){
		if(this.mDb==null) return;
		this.mDb.close();
		this.mDb=null;
	}//end function Destroy

	private void InitDb(){
		if(!this.IsDbExists()) return;
		if(this.mDb!=null) return;
		this.mDb=SQLiteDatabase.openDatabase(SqlHelper.DB_PATH,
				null,
				SQLiteDatabase.OPEN_READWRITE);
	}//end function InitDb

	private boolean IsDbExists(){
		File fDb=new File(SqlHelper.DB_PATH);
		boolean b=fDb.exists();
		if(!b){
			Toast.makeText(this.context, "数据库不存在", Toast.LENGTH_SHORT).show();
		}
		return b;
	}//end function isDbExists
}
