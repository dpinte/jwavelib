/**
 * @author Bertrand antoine
 *
 */

import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import cli.CLI;
import cli.Generator;

import common.Settings;
import common.XmlParser;

import frame.FrameContainer;

import gui.MainWindow;

public class Analyser {

	public static void displayHelp(){
		System.out.println("Args missings");
		System.out.println("args are: <display> <portName>");
		System.out.println("<display can be cli | gui | gen");
		System.out.println("<portName> is the port name to use");
		System.out.println("    the 2d arg is use only with 'gen' display");
		System.out.println("    for other display set it in $HOME/.wavesniffer.cfg");
	}
	
	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		FrameContainer frameContainer;
		
		if(args.length < 1){
			displayHelp();
			System.exit(1);
		}

		System.out.println("Welcome to the WaveSniffer Analyser\n\n");
		
		XmlParser parser = new XmlParser("rel.xml");
		parser.buildRelation();
		
		Settings settings = new Settings(System.getenv("HOME") + File.separatorChar + ".wavesniffer.cfg");
		settings.loadSettings();
		
		frameContainer = new FrameContainer();
		
		if(args[0].equals("cli")){
			CLI cli = new CLI(frameContainer, settings);
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
			MainWindow window = new MainWindow(frameContainer, settings);
		} else if(args[0].equals("gen")){
			if(args.length != 2){
				System.out.println("missing args");
				displayHelp();
				System.exit(1);
			} else {
				Generator gen = new Generator(args[1]);
			}
		}
		
		/*Runtime.getRuntime().addShutdownHook(new Thread(){
												public void run(){
													cli.close();
												}
											});*/
	}
}
