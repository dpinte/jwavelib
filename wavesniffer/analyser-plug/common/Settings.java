package common;

import java.awt.Color;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

// FIXME: /!\ implement checks for confFilePath and load
public class Settings {

	public static final String CRIT_ERR_BG = "critE_color_bg";
	public static final String CRIT_ERR_FG = "critE_color_fb";
	public static final String WARN_BG = "warn_color_bg";
	public static final String WARN_FG = "warn_color_fg";
	public static final String SEL_BG = "sel_color_bg";
	public static final String SEL_FG = "sel_color_fg";
	public static final String HIG_BG = "higL_color_bg";
	public static final String HIG_FG = "higL_color_fg";
	
	public static final String SPORT_NAME = "sport_name";
	public static final String SPORT_BAUD = "sport_baud";
	
	public static final String H_SIZE = "heigth";
	public static final String W_SIZE = "width";
	public static final String VIEW_MODE = "view_mod";
	public static final int VM_TABS = 1;
	public static final int VM_IFRAME = 2;
	
	private static Settings instance;
	
	private Properties confFile = new Properties();
	private String confFilePath;
	private boolean isLoaded = false; 
	
	private Settings() {}
	
	// FIXME: use a static init  (private static Settings instance = new Settings() )??
	public static synchronized Settings getInstance() {
		if(instance == null) {
			instance = new Settings();
		}
		
		return instance;
	}
	
	/*
	public Settings(String filePath){
		this.confFilePath = filePath;
	}
	*/
	
	public void loadSettings(){	
		if(!this.isLoaded) {
			try {
				this.confFile.load(new FileReader(this.confFilePath));
			} catch (IOException e) {
				Log.info("Configuration file not found, using default Settings");
				this.setDefault();
			}
		
			this.isLoaded = true;
		}
	}
	
	public void closeSettings(){
		try {
			String comment = gui.AboutDialog.NAME +" "+ gui.AboutDialog.VERSION;
			this.confFile.store(new FileWriter(this.confFilePath), comment);
		} catch (IOException e) {
			Log.fatal(e.getMessage());
		}
	}
	
	private void setDefault(){
		String osName = System.getProperty("os.name").split(" ")[0];
		if( osName.equals("Windows")){
			//FIXME: check in windows platform
			this.setString(SPORT_NAME, "COM1");
		} else {		
			this.setString(SPORT_NAME,"/dev/ttyS0");
		}
		
		this.setInt(SPORT_BAUD, 115200);
		
		this.setColor(CRIT_ERR_BG, Color.BLACK);
		this.setColor(CRIT_ERR_FG, Color.WHITE);
		this.setColor(WARN_BG, Color.ORANGE);
		this.setColor(WARN_FG, Color.WHITE);
		this.setColor(SEL_BG, Color.BLUE);
		this.setColor(SEL_FG, Color.WHITE);
		this.setColor(HIG_BG, Color.YELLOW);
		this.setColor(HIG_FG, Color.BLACK);
		
		this.setInt(H_SIZE, 768);
		this.setInt(W_SIZE, 1024);
		this.setInt(VIEW_MODE, VM_IFRAME);
	}
	
	public String getConfFilePath() {
		return confFilePath;
	}

	public void setConfFilePath(final String confFilePath) {
		this.confFilePath = confFilePath;
	}
	
	public void setString(final String key, final String setting){
		this.confFile.setProperty(key, setting);
	}
	
	public void setInt(final String key, final int setting){
		this.confFile.setProperty(key, Integer.toString(setting));
	}
	
	public void setColor(final String key, final Color color){
		this.confFile.setProperty(key, Integer.toString(color.getRGB()));
	}
	
	public String getString(final String key){
		return this.confFile.getProperty(key);
	}
	
	public int getInt(final String key){
		return Integer.parseInt(this.confFile.getProperty(key));
	}
	
	public Color getColor(final String key){
		return new Color(Integer.parseInt(this.confFile.getProperty(key)));
	}
}

