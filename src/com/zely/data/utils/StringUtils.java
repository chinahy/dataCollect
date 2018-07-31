package com.zely.data.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {
	public static String date() {
		SimpleDateFormat sdfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = sdfm.format(new Date());
		return str;
	}
	public static String toDecimal(Double b){
		DecimalFormat    df   = new DecimalFormat("######0.00");   
		return  df.format(b);
	}
}
