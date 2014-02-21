package common;

import java.util.Arrays;

import com.dipole.libs.Functions;

public class ParameterList {

	public static final int AWAKENING_PERIOD = 0x00;
	public static final int WAKEUP_TYPE = 0x01;
	public static final int WAKEUP_LENGTH = 0x02;
	public static final int WAVECARD_POLLING_GROUP = 0x03;
	public static final int RADIO_ACKNOWLEGDE = 0x04;
	public static final int RADIO_ADDRESS = 0x05;
	public static final int RELAY_ROUTE_STATUS = 0x06; 
	public static final int RELAY_ROUTE = 0x07;
	public static final int POLLING_ROUTE = 0x08;
	public static final int GROUP_NUMBER = 0x09;
	public static final int POLLING_TIME = 0x0A;
	public static final int RADIO_USER_TIMEOUT = 0x0C;
	public static final int EXCHANGE_STATUS = 0x0E;
	public static final int SWITCH_MODE_SATUS = 0x10;
	public static final int WAVECARD_MULTICAST_GROUP = 0x16;
	public static final int BCST_RECEPTION_TIMEOUT = 0x17;
	
	public ParameterList() {}
	
	/**
	 * 
	 * @param paramNum parameter number
	 * @param paramVal integer array with parameter data
	 * @return
	 */
	public static String parseParameterValue(int paramNum, int[] paramVal){
		if(paramVal == null || paramVal.length == 0)
			return "N/A";
			
		String value = null;
		
		switch(paramNum){
			case AWAKENING_PERIOD:
				if(paramVal[0] == 0)
					value = "20ms";
				else
					value = Integer.toString(paramVal[0] * 100) +"ms";
				break;
				
			case WAKEUP_TYPE:
				if(paramVal[0] == 1)
					value = "Short wake-up: 50ms";
				else
					value = "Long wake-up";
				break;
				
			case WAKEUP_LENGTH:
				int tmp = paramVal[1] << 8 | paramVal[0];
				value = Integer.toString(tmp);
				break;
				
			case WAVECARD_POLLING_GROUP:
			case GROUP_NUMBER:
				value = Integer.toString(paramVal[0]) +
						" ("+ Functions.printHumanHex(paramVal[0], true) +")";
				break;
				
			case RADIO_ACKNOWLEGDE:
				if(paramVal[0] == 1)
					value = "No acknowledgment";
				else
					value = "With acknowledgment";
				break;
				
			case RADIO_ADDRESS:
				value = Functions.printHumanHex(paramVal, false);
				break;
				
			case RELAY_ROUTE_STATUS:
				if(paramVal[0] == 1)
					value = "Relay route trasmition activated";
				else
					value = "Relay route trasmition desactivated";
				break;
				
			case RELAY_ROUTE:
				int nbRep = paramVal[0];
				int[] copy;
				
				if(nbRep != 0){	
					value = "";
					int from = 1;
					for(int i = 0; i < nbRep; i++){
						copy = Arrays.copyOfRange(paramVal, from, from + 6);
						value += Functions.printHumanHex(copy, false) +",";
						from += 6;
					}
				} else {
					value = "No repeater";
				}
				break;
				
			case POLLING_ROUTE:
				int nbRepPol = paramVal[0];
				int[] copyPol;
				
				if(nbRepPol != 0){	
					value = "";
					int from = 1;
					for(int i = 0; i < nbRepPol; i++){
						copyPol = Arrays.copyOfRange(paramVal, from, from + 6);
						value += Functions.printHumanHex(copyPol, false) +",";
						from += 6;
					}
				} else {
					value = "No modules to poll";
				}
				break;
				
			case POLLING_TIME:				
			case RADIO_USER_TIMEOUT:
			case BCST_RECEPTION_TIMEOUT:
				value = Integer.toString(paramVal[0] * 100) +"ms";
				break;
				
			case EXCHANGE_STATUS:
				switch(paramVal[0]){
					case 0:
						value = "Status and error frames desactivated";
						break;
					case 1:
						value = "Error frame activated";
						break;
					case 2:
						value = "Status frame activated";
						break;
					case 3:
						value = "Both status and error frames activated";
						break;
				}
				break;
				
			case SWITCH_MODE_SATUS:
				if(paramVal[0] == 1)
					value = "Automatic selection activated";
				else
					value = "Automatic selection desactivated";
				break;
				
			case WAVECARD_MULTICAST_GROUP:
				if(paramVal[0] == 0xFF)
					value = "No group selected (0xFF)";
				else
					value = Integer.toString(paramVal[0]) +
							" ("+ Functions.printHumanHex(paramVal[0], true) +")";
				break;				
			default:
				value = "Unknow parameter";
		}
		
		return value;
	}
	
	/**
	 * Return the Name of the parameter as it's written in the WaveCard User Manual
	 * @param param The parameter Number
	 * @return the name of the parameter
	 */
	public static String getParameterName(int paramNum){
		String name = null;
		switch(paramNum){
			case AWAKENING_PERIOD:
				name = "AWAKENING_PERIOD";
				break;
			case WAKEUP_TYPE:
				name = "WAKEUP_TYPE";
				break;
			case WAKEUP_LENGTH:
				name = "WAKEUP_LENGTH";
				break;
			case WAVECARD_POLLING_GROUP:
				name = "WAVECARD_POLLING_GROUP";
				break;
			case RADIO_ACKNOWLEGDE:
				name = "RADIO_ACKNOWLEGDE";
				break;
			case RADIO_ADDRESS:
				name = "RADIO_ADDRESS";
				break;
			case RELAY_ROUTE_STATUS:
				name = "RELAY_ROUTE_STATUS";
				break;
			case RELAY_ROUTE:
				name = "RELAY_ROUTE";
				break;
			case POLLING_ROUTE:
				name = "POLLING_ROUTE";
				break;
			case GROUP_NUMBER:
				name = "GROUP_NUMBER";
				break;
			case POLLING_TIME:
				name = "POLLING_TIME";
				break;
			case RADIO_USER_TIMEOUT:
				name = "RADIO_USER_TIMEOUT";
				break;
			case EXCHANGE_STATUS:
				name = "EXCHANGE_STATUS";
				break;
			case SWITCH_MODE_SATUS:
				name = "SWITCH_MODE_SATUS";
				break;
			case WAVECARD_MULTICAST_GROUP:
				name = "WAVECARD_MULTICAST_GROUP";
				break;
			case BCST_RECEPTION_TIMEOUT:
				name = "BCST_RECEPTION_TIMEOUT";
				break;
			default:
				name = "Unknow parameter";
		}
		return name;
	}
}
