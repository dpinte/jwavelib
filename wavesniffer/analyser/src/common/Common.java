package common;

public class Common {
	public Common(){}
	
	public static int[] integerArrayToIntArray(Integer[] array){
		if(array == null || array.length == 0)
			return new int[0];
		
		int[] tmp = new int[array.length];
		
		for(int i = 0; i < array.length; i++){
			tmp[i] = array[i].intValue();
		}
		
		return tmp;
	}
}
