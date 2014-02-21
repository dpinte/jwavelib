package gui;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.coronis.exception.CoronisException;
import com.coronis.frames.CoronisFrame;
import com.dipole.libs.Functions;
import common.ParameterList;

import frame.*;

public class FrameDetailPanel extends JScrollPane implements ListSelectionListener {
	private JTree detailTree;
	private DefaultMutableTreeNode root, header, body, footer, data, timeout, route;
	private SnifferFrameInterface frame = null;
	private FrameContainer container;
	private JTable frameTable;
	private int frameIndex;
	
	public FrameDetailPanel(FrameContainer container, JTable frameTable) {
		super();
		
		this.container = container;
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

	private void populateTree(){
		this.header.removeAllChildren();
		this.body.removeAllChildren();
		this.footer.removeAllChildren();
		this.data.removeAllChildren();
		this.timeout.removeAllChildren();
		this.route.removeAllChildren();
		
		this.header.add(new DefaultMutableTreeNode("SYNC : 0xFF"));
		if(this.frame.isStxOk()){
			this.header.setUserObject("Header");
			this.header.add(new DefaultMutableTreeNode("STX : 0x02"));
		} else {
			this.header.setUserObject("HeaderE");
			this.header.add(new DefaultMutableTreeNode("STXE"));
		}
		this.header.add(new DefaultMutableTreeNode("LENGTH: "+ (this.frame.getData().length() +4)));
		
		this.body.add(new DefaultMutableTreeNode("CMD: "+ Functions.printHumanHex(this.frame.getCmd(), true)));
		
		switch(this.frame.getCmd()){
			case CoronisFrame.RES_SEND_SERVICE:
			case CoronisFrame.SERVICE_RESPONSE:
			case CoronisFrame.RECEIVED_FRAME:
				this.data.setUserObject("Data: "+ this.frame.getData());
				this.data.add(new DefaultMutableTreeNode("Module ID: "+ 
														((SnifferReceivedFrame)this.frame).getModuleId()));
				break;
				
			case CoronisFrame.REQ_WRIT_PARAM:
			case CoronisFrame.RES_READ_PARAM:
            case CoronisFrame.RES_READ_CHANNEL:
            case CoronisFrame.RES_READ_PHYCONFIG:
            case CoronisFrame.RES_READ_TX_POWER:
            	this.data.setUserObject("Data: "+ this.frame.getData()) ;
            	int paramNum = ((SnifferReqWriteParameterFrame)this.frame).getParameterNumber();
            	int[] paramData = ((SnifferReqWriteParameterFrame)this.frame).getParameterData();
            	
            	this.data.add(new DefaultMutableTreeNode("parameter: "+ ParameterList.getParameterName(paramNum)));
            	this.data.add(new DefaultMutableTreeNode("value: "+ ParameterList.parseParameterValue(paramNum, paramData)));
            	break;
            	
            case CoronisFrame.RES_CHANGE_TX_POWER:
            case CoronisFrame.RES_CHANGE_UART_BAUDRATE:
            case CoronisFrame.RES_SELECT_CHANNEL:
            case CoronisFrame.RES_SELECT_PHYCONFIG:
            case CoronisFrame.RES_WRIT_AUTOCORR_STATE:
			case CoronisFrame.RES_WRIT_PARAM:
				this.data.setUserObject("Data: "+ this.frame.getData());
				if(((SnifferResWriteParameterFrame)this.frame).getStatus()){
					this.data.add(new DefaultMutableTreeNode("Update Status: OK"));
				} else {
					this.data.add(new DefaultMutableTreeNode("Update Status: ERROR"));
				}
				break;
				
			case CoronisFrame.RES_SEND_FRAME:
				this.data.setUserObject("Data: "+ this.frame.getData());
				if(((SnifferResSendFrame)this.frame).getStatus()){
					this.data.add(new DefaultMutableTreeNode("Emission status: OK"));
				} else {
					this.data.add(new DefaultMutableTreeNode("Emission Status: ERROR"));
				}
				break;
				
            case CoronisFrame.TR_ERROR_FRAME:
            	this.data.setUserObject("Data: "+ this.frame.getData());
    			try {
    				if(((SnifferTransmitionErrorFrame)frame).isPtpMode()){
    					this.data.add(new DefaultMutableTreeNode("Tramsition mode: Point-To-Point"));
    				} else {
    					this.data.add(new DefaultMutableTreeNode("Tramsition mode: Relayed"));
    				}
    				this.data.add(new DefaultMutableTreeNode("Message:" + ((SnifferTransmitionErrorFrame)frame).getErrorMessage()));
    			} catch (CoronisException e) {
    				this.data.add(new DefaultMutableTreeNode(e.getMessage()));
    			}
            	break;
            	
            case CoronisFrame.RECEIVED_MULTIFRAME:
            	this.data.setUserObject("Data: "+ this.frame.getData());

    			this.data.add(new DefaultMutableTreeNode("Module ID: "+
    													((SnifferReceivedMultiFrame)this.frame).getModuleId()));
    			this.data.add(new DefaultMutableTreeNode("Status: "+
    													((SnifferReceivedMultiFrame)this.frame).getStatus()));
    			this.data.add(new DefaultMutableTreeNode("Total Frame: "+
    													((SnifferReceivedMultiFrame)this.frame).getTotalFramesReceived()));
    			this.data.add(new DefaultMutableTreeNode("Frame Index: "+
    													((SnifferReceivedMultiFrame)this.frame).getFrameIndex()));
				break;
				
            default:
            	this.data.setUserObject("Data: "+ this.frame.getData());            	
		}

		this.body.add(this.data);
		
		if(this.frame.isCrcOk()){
			this.footer.setUserObject("Footer");
			this.footer.add(new DefaultMutableTreeNode("CRC: "+ Functions.printHumanHex(this.frame.getCalculatedCrc(), true)));
		} else {
			this.footer.setUserObject("FooterE");
			this.footer.add(new DefaultMutableTreeNode("CRCE,"+ Functions.printHumanHex(this.frame.getSniffedCrc(), true) +","
																+ Functions.printHumanHex(this.frame.getCalculatedCrc(), true)));
		}
		if(this.frame.isEtxOk()){
			this.footer.setUserObject("Footer");
			this.footer.add(new DefaultMutableTreeNode("ETX : 0x03"));
		} else {
			this.footer.setUserObject("FooterE");
			this.footer.add(new DefaultMutableTreeNode("ETXE")); 
		}
		
		if(this.frame.isTimeStampOk()){
			this.timeout.add(new DefaultMutableTreeNode("OK"));
		} else {
			this.timeout.add(new DefaultMutableTreeNode("Timeout Error"));
		}
		String[] rep = null;
		
		if(	this.frame.getCmd() != CoronisFrame.CMD_ACK &&
			this.frame.getCmd() != CoronisFrame.REQ_WRIT_PARAM &&
			this.frame.getCmd() != CoronisFrame.REQ_READ_PARAM )
			rep = FrameAnalyser.getFrameRelayRoute(this.container, this.frameIndex);
		
		if(rep == null || rep.length <= 0)
			this.route.add(new DefaultMutableTreeNode("No repeater"));
		else
			this.route.add(new DefaultMutableTreeNode(rep[0]));
		
		DefaultTreeModel model = (DefaultTreeModel)this.detailTree.getModel();
		model.reload();
	}
	
	public void setFrame(SnifferFrameInterface frame){
		this.frame = frame;
		this.populateTree();
	}
	
	public JTree getDetailTree(){
		return this.detailTree;
	}
	
	public void valueChanged(ListSelectionEvent event){
		/* valueChanged is fired twice, ignore one call */
		if (event.getValueIsAdjusting())
			return;
		
		DefaultListSelectionModel sel = (DefaultListSelectionModel) event.getSource();
		if(sel.getMaxSelectionIndex() >= 0){
			FrameTableModel model = (FrameTableModel) this.frameTable.getModel();
			this.frameIndex = model.getFrameIndex(sel.getMaxSelectionIndex());
			this.setFrame(this.container.getFrameAt(model.getFrameIndex(sel.getMaxSelectionIndex())));
		}
	}
}
