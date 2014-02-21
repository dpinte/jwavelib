package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import reader.CsvFileReader;

import modules.ModuleInterface;
import modules.SettingsInterface;

import writer.CsvFileWriter;

import events.ModuleErrorListener;
import events.ReaderErrorListener;
import events.ReaderStatusListener;

import frame.FrameContainer;

import common.Log;
import common.Settings;

public class MainWindow extends JFrame implements ModuleErrorListener, ReaderErrorListener, ReaderStatusListener, ActionListener {
	private JDesktopPane desk;
	private JTabbedPane tabedDesk;
	private JPanel toolPanel;
	private JMenuBar menuBar;
	private JMenu appMenu, moduleMenu, settingsMenu, helpMenu;
	private JMenuItem newItem, openItem, saveItem, saveAsItem, quitItem;
	private JMenuItem cfgItem;
	private JMenuItem helpItem, aboutItem;
	private JButton newBut, openBut, saveBut, saveAsBut;
	private JButton helpBut;
	private StatusBar statusBar;
	private FrameContainer container;
	private Settings settings;
	private String savePath;
	ArrayList <ModuleInterface> modules;
	//ArrayList <JMenu> modMen;
	
	public MainWindow(ArrayList <ModuleInterface> modules) {
		/*
		this.settings = settings;
		*/
		
		this.settings = Settings.getInstance();
		this.container = FrameContainer.getInstance();
		
		this.modules = modules;
		
		this.setTitle("WaveSniffer Analyser");
		this.setSize(1024, 768);
		
		this.setLocationRelativeTo(null);
		
		this.addWindowListener(new WindowAdapter(){
									public void windowClosing(WindowEvent e){quit();}
								});
		
		this.statusBar = new StatusBar();
		this.statusBar.setStatusText("Welcome to WaveSniffer");
		
		this.setMenu();
		this.setJMenuBar(this.menuBar);
		
		this.setTool();
		
		this.getContentPane().setLayout(new BorderLayout());
		this.add(this.toolPanel, BorderLayout.NORTH);
		this.add(this.statusBar, BorderLayout.SOUTH);
		
		//this.modMen = new ArrayList <JMenu> (5);
		for(int i = 0; i < this.modules.size(); i++){
			ModuleInterface module = this.modules.get(i);
		
			JMenu men = new JMenu();
			men.setText(module.getModuleName());
			
			men.add(new JMenuItem(new LoadModuleAction(this, i)));
			men.add(new JMenuItem(new UnLoadModuleAction(this, i)));
			
			this.moduleMenu.add(men);
			//this.modMen.add(men);
		}
		
		if(this.settings.getInt(Settings.VIEW_MODE) == Settings.VM_IFRAME){
			this.desk = new JDesktopPane();
			this.add(this.desk, BorderLayout.CENTER);
		} else {
			this.tabedDesk = new JTabbedPane();
			this.add(this.tabedDesk, BorderLayout.CENTER);
		}
		
		this.setVisible(true);
	}
	
	public void loadModule(int ind){
		ModuleInterface module = this.modules.get(ind);
		
		module.addModuleErrorListener(this);
		module.addModuleStatusListener(this.statusBar);
		
		if(module.hasToolBar()){
			JToolBar tmpTool = module.getToolBar();
			
			/* 
			 * need to refresh LAF 
			 * if no refresh, modules use default LAF (Metal)
			 */
			SwingUtilities.updateComponentTreeUI(tmpTool);
			
			this.toolPanel.add(tmpTool);
		}
			
		
		if(module.hasMenu()){
			JMenu tmpMenu = module.getMenu();
			
			/* 
			 * need to refresh LAF 
			 * if no refresh, modules use default LAF (Metal)
			 */
			SwingUtilities.updateComponentTreeUI(tmpMenu);
			
			this.menuBar.add(tmpMenu);
		}
		
		if(module.hasGUI()){
			Component tmpGui = module.getGUI();
			
			/* 
			 * need to refresh LAF 
			 * if no refresh, modules use default LAF (Metal)
			 */
			SwingUtilities.updateComponentTreeUI(tmpGui);
			
			if(this.settings.getInt(Settings.VIEW_MODE) == Settings.VM_IFRAME)
				this.createIFrames(tmpGui, module.getModuleName());
			else
				this.tabedDesk.add(module.getModuleName(), tmpGui);
			
		} else {
			/* 
			 * force a refresh of the panel, repaint doesn't work
			 * if a module add no GUI but a toolbar, the module's toolbar isn't displayed 
			 */
			this.toolPanel.setVisible(false);
			this.toolPanel.setVisible(true);
		}
	}
	
