package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import reader.ReaderManager;
import writer.CsvFileWriter;

import events.ReaderErrorListener;
import events.ReaderStatusListener;

import frame.FrameContainer;

import common.Settings;

public class MainWindow extends JFrame implements ReaderStatusListener, ReaderErrorListener, ActionListener {
	private JMenuBar menuBar;
	private JToolBar toolBar;
	private JMenu appMenu, captureMenu, settingsMenu, helpMenu;
	private JMenuItem newItem, openItem, saveItem, saveAsItem, quitItem;
	private JMenuItem startItem, stopItem, suspendItem;
	private JMenuItem displayCfgItem, serialCfgItem;
	private JMenuItem helpItem, aboutItem;
	private JButton newBut, openBut, saveBut, saveAsBut;
	private JButton startBut, suspendBut, stopBut, serialCfgBut;
	private JButton nextBut, prevBut, refBut;
	private JButton helpBut, modIdBut;
	private MainPanel mainPanel;
	private StatusBar statusBar;
	private FrameContainer container;
	private Settings settings;
	private ReaderManager readers;
	private String savePath = null;
	
	public MainWindow(FrameContainer container, Settings settings) {
		this.container = container;
		this.settings = settings;
		this.readers = new ReaderManager(this.container);
		
		this.setTitle("WaveSniffer Analyser");
		this.setSize(800, 600);
		
		this.setLocationRelativeTo(null);
		
		this.addWindowListener(new WindowAdapter(){
									public void windowClosing(WindowEvent e){quit();}
								});
		
		this.setMenu();
		this.setJMenuBar(this.menuBar);
		this.setTool();
		this.statusBar = new StatusBar("Welcome to WaveSniffer");
		
		this.mainPanel = new MainPanel(this.container, this.settings);
		
		this.setListeners();
		
		this.getContentPane().setLayout(new BorderLayout());
		this.add(this.mainPanel, BorderLayout.CENTER);
		this.add(this.toolBar, BorderLayout.NORTH);
		this.add(this.statusBar, BorderLayout.SOUTH);
		
		this.setVisible(true);
	}
	
	private void setMenu(){	
		this.newItem = new JMenuItem("New", new ImageIcon(this.getClass().getResource("/icons/16x16/document-new.png")));
		this.openItem = new JMenuItem("Open", new ImageIcon(this.getClass().getResource("/icons/16x16/document-open.png")));
		this.saveItem = new JMenuItem("save", new ImageIcon(this.getClass().getResource("/icons/16x16/document-save.png")));
		this.saveItem.setEnabled(false);
		this.saveAsItem = new JMenuItem("Save As", new ImageIcon(this.getClass().getResource("/icons/16x16/document-save-as.png")));
		this.saveAsItem.setEnabled(false);
		this.quitItem = new JMenuItem("Quit", new ImageIcon(this.getClass().getResource("/icons/16x16/system-log-out.png")));
		
		this.startItem = new JMenuItem("Start", new ImageIcon(this.getClass().getResource("/icons/16x16/media-record.png")));
		this.stopItem = new JMenuItem("Stop", new ImageIcon(this.getClass().getResource("/icons/16x16/media-playback-pause.png")));
		this.stopItem.setEnabled(false);
		this.suspendItem = new JMenuItem("Suspend", new ImageIcon(this.getClass().getResource("/icons/16x16/process-stop.png")));
		this.suspendItem.setEnabled(false);
		
		this.serialCfgItem = new JMenuItem("Serial Port", new ImageIcon(this.getClass().getResource("/icons/16x16/preferences-system.png")));
		this.displayCfgItem = new JMenuItem("Display", new ImageIcon(this.getClass().getResource("/icons/16x16/preferences-desktop.png")));
		
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
		
		this.captureMenu = new JMenu("Capture");
		this.captureMenu.add(this.startItem);
		this.captureMenu.add(this.suspendItem);
		this.captureMenu.add(this.stopItem);
		
		this.settingsMenu = new JMenu("Settings");
		this.settingsMenu.add(this.serialCfgItem);
		this.settingsMenu.add(this.displayCfgItem);
		
		this.helpMenu = new JMenu("Help");
		this.helpMenu.add(this.helpItem);
		this.helpMenu.add(this.aboutItem);
		
		this.menuBar = new JMenuBar();
		this.menuBar.add(this.appMenu);
		this.menuBar.add(this.captureMenu);
		this.menuBar.add(this.settingsMenu);
		this.menuBar.add(this.helpMenu);
	}
	
