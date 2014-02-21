package com.dipole.jwavetool.gui.analyser;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.dipole.jwavetool.frame.*;

import com.dipole.jwavetool.common.Common;
import com.dipole.jwavetool.common.Log;

import com.coronis.frames.CoronisFrame;
import com.dipole.libs.Functions;


public class FrameDetailPanel extends JScrollPane implements ListSelectionListener {
	private JTree detailTree;
	private DefaultMutableTreeNode root, header, body, footer, data, timeout, route;
	private SnifferFrameInterface frame = null;
	private FrameContainer container;
	private JTable frameTable;
	private int frameIndex;
	
	public FrameDetailPanel(final JTable frameTable) {
		super();
		
		/* 
		 * keep reference to instance of Frame container
		 */
		this.container = FrameContainer.getInstance();
		
		this.frameTable = frameTable;
		
		this.root = new DefaultMutableTreeNode("Frame Detail");
		this.header = new DefaultMutableTreeNode("Header");
		this.body = new DefaultMutableTreeNode("Body");
		this.footer = new DefaultMutableTreeNode("Footer");
		this.timeout = new DefaultMutableTreeNode("Timeout");
		this.data = new DefaultMutableTreeNode();
		this.route = new DefaultMutableTreeNode("Frame Routing");
		
		this.root.add(this.header);
		this.root.add(this.body);
		this.root.add(this.footer);
		this.root.add(this.timeout);
		this.root.add(this.route);
		
		this.detailTree = new JTree(this.root);
		this.detailTree.setCellRenderer(new FrameDetailCellsRenderer());
		this.frameTable.getSelectionModel().addListSelectionListener(this);
		this.setViewportView(this.detailTree);
	}

	private void populateTree() {
		Log.trace("Enter module.analyser.gui.FrameDetailAnalyser.populateTree");
		Log.debug("   cmd: "+ Functions.printHumanHex(this.frame.getCmd(), true));
		Log.debug("   data: "+ this.frame.getDataAsString());
		
		Description desc;
		
		/* clear all tree */
		this.header.removeAllChildren();
		this.body.removeAllChildren();
		this.footer.removeAllChildren();
		this.data.removeAllChildren();
		this.timeout.removeAllChildren();
		this.route.removeAllChildren();
		
		/* populate HEADER: SYNC + ETX + LENGTH */
		this.header.add(new DefaultMutableTreeNode("SYNC : 0xFF"));
		
		if(this.frame.isStxOk()) {
			this.header.setUserObject("Header");
			this.header.add(new DefaultMutableTreeNode("STX : 0x02"));
		} else {
			this.header.setUserObject("HeaderE");
			this.header.add(new DefaultMutableTreeNode("STXE"));
		}
		
		this.header.add(new DefaultMutableTreeNode("LENGTH: "+ (this.frame.getDataAsString().length() +4)));
		
		/* populate BODY: CMD + DATA */
		desc = Common.cmdDescription.get(this.frame.getCmd());
		
		if(desc != null) {
			this.body.add(new DefaultMutableTreeNode("CMD: "+
													desc.getName() +"(" +
													Functions.printHumanHex(this.frame.getCmd(), true) +")"));
		} else {
			this.body.add(new DefaultMutableTreeNode("CMD: BAD COMMAND (" +
													Functions.printHumanHex(this.frame.getCmd(), true) +")"));
		}
		
		this.data.setUserObject("Data: "+ this.frame.getDataAsString());
		String[][] dataVal = FrameParser.parseData(this.frame);
		
		if(dataVal != null) {
			Log.debug("dataVal length:" + dataVal.length);
			for(int i = 0; i < dataVal.length; i++){
				this.data.add(new DefaultMutableTreeNode(dataVal[i][0] +": "+ dataVal[i][1]));
			}
		}
		
		this.body.add(this.data);
		
		/* populate FOOTER: CRC + ETX */
		if(this.frame.isCrcOk()) {
			this.footer.setUserObject("Footer");
			this.footer.add(new DefaultMutableTreeNode("CRC: "+ Functions.printHumanHex(this.frame.getCalculatedCrc(), true)));
		} else {
			this.footer.setUserObject("FooterE");
			this.footer.add(new DefaultMutableTreeNode("CRCE,"+ Functions.printHumanHex(this.frame.getSniffedCrc(), true) +","
																+ Functions.printHumanHex(this.frame.getCalculatedCrc(), true)));
		}
		
		if(this.frame.isEtxOk()) {
			this.footer.setUserObject("Footer");
			this.footer.add(new DefaultMutableTreeNode("ETX : 0x03"));
		} else {
			this.footer.setUserObject("FooterE");
			this.footer.add(new DefaultMutableTreeNode("ETXE")); 
		}
		
		/* populate Timeout */
		if(this.frame.isTimeStampOk()) {
			this.timeout.add(new DefaultMutableTreeNode("OK"));
		} else {
			this.timeout.add(new DefaultMutableTreeNode("Timeout Error"));
		}
		
		/* populate Route */
		String[] rep = null;
		
		if(	this.frame.getCmd() != CoronisFrame.CMD_ACK &&
			this.frame.getCmd() != CoronisFrame.REQ_WRIT_PARAM &&
			this.frame.getCmd() != CoronisFrame.REQ_READ_PARAM ) {
			rep = FrameAnalyser.getFrameRelayRoute(this.container, this.frameIndex);
		}
		
		if(rep == null || rep.length <= 0) {
			this.route.add(new DefaultMutableTreeNode("No repeater"));
		} else {
			this.route.add(new DefaultMutableTreeNode(rep[0]));
		}
		
		/* display tree */
		DefaultTreeModel model = (DefaultTreeModel)this.detailTree.getModel();
		model.reload();
	}
	
	public void setFrame(final SnifferFrameInterface frame) {
		this.frame = frame;
		this.populateTree();
	}
	
	public JTree getDetailTree() {
		return this.detailTree;
	}
	
	public void valueChanged(final ListSelectionEvent event) {
		/* valueChanged is fired twice, ignore one call */
		if (event.getValueIsAdjusting()) {
			return;
		}
		
		DefaultListSelectionModel sel = (DefaultListSelectionModel) event.getSource();
		if(sel.getMaxSelectionIndex() >= 0){
			FrameTableModel model = (FrameTableModel) this.frameTable.getModel();
			this.frameIndex = model.getFrameIndex(sel.getMaxSelectionIndex());
			this.setFrame(this.container.getFrameAt(model.getFrameIndex(sel.getMaxSelectionIndex())));
		}
	}
}
