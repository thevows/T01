package com.hd.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DHFile {
	public static String ErrInfo="";

	public static boolean PutContent(String fName,String content){
		File f=new File(android.os.Environment.getExternalStorageDirectory(),fName);
		File fParent=new File(f.getParent());
		if(!fParent.exists() && !fParent.mkdirs()){
			DHFile.SetLastError("父路径创建失败");
			return false;
		}

		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				DHFile.SetLastError("文件不存在,并且创建失败");
				return false;
			}
		}

		if(!f.isFile()){
			DHFile.SetLastError("传入的文件信息["+fName+"]不是文件类型");
			return false;
		}

		try {
			FileOutputStream oStream=new FileOutputStream(f);
			oStream.write(content.getBytes());
			oStream.flush();
			oStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			DHFile.SetLastError("文件不存在");
			return false;
		} catch (Exception e){
			e.printStackTrace();
			DHFile.SetLastError("写入文件发生其它异常");
			return false;
		}

		return true;
	}//end function PutContent

	public static String GetContent(String fName){
		File f=new File(android.os.Environment.getExternalStorageDirectory(),fName);
		if(!f.exists() || !f.isFile() || f.length()<=0) return "";

		String cnt=null;
		try {
			FileInputStream iStream = new FileInputStream(f);
			byte[] buf=new byte[(int)f.length()];
			iStream.read(buf, 0, (int)f.length());
			cnt=new String(buf);
			iStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}

		return cnt==null?"":cnt;
	}//end function GetContent

	public static String GetLastError(){
		return DHFile.ErrInfo;
	}//end function GetLastError

	public static void SetLastError(String errInfo){
		if(errInfo==null) return;
		DHFile.ErrInfo=errInfo;
	}//end function SetLastError
}