	private void setTool(){
		this.newBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/document-new.png")));
		this.openBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/document-open.png")));
		this.openBut.setToolTipText("Open a file");
		this.saveBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/document-save.png")));
		this.saveBut.setToolTipText("Save");
		this.saveBut.setEnabled(false);
		this.saveAsBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/document-save-as.png")));
		this.saveAsBut.setEnabled(false);
		this.saveAsBut.setToolTipText("Save As");
		
		this.startBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/media-record.png")));
		this.startBut.setToolTipText("Start Sniffing");
		this.suspendBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/media-playback-pause.png")));
		this.suspendBut.setToolTipText("Suspend sniffing");
		this.suspendBut.setEnabled(false);
		this.stopBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/process-stop.png")));
		this.stopBut.setToolTipText("Stop sniffing");
		this.stopBut.setEnabled(false);
		this.serialCfgBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/preferences-system.png")));
		this.serialCfgBut.setToolTipText("Configure serial port");
		
		this.nextBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/go-down.png")));
		this.nextBut.setToolTipText("Select next frame");
		this.prevBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/go-up.png")));
		this.prevBut.setToolTipText("select previous frame");
		this.prevBut.setEnabled(false);
		this.refBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/view-refresh.png")));
		this.refBut.setToolTipText("Refresh");
		
		this.helpBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/help-browser.png")));		
		this.modIdBut = new JButton(new ImageIcon(this.getClass().getResource("/icons/22x22/edit-find.png")));
		this.modIdBut.setToolTipText("View statistics");
		
		this.toolBar = new JToolBar();
		this.toolBar.add(this.newBut);
		this.toolBar.add(this.openBut);
		this.toolBar.add(this.saveBut);
		this.toolBar.add(this.saveAsBut);
		this.toolBar.addSeparator();
		this.toolBar.add(this.startBut);
		this.toolBar.add(this.suspendBut);
		this.toolBar.add(this.stopBut);
		this.toolBar.add(this.serialCfgBut);
		this.toolBar.addSeparator();
		this.toolBar.add(this.nextBut);
		this.toolBar.add(this.prevBut);
		this.toolBar.add(this.refBut);
		this.toolBar.add(this.modIdBut);
		this.toolBar.addSeparator();
		this.toolBar.add(this.helpBut);
	}

	private void setListeners(){
		this.startBut.addActionListener(this);
		this.stopBut.addActionListener(this);
		this.startItem.addActionListener(this);
		this.stopItem.addActionListener(this);
		
		this.serialCfgBut.addActionListener(this);
		this.serialCfgItem.addActionListener(this);
		
		this.displayCfgItem.addActionListener(this);
		
		this.quitItem.addActionListener(this);
		
		this.saveBut.addActionListener(this);
		this.saveItem.addActionListener(this);
		this.saveAsBut.addActionListener(this);
		this.saveAsItem.addActionListener(this);
		
		this.newBut.addActionListener(this);
		this.newItem.addActionListener(this);
		this.openBut.addActionListener(this);
		this.openItem.addActionListener(this);
		
		this.nextBut.addActionListener(this);
		this.prevBut.addActionListener(this);
		
		this.aboutItem.addActionListener(this);
		this.modIdBut.addActionListener(this);
	}
	