	public void unloadModule(int ind){
		ModuleInterface module = this.modules.get(ind);
		
		if(module.hasToolBar())
			this.toolPanel.remove(module.getToolBar());
		
		if(module.hasMenu())
			this.menuBar.remove(module.getMenu());
			
		if(module.hasGUI()){
			if(this.settings.getInt(Settings.VIEW_MODE) == Settings.VM_IFRAME)
				this.desk.remove(module.getGUI());
			else
				this.tabedDesk.remove(module.getGUI());
		} else {
			/* 
			 * force a refresh of the panel, repaint doesn't work
			 * if a module add no GUI but a toolbar, the module's toolbar isn't displayed 
			 */
			this.toolPanel.setVisible(false);
			this.toolPanel.setVisible(true);
		}
	}
	
	private void createIFrames(Component gui, String title) {
		JInternalFrame iFrame = new JInternalFrame(title);
		
		iFrame.setSize(800, 600);
		iFrame.setVisible(true);
		iFrame.setResizable(true);
		iFrame.setIconifiable(true);
		
		iFrame.add(gui);
		
		this.desk.add(iFrame);
	}
	
	private void setMenu(){	
		this.newItem = new JMenuItem("New", new ImageIcon(this.getClass().getResource("/icons/16x16/document-new.png")));
		this.openItem = new JMenuItem("Open", new ImageIcon(this.getClass().getResource("/icons/16x16/document-open.png")));
		this.saveItem = new JMenuItem("save", new ImageIcon(this.getClass().getResource("/icons/16x16/document-save.png")));
		this.saveItem.setEnabled(false);
		this.saveAsItem = new JMenuItem("Save As", new ImageIcon(this.getClass().getResource("/icons/16x16/document-save-as.png")));
		this.saveAsItem.setEnabled(false);
		this.quitItem = new JMenuItem("Quit", new ImageIcon(this.getClass().getResource("/icons/16x16/system-log-out.png")));
		
		this.cfgItem = new JMenuItem("Preference", new ImageIcon(this.getClass().getResource("/icons/16x16/preferences-system.png")));
		
		this.helpItem = new JMenuItem("Help", new ImageIcon(this.getClass().getResource("/icons/16x16/help-browser.png")));
		this.aboutItem = new JMenuItem("About");
		
		this.appMenu = new JMenu("Application");
		this.appMenu.add(this.newItem);
		this.appMenu.addSeparator();
		this.appMenu.add(this.openItem);
		this.appMenu.add(this.saveItem);
		this.appMenu.add(this.saveAsItem);
		this.appMenu.addSeparator();
		this.appMenu.add(this.quitItem);
		
		this.moduleMenu = new JMenu("Modules");
		
		this.settingsMenu = new JMenu("Settings");
		this.settingsMenu.add(this.cfgItem);
		
		this.helpMenu = new JMenu("Help");
		this.helpMenu.add(this.helpItem);
		this.helpMenu.add(this.aboutItem);
		
		this.menuBar = new JMenuBar();
		this.menuBar.add(this.appMenu);
		this.menuBar.add(this.moduleMenu);
		this.menuBar.add(this.settingsMenu);
		this.menuBar.add(this.helpMenu);
		
		this.quitItem.addActionListener(this);
		this.saveItem.addActionListener(this);
		this.saveAsItem.addActionListener(this);
		this.openItem.addActionListener(this);
		this.newItem.addActionListener(this);
		this.aboutItem.addActionListener(this);
		this.cfgItem.addActionListener(this);
	}
	
	private void setTool(){
		JToolBar mainToolBar = new JToolBar(AboutDialog.NAME +" tools"); 
		
		this.newBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/document-new.png")));
		this.newBut.setToolTipText("New");
		
		this.openBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/document-open.png")));
		this.openBut.setToolTipText("Open a file");
		
		this.saveBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/document-save.png")));
		this.saveBut.setToolTipText("Save");
		this.saveBut.setEnabled(false);
		
		this.saveAsBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/document-save-as.png")));
		this.saveAsBut.setEnabled(false);
		this.saveAsBut.setToolTipText("Save As");
		
		JToolBar helpToolBar = new JToolBar("Help");
		this.helpBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/help-browser.png")));		
		
		mainToolBar = new JToolBar("Main");
		mainToolBar.add(this.newBut);
		mainToolBar.add(this.openBut);
		mainToolBar.add(this.saveBut);
		mainToolBar.add(this.saveAsBut);

		helpToolBar.add(this.helpBut);
		
		this.toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.toolPanel.add(mainToolBar);
		this.toolPanel.add(helpToolBar);
		
		this.openBut.addActionListener(this);
		this.newBut.addActionListener(this);
		this.saveBut.addActionListener(this);
		this.saveAsBut.addActionListener(this);
	}
	
