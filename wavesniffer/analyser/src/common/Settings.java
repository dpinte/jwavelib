package common;
import java.awt.Color;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Settings {
	/*public static final String CRCE = "crce_color";
	public static final String TSE = "tse_colo";*/
	public static final String CRIT_ERR_BG = "critE_color_bg";
	public static final String CRIT_ERR_FG = "critE_color_fb";
	//public static final String 
	public static final String WARN_BG = "warn_color_bg";
	public static final String WARN_FG = "warn_color_fg";
	public static final String SEL_BG = "sel_color_bg";
	public static final String SEL_FG = "sel_color_fg";
	public static final String HIG_BG = "higL_color_bg";
	public static final String HIG_FG = "higL_color_fg";
	
	private Properties confFile = new Properties();
	private String confFilePath;
	
	public Settings(String filePath){
		this.confFilePath = filePath;
	}
	
	public void loadSettings(){		
		try {
			this.confFile.load(new FileReader(this.confFilePath));
		} catch (IOException e) {
			System.out.println("Configuration file not found, using default Settings");
			this.setDefault();
		}
	}
	
	public void closeSettings(){
		try {
			this.confFile.store(new FileWriter(this.confFilePath), "WaveSniffer Analyser v 0.1 settings");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setDefault(){
		String osName = System.getProperty("os.name").split(" ")[0];
		if( osName.equals("Windows")){
			//FIXME: check in windows platform
			this.setSerialPort("COM 0");
		} else {		
			this.setSerialPort("/dev/ttyS0");
		}
		this.setBaudrate("115200");
		
		this.setColor(CRIT_ERR_BG, Color.BLACK);
		this.setColor(CRIT_ERR_FG, Color.WHITE);
		this.setColor(WARN_BG, Color.ORANGE);
		this.setColor(WARN_FG, Color.WHITE);
		this.setColor(SEL_BG, Color.BLUE);
		this.setColor(SEL_FG, Color.WHITE);
		this.setColor(HIG_BG, Color.YELLOW);
		this.setColor(HIG_FG, Color.BLACK);
	}
	
	public Color getColor(String key){
		return new Color(Integer.parseInt(this.confFile.getProperty(key)));
	}
	
	public String getSerialPort(){
		return this.confFile.getProperty("Port");
	}
	
	public int getBaudrate(){
		return Integer.parseInt(this.confFile.getProperty("Baud"));
	}
	
	public void setColor(String key, Color color){
		this.confFile.setProperty(key, Integer.toString(color.getRGB()));
	}
	
	public void setSerialPort(String portName){
		this.confFile.setProperty("Port", portName);
	}
	
	public void setBaudrate(String baudrate){
		this.confFile.setProperty("Baud", baudrate);
	}
}