	private void save(boolean saveAs){
		if(saveAs || savePath == null){
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
		//FIXME: disconnect reader more properly
		this.stopBut.doClick();
		
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
	
	public void readerStatus(String message){
		this.statusBar.setStatusText(message);
		System.out.println(message);
	}
	
	public void readerError(String message){
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
		System.err.println(message);
	}
	
	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent event){
		if(event.getSource() == this.quitItem){
			this.quit();
		} else if(event.getSource() == this.serialCfgBut || event.getSource() == this.serialCfgItem) {
			SerialSettingsDialog cfg = new SerialSettingsDialog(this, this.settings);
		} else if(event.getSource() == this.aboutItem){
			AboutDialog about = new AboutDialog(this);
		} else if(event.getSource() == this.displayCfgItem){
			DisplaySettingsDialog display = new DisplaySettingsDialog(this, this.settings);
		} else if(event.getSource() == this.saveAsBut || event.getSource() == this.saveAsItem){
			this.save(true);
		} else if(event.getSource() == this.saveBut || event.getSource() == this.saveItem){
			this.save(false);
		} else if(event.getSource() == this.openBut || event.getSource() == this.openItem){
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("*.csv files", "csv"));
			int retVal = fc.showOpenDialog(this);
			
			if(retVal == JFileChooser.APPROVE_OPTION){
				this.savePath = fc.getSelectedFile().getAbsolutePath();
				this.readers.loadFileReader(ReaderManager.CSV_FILE, this.savePath, this, this);
				
				this.container.setSaved(true);	//no modification
				this.saveAsBut.setEnabled(true);
				this.saveBut.setEnabled(true);
				this.saveAsItem.setEnabled(true);
				this.saveItem.setEnabled(true);
			}
		} else if(event.getSource() == this.startBut || event.getSource() == this.startItem){
			this.clear();
			
			this.readers.loadSerialReader(this.container, this.settings.getSerialPort(), this.settings.getBaudrate(), this, this);
			
			//disable or enable available buttons
			this.startBut.setEnabled(false);
			this.startItem.setEnabled(false);
			this.stopBut.setEnabled(true);
			this.stopItem.setEnabled(true);
			this.serialCfgBut.setEnabled(false);
			this.serialCfgItem.setEnabled(false);
			this.openBut.setEnabled(false);
			this.openItem.setEnabled(false);
			this.saveAsBut.setEnabled(false);
			this.saveAsItem.setEnabled(false);
		} else if(event.getSource() == this.stopBut || event.getSource() == this.stopItem){ 
			this.readers.closeSerialReader();
			
			//disable or enable available buttons
			this.startBut.setEnabled(true);
			this.startItem.setEnabled(true);
			this.stopBut.setEnabled(false);
			this.stopItem.setEnabled(false);
			this.serialCfgBut.setEnabled(true);
			this.serialCfgItem.setEnabled(true);
			this.openBut.setEnabled(true);
			this.openItem.setEnabled(true);
			this.saveAsBut.setEnabled(true);
			this.saveAsItem.setEnabled(true);
		} else if(event.getSource() == this.nextBut){
			if(this.mainPanel.getTablePanel().selectNextRow()){
				if(!this.prevBut.isEnabled()){
					this.prevBut.setEnabled(true);
				} 
			} else {
				if(this.prevBut.isEnabled()){
					this.nextBut.setEnabled(false);
				}
			}
		} else if(event.getSource() == this.prevBut){
			if(this.mainPanel.getTablePanel().selectPrevRow()){
				if(!this.nextBut.isEnabled()){
					this.nextBut.setEnabled(true);
				}
			} else {
				if(this.nextBut.isEnabled()){
					this.prevBut.setEnabled(false);
				}
			}
		} else if(event.getSource() == this.newBut || event.getSource() == this.newItem){
			this.clear();
		} else if(event.getSource() == this.modIdBut){
			StatDialog db = new StatDialog(this.container, this.settings);
		}
	}
}
