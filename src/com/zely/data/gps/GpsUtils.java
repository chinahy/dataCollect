package com.zely.data.gps;

public class GpsUtils {
	 private static double EARTH_RADIUS = 6371.393;  
     private static double rad(double d)  
     {  
        return d * Math.PI / 180.0;  
     }  
	/**
	 *  传入两个坐标用逗号隔开
	 * @param current
	 * @param next
	 * @return
	 */
    public static double getDistance(String current, String next)  
    {  
       String [] curArray=current.split(",");
       String [] nextArray=next.split(",");
       double radLat1 = rad(Double.parseDouble(curArray[1]));  
       double radLat2 = rad(Double.parseDouble(nextArray[1]));  
       double a = radLat1 - radLat2;      
       double b = rad(Double.parseDouble(curArray[0])) - rad(Double.parseDouble(nextArray[0]));  
       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +   
        Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));  
       s = s * EARTH_RADIUS;  
       s = Math.round(s * 1000);
       return s;  
    }  
    
	 public static void main(String[] args) {  
		 //[104.0659883,30.5503666, 
		 String str="104.0659883,30.5503666";
		 String str1="104.0666033,30.5503083";
	        System.out.println(getDistance(str,str1));  
	    }  
}