	private void save(boolean saveAs){
		if(saveAs || this.savePath == null){
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("*.csv files", "csv"));
			int retVal = fc.showSaveDialog(this);
			
			if(retVal == JFileChooser.APPROVE_OPTION){
				this.savePath = fc.getSelectedFile().getAbsolutePath();
				CsvFileWriter writer = new CsvFileWriter(this.savePath, this.container);
				writer.exportToCsv();
				this.saveBut.setEnabled(true);
				this.saveAsItem.setEnabled(true);
			} else {
				return;
			}
		} else {
			CsvFileWriter writer = new CsvFileWriter(this.savePath, this.container);
			writer.exportToCsv();
		}
	}
	
	private void quit(){
		for(ModuleInterface module : this.modules)
			module.destroyModule();
		
		if(this.container.getTotalFrames() > 0 && this.container.isSaved() == false){
			int res = JOptionPane.showConfirmDialog(this, "Would you save");
			if(res == JOptionPane.YES_OPTION){
				this.save(true);
			} else if(res == JOptionPane.CANCEL_OPTION){
				return;
			} 
		}

		this.settings.closeSettings();
		System.exit(0);
	}
	
	private void clear(){
		if(this.container.getTotalFrames() > 0 && this.container.isSaved() == false){
			int res = JOptionPane.showConfirmDialog(this, "Would you save");
			if(res == JOptionPane.YES_OPTION){
				this.save(false);
			} else if(res == JOptionPane.CANCEL_OPTION){
				return;
			} 
		}
		
		if(this.container.getTotalFrames() > 0)
			this.container.resetContainer();
	}
	
	@SuppressWarnings("unused")
	private void showSettingsPanel(){
		ArrayList <SettingsInterface> modConf = new ArrayList <SettingsInterface> ();
		
		modConf.add(new DisplaySettingsPanel(/*this.settings*/));
		
		for(ModuleInterface mod : this.modules){
			if(mod.hasSettingsPanel())
				modConf.add(mod.getSettingsPanel());
		}
		
		
		SettingsDialog diag = new SettingsDialog(this, modConf);
	}
	
	public void readerStatus(String message){
		this.statusBar.setStatusText(message);
		Log.info("READER: "+ message);
		Log.debug("REMOVE READER");
	}
	
	public void readerError(String message){
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
		Log.error("READER: "+ message);
		Log.debug("REMOVE READER");
	}

	@Override
	public void moduleError(String source, String message) {
		JOptionPane.showMessageDialog(this,
										source +"\n"+ message, "Error",
										JOptionPane.ERROR_MESSAGE);
		Log.error(source +" : "+ message);
	}
	
	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent event){
		if(event.getSource() == this.quitItem){
			this.quit();
			
		} else if(event.getSource() == this.aboutItem){
			AboutDialog about = new AboutDialog(this);
			
		} else if(event.getSource() == this.cfgItem){
			this.showSettingsPanel();
			
		} else if(	event.getSource() == this.saveAsBut ||
					event.getSource() == this.saveAsItem){
			this.save(true);
			
		} else if(	event.getSource() == this.saveBut ||
					event.getSource() == this.saveItem){
			this.save(false);
			
		} else if(	event.getSource() == this.openBut ||
					event.getSource() == this.openItem){
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("*.csv files", "csv"));
			int retVal = fc.showOpenDialog(this);
			
			if(retVal == JFileChooser.APPROVE_OPTION){
				this.savePath = fc.getSelectedFile().getAbsolutePath();
				CsvFileReader reader = new CsvFileReader(this.savePath, this.container);
				reader.addReaderErrorListener(this);
				reader.addReaderStatusListener(this);

				reader.run();
				
				this.container.setSaved(true);	//no modification
				this.saveAsBut.setEnabled(true);
				this.saveBut.setEnabled(true);
				this.saveAsItem.setEnabled(true);
				this.saveItem.setEnabled(true);
			}
			
		} else  if(	event.getSource() == this.newBut ||
					event.getSource() == this.newItem){
			this.clear();
		}
	}

}
