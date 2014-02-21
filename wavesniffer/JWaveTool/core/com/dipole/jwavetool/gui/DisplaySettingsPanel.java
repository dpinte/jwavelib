package com.dipole.jwavetool.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dipole.jwavetool.common.Settings;

import com.dipole.jwavetool.modules.SettingsInterface;

public class DisplaySettingsPanel 	extends JPanel 
									implements 	ActionListener,
												SettingsInterface {

	private JPanel errColPanel, selColPanel;
	private JButton critColBgBut, critColFgBut, warnColBgBut, warnColFgBut;
	private JButton selColBgBut, selColFgBut, higColBgBut, higColFgBut;
	private Box setBox;
	private Settings settings;
	
	public DisplaySettingsPanel(/*final Settings settings*/) {
		super();
		
		//this.settings = settings;
		this.settings = Settings.getInstance();
		
		this.critColBgBut = new JButton();
		this.critColFgBut = new JButton();
		this.warnColBgBut = new JButton();
		this.warnColFgBut = new JButton();
		this.selColBgBut = new JButton();
		this.selColFgBut = new JButton();
		this.higColBgBut = new JButton();
		this.higColFgBut = new JButton();
		
		this.loadSettings();
		
		this.errColPanel = new JPanel(new GridBagLayout());
		this.errColPanel.setBorder(BorderFactory.createTitledBorder("Error Color"));
		
		GridBagConstraints gbcE = new GridBagConstraints();
		gbcE.fill = GridBagConstraints.BOTH;
		
		gbcE.gridx = 2;
		gbcE.gridy = 0;
		this.errColPanel.add(new JLabel("Background"), gbcE);
		
		gbcE.gridx = 3;
		gbcE.gridy = 0;
		this.errColPanel.add(new JLabel("Foreground"), gbcE);
		
		gbcE.gridx = 0;
		gbcE.gridy = 1;
		this.errColPanel.add(new JLabel("Critical Error : "), gbcE);
		
		gbcE.gridx = 2;
		gbcE.gridy = 1;
		this.errColPanel.add(this.critColBgBut, gbcE);
		
		gbcE.gridx = 3;
		gbcE.gridy = 1;
		this.errColPanel.add(this.critColFgBut, gbcE);
		
		gbcE.gridx = 0;
		gbcE.gridy = 2;
		this.errColPanel.add(new JLabel("Warning : "), gbcE);
		
		gbcE.gridx = 2;
		gbcE.gridy = 2;
		this.errColPanel.add(this.warnColBgBut, gbcE);
		
		gbcE.gridx = 3;
		gbcE.gridy = 2;
		this.errColPanel.add(this.warnColFgBut, gbcE);
		
		this.selColPanel = new JPanel(new GridBagLayout());
		this.selColPanel.setBorder(BorderFactory.createTitledBorder("Selection Color"));

		GridBagConstraints gbcS = new GridBagConstraints();
		gbcS.fill = GridBagConstraints.BOTH;
		
		gbcS.gridx = 2;
		gbcS.gridy = 0;
		gbcS.weightx = 0;
		this.selColPanel.add(new JLabel("Background"), gbcS);
		
		gbcS.gridx = 3;
		gbcS.gridy = 0;
		this.selColPanel.add(new JLabel("Foreground"), gbcS);

		gbcS.gridx = 0;
		gbcS.gridy = 1;
		this.selColPanel.add(new JLabel("Selection : "), gbcS);
		
		gbcS.gridx = 2;
		gbcS.gridy = 1;
		this.selColPanel.add(this.selColBgBut, gbcS);
		
		gbcS.gridx = 3;
		gbcS.gridy = 1;
		this.selColPanel.add(this.selColFgBut, gbcS);
		
		gbcS.gridx = 0;
		gbcS.gridy = 2;
		this.selColPanel.add(new JLabel("Highlight : "), gbcS);
		
		gbcS.gridx = 2;
		gbcS.gridy = 2;
		this.selColPanel.add(this.higColBgBut, gbcS);
		
		gbcS.gridx = 3;
		gbcS.gridy = 2;
		this.selColPanel.add(this.higColFgBut, gbcS);
		
		
		this.setBox = Box.createVerticalBox();
		setBox.add(errColPanel);
		setBox.add(selColPanel);
		
		this.add(setBox);
		
		this.critColBgBut.addActionListener(this);
		this.critColFgBut.addActionListener(this);
		this.warnColBgBut.addActionListener(this);
		this.warnColFgBut.addActionListener(this);
		this.selColBgBut.addActionListener(this);
		this.selColFgBut.addActionListener(this);
		this.higColBgBut.addActionListener(this);
		this.higColFgBut.addActionListener(this);
	}

	@Override
	public String getTitle() {
		return "Display";
	}

	@Override
	public void loadSettings() {
		this.critColBgBut.setIcon(new ColorButtonIcon(this.settings.getColor(Settings.CRIT_ERR_BG)));
		this.critColFgBut.setIcon(new ColorButtonIcon(this.settings.getColor(Settings.CRIT_ERR_FG)));
		
		this.warnColBgBut.setIcon(new ColorButtonIcon(this.settings.getColor(Settings.WARN_BG)));
		this.warnColFgBut.setIcon(new ColorButtonIcon(this.settings.getColor(Settings.WARN_FG)));
		
		this.selColBgBut.setIcon(new ColorButtonIcon(this.settings.getColor(Settings.SEL_BG)));
		this.selColFgBut.setIcon(new ColorButtonIcon(this.settings.getColor(Settings.SEL_FG)));
		
		this.higColBgBut.setIcon(new ColorButtonIcon(this.settings.getColor(Settings.HIG_BG)));
		this.higColFgBut.setIcon(new ColorButtonIcon(this.settings.getColor(Settings.HIG_FG)));
	}

	@Override
	public void saveSettings() {
		ColorButtonIcon icon;
		
		icon = (ColorButtonIcon) this.critColBgBut.getIcon();
		this.settings.setColor(Settings.CRIT_ERR_BG, icon.getColor());
		icon = (ColorButtonIcon) this.critColFgBut.getIcon();
		this.settings.setColor(Settings.CRIT_ERR_FG, icon.getColor());
		
		icon = (ColorButtonIcon) this.warnColBgBut.getIcon();
		this.settings.setColor(Settings.WARN_BG, icon.getColor());
		icon = (ColorButtonIcon) this.warnColFgBut.getIcon();
		this.settings.setColor(Settings.WARN_FG, icon.getColor());
		
		icon = (ColorButtonIcon) this.selColBgBut.getIcon();
		this.settings.setColor(Settings.SEL_BG, icon.getColor());
		icon = (ColorButtonIcon) this.selColFgBut.getIcon();
		this.settings.setColor(Settings.SEL_FG, icon.getColor());
		
		icon = (ColorButtonIcon) this.higColBgBut.getIcon();
		this.settings.setColor(Settings.HIG_BG, icon.getColor());
		icon = (ColorButtonIcon) this.higColFgBut.getIcon();
		this.settings.setColor(Settings.HIG_FG, icon.getColor());
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		final ColorButtonIcon icon = (ColorButtonIcon)((JButton) event.getSource()).getIcon();
		final Color resColor = JColorChooser.showDialog(this, "Select Color", icon.getColor());
		
		if(resColor != null) {
			icon.setColor(resColor);
		}
		
	}

	@Override
	public JPanel display() {
		return this;
	}

}
