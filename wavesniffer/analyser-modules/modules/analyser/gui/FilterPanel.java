package modules.analyser.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import modules.analyser.FrameAnalyser;

import com.coronis.frames.CoronisFrame;

import frame.FrameContainer;
import frame.SnifferFrame;

public class FilterPanel extends JPanel implements ActionListener {
	private JButton filterBut, resetBut;
	private JComboBox filterCmdCombo, filterDirCombo;
	private HashMap <String, Integer> filterItem;
	
	public FilterPanel() {
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		
		this.filterDirCombo = new JComboBox();
		this.filterDirCombo.addItem("All");
		this.filterDirCombo.addItem("From WavePort");
		this.filterDirCombo.addItem("From Terminal");
		
		this.filterCmdCombo = new JComboBox();
		this.filterCmdCombo.addItem("All");
		
		this.filterBut = new JButton("Filter");
		this.resetBut = new JButton("Reset");
		
		this.filterItem = new HashMap <String, Integer> ();
		this.setupItem();
		
		ArrayList <String> key = new ArrayList <String> (this.filterItem.keySet());
		Collections.sort(key);
		for(String item : key){
			this.filterCmdCombo.addItem(item);
		}
		
		this.add(new JLabel("Select CMD: "));
		this.add(this.filterCmdCombo);
		this.add(new JLabel("Source: "));
		this.add(this.filterDirCombo);
		this.add(this.filterBut);
		this.add(this.resetBut);
		
		this.filterBut.addActionListener(this);
		this.resetBut.addActionListener(this);
	}

	private void setupItem(){
		this.filterItem.put("ACK", Integer.valueOf(CoronisFrame.ACK));
		this.filterItem.put("NAK", Integer.valueOf(CoronisFrame.NAK));
		this.filterItem.put("ERROR", Integer.valueOf(CoronisFrame.ERROR));
		this.filterItem.put("REQ_SEND_FRAME", Integer.valueOf(CoronisFrame.REQ_SEND_FRAME));
		this.filterItem.put("RES_SEND_FRAME", Integer.valueOf(CoronisFrame.RES_SEND_FRAME));
		this.filterItem.put("REQ_SEND_MESSAGE", Integer.valueOf(0x22));
		this.filterItem.put("REQ_SEND_BROADCAST_RESPONSE", Integer.valueOf(0x24));
		this.filterItem.put("REQ_SEND_POLLING", Integer.valueOf(0x26));
		this.filterItem.put("REQ_SEND_BROADCAST", Integer.valueOf(0x28));
		this.filterItem.put("REQ_SEND_BROADCAST_MESSAGE", Integer.valueOf(0x2A));
		this.filterItem.put("RECEIVED_FRAME", Integer.valueOf(CoronisFrame.RECEIVED_FRAME));
		this.filterItem.put("RECEPTION_ERROR", Integer.valueOf(0x31));
		this.filterItem.put("RECEIVED_FRAME_POLLING", Integer.valueOf(0x32));
		this.filterItem.put("RECEIVED_BROADCAST_RESPONSE", Integer.valueOf(0x34));
		this.filterItem.put("RECEIVED_FRAME_RELAYED", Integer.valueOf(0x35));
		this.filterItem.put("RECEIVED_MULTIFRAME", Integer.valueOf(CoronisFrame.RECEIVED_MULTIFRAME));
		this.filterItem.put("END_MESSAGE_EXCHANGE", Integer.valueOf(0x37));
		this.filterItem.put("RECEIVED_BROADCAST_FRAME", Integer.valueOf(0x38));
		
	}
	
	public void actionPerformed(ActionEvent event) {		
		if(event.getSource() == this.filterBut){			
			int dir;
			int cmd;
			
			if(this.filterCmdCombo.getSelectedItem().toString().equals("All")){
				cmd = -1;
			} else {
				cmd = this.filterItem.get(this.filterCmdCombo.getSelectedItem().toString()).intValue();
			}
			
			if(this.filterDirCombo.getSelectedIndex() == 1){
				dir = SnifferFrame.SNI_FROM_MOD;
			} else if (this.filterDirCombo.getSelectedIndex() == 2){
				dir = SnifferFrame.SNI_FROM_TER;
			} else {
				dir = -1;
			}
			
			FrameAnalyser.filterFrames(FrameContainer.getInstance(), cmd, dir);
		} else if(event.getSource() == this.resetBut){
			this.filterDirCombo.setSelectedIndex(0);
			this.filterCmdCombo.setSelectedIndex(0);
			FrameAnalyser.filterFrames(FrameContainer.getInstance(), -1, -1);
		}
	}
}
