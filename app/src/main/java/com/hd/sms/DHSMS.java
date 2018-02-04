package com.hd.sms;

import java.util.ArrayList;
import java.util.HashMap;

import sms.reply.utils.SqlHelper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class DHSMS {
	public static final int TYPE_ALL=0;
	public static final int TYPE_SENT=1;
	public static final int TYPE_RECEIVE=2;

	public static final String URI_SMS_ALL="content://sms/";
	public static final String URI_SMS_SENT="content://sms/sent/";
	public static final String URI_SMS_RECEIVE="content://sms/inbox/";

	private String mUri=DHSMS.URI_SMS_ALL;
	private Context mContext=null;
	private ArrayList<DHSMS.SMSContent> mContentList=new ArrayList<DHSMS.SMSContent>(30);
	private HashMap<String,String> mHashPhones=new HashMap<String,String>();

	public DHSMS(int t,Context win){
		switch(t){
			case DHSMS.TYPE_ALL:
				this.mUri=DHSMS.URI_SMS_ALL;
				break;
			case DHSMS.TYPE_SENT:
				this.mUri=DHSMS.URI_SMS_SENT;
				break;
			case DHSMS.TYPE_RECEIVE:
				this.mUri=DHSMS.URI_SMS_RECEIVE;
				break;
			default:
				this.mUri=DHSMS.URI_SMS_ALL;
				break;
		}
		this.mContext=win;
	}//end constructor DHSMS

	public ArrayList<DHSMS.SMSContent> GetSMSList(){
		return this.mContentList;
	}//end function GetSMSList

	public void FetchSMSFromDb(int limit){    //需预防卡死主线程
		this.mContentList.clear();
		SqlHelper helper=new SqlHelper(this.mContext);

		String[] projection={
				"_id",
				"address",
				"body",
				"date",
				"type"
		};
		Cursor c=this.mContext.getContentResolver().query(
				Uri.parse(this.mUri),
				projection,
				null,
				null,
				"_id DESC"+" LIMIT "+(limit>0?limit:20));

		if(c.moveToFirst()){
			int idIdx=c.getColumnIndex("_id");
			int addressIdx=c.getColumnIndex("address");
			int bodyIdx=c.getColumnIndex("body");
			int dateIdx=c.getColumnIndex("date");
			int typeIdx=c.getColumnIndex("type");

			do{
				SMSContent oSmsCnt=new SMSContent();
				oSmsCnt._id=c.getInt(idIdx);
				oSmsCnt.address=c.getString(addressIdx).replace("+86", "");
				oSmsCnt.body=c.getString(bodyIdx);
				oSmsCnt.person=this.GetPersonByAddress(oSmsCnt.address);
				oSmsCnt.date=c.getLong(dateIdx);
				oSmsCnt.type=c.getInt(typeIdx);
				oSmsCnt.isReply=helper.IsAddressExist(oSmsCnt.address);

				if(oSmsCnt.body.length()>20){
					oSmsCnt.body=oSmsCnt.body.substring(0,19)+"...";
				}

				this.mContentList.add(oSmsCnt);
			}while(c.moveToNext());
		}
		c.close();
		helper.Destory();
	}//end function FetchSMSFromDb

	public void FetchSMSFromDb(){
		this.FetchSMSFromDb(20);
	}//end function FetchSMSFraomDb

	private String GetPersonByAddress(String address){
		if(!this.mHashPhones.containsKey(address)){
			this.mHashPhones.put(address, "查无此人");

			String[] projection={
					ContactsContract.PhoneLookup.DISPLAY_NAME,
					ContactsContract.CommonDataKinds.Phone.NUMBER
			};

			Cursor c=this.mContext.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					projection,
					ContactsContract.CommonDataKinds.Phone.NUMBER+"='"+address+"'",
					null,
					"_id ASC LIMIT 1"
			);

			if(c.moveToFirst()){
				int nameIdx=c.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
				this.mHashPhones.put(address, c.getString(nameIdx));
			}
			c.close();
		}

		return this.mHashPhones.get(address);
	}//end function GetPersonByAddress

	public class SMSContent{
		public int _id;
		public String address;
		public String body;
		public String person;
		public long date;
		public boolean isReply;
		public int type;
	}//end inner classSmsContent
}
