package com.dipole.jwavetool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;

import com.coronis.CoronisLib;

import com.dipole.jwavetool.common.Common;

public class AboutDialog extends JDialog implements ActionListener {
	
	private JPanel butPan;
	private JEditorPane txtArea;
	private JButton validBut;
	
	public AboutDialog(final Component parent) {
		super();
		
		this.setTitle("About WaveSniffer Analyser");
		this.setModal(true);
		this.setSize(300, 300);
		
		this.setLocationRelativeTo(parent);
		
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		this.txtArea = new JEditorPane();
		this.txtArea.setEditorKit(new HTMLEditorKit());
		this.txtArea.setEditable(false);

		StringBuffer txtBuff = new StringBuffer(180);
		txtBuff.append("<center><b><u>"+ Common.NAME +"</u></b></center><br>");
		txtBuff.append("<u>Version:</u>&nbsp;"+ Common.VERSION +"<br>");
		txtBuff.append("<br>Using <b>JWaveLib</b> version: "+ CoronisLib.version +"<br>");
		txtBuff.append("<br>Icons set from <b><a href='http://tango.freedesktop.org'>Tango Project</a><b>");
		
		txtArea.setText(txtBuff.toString());

		
		this.validBut = new JButton("OK");
		this.butPan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		this.butPan.add(this.validBut);
		
		this.add(this.txtArea, BorderLayout.CENTER);
		this.add(this.butPan, BorderLayout.SOUTH);
		
		this.validBut.addActionListener(this);
		this.setVisible(true);
	}
	
	public void actionPerformed(final ActionEvent event) {
		if(event.getSource() == this.validBut){
			this.dispose();
		}
	}
}
