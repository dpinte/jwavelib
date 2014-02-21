/**
 * @author Bertrand antoine
 *
 */

import java.io.File;
import java.util.ArrayList;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

//import cli.CLI;
//import cli.Generator;

import common.Settings;
import common.XmlParser;
import common.Log;

import frame.FrameContainer;

import gui.MainWindow;
import modules.ModulesLoader;
import modules.ModuleInterface;

public class Analyser {

	public static void displayHelp(){
		/*
		System.out.println("Args missings");
		System.out.println("args are: <display> <portName>");
		System.out.println("<display can be cli | gui | gen");
		System.out.println("<portName> is the port name to use");
		System.out.println("    the 2d arg is use only with 'gen' display");
		System.out.println("    for other display set it in $HOME/.wavesniffer.cfg");
		*/
	}
	
	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		FrameContainer frameContainer;
		ArrayList <ModuleInterface> modules = new ArrayList <ModuleInterface> (5);
		
		/* init logger */
		Log.useConsole(false);
		
		/*
		if(args.length < 1){
			displayHelp();
			System.exit(1);
		}
		 */
		
		Log.info("Welcome to the WaveSniffer Analyser\n");
		
		Log.debug("libra path: "+ System.getProperty("java.library.path"));
		Log.debug("class path: "+ System.getProperty("java.class.path"));
		
		XmlParser parser = new XmlParser("rel.xml");
		parser.buildRelation();
		
		//Settings settings = new Settings(System.getenv("HOME") + File.separatorChar + ".wavesniffer.cfg");
		Settings settings = Settings.getInstance();
		settings.setConfFilePath(System.getenv("HOME") + File.separatorChar + ".wavesniffer.cfg");
		settings.loadSettings();		
		
		ModulesLoader loader = new ModulesLoader();
		try {
			loader.search("mods");
			modules = loader.getModuleList();
			Log.info("found "+ modules.size() +" modules: ");
			
			for(ModuleInterface module : modules){				
				module.createModule(settings);
				Log.info("   * "+ module.getModuleName());
			}
		} catch (Exception e1) {
			Log.fatal(e1.getMessage());
		}
		
		try {
			//set the look and feel
			if(System.getProperty("os.name").equals("Linux")){
				//force GTK LookAndFeel on linux platform, works well with other WM than gnome ;)
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			}  else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
	    } catch (UnsupportedLookAndFeelException e) {
	       Log.error(e.getMessage());
	    } catch (ClassNotFoundException e) {
	    	Log.error(e.getMessage());
	    } catch (InstantiationException e) {
	    	Log.error(e.getMessage());
	    } catch (IllegalAccessException e) {
	    	Log.error(e.getMessage());
	    } finally {
			MainWindow window = new MainWindow(modules);
	    }
		
		/*
		if(args[0].equals("cli")){
			//CLI cli = new CLI(frameContainer, settings);
			System.out.println("CLI disabled");
		} else if(args[0].equals("gui")){
			try {
				//set the look and feel
				if(System.getProperty("os.name").equals("Linux")){
					//force GTK LookAndFeel on linux platform, works well with other WM than gnome ;)
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				}  else {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
		    } catch (UnsupportedLookAndFeelException e) {
		       // handle exception
		    } catch (ClassNotFoundException e) {
		       // handle exception
		    } catch (InstantiationException e) {
		       // handle exception
		    } catch (IllegalAccessException e) {
		       // handle exception
		    }
			MainWindow window = new MainWindow(frameContainer, settings, modules);
		} else if(args[0].equals("gen")){
			System.out.println("generator disabled");
			if(args.length != 2){
				System.out.println("missing args");
				displayHelp();
				System.exit(1);
			} else {
				Generator gen = new Generator(args[1]);
			}
		}
		*/
		
		/*Runtime.getRuntime().addShutdownHook(new Thread(){
												public void run(){
													cli.close();
												}
											});*/
	}
}
