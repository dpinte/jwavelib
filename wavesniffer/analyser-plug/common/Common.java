package common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
	//public Common(){}
	
	public static int[] integerArrayToIntArray(Integer[] array){
		if(array == null || array.length == 0)
			return new int[0];
		
		int[] tmp = new int[array.length];
		
		for(int i = 0; i < array.length; i++){
			tmp[i] = array[i].intValue();
		}
		
		return tmp;
	}
	
	public static String getCurentDateTime(){
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		DateFormat timeFormat = new SimpleDateFormat("hh':'mm':'ss");
		Date now = new Date();
		
		return dateFormat.format(now) + " " +timeFormat.format(now);
	}
}
