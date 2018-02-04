package com.hd.numeric;

public class NumberConvert {
    public static int ConvertToInt(String s){
    	int rtl=0;
    	if(s==null) return rtl;
    	
    	try{
    		rtl=Integer.parseInt(s);
    	}catch(NumberFormatException e){
    		rtl=0;
    	}
    	
    	return rtl;
    }//end function ConvertToInt
    
    public static float ConvertToFloat(String s){
    	float f=0f;
    	if(s==null) return f;
    	
    	try{
    		f=Float.parseFloat(s);
    	}catch(NumberFormatException e){
    		f=0f;
    	}
    	
    	return f;
    }//end function ConvertToFloat
}
