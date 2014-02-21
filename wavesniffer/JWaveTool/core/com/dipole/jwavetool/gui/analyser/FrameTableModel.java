package com.dipole.jwavetool.gui.analyser;

import java.util.Arrays;

import javax.swing.table.AbstractTableModel;

import com.coronis.frames.CoronisFrame;
import com.dipole.jwavetool.events.FrameContainerListener;

import com.dipole.jwavetool.frame.FrameContainer;
import com.dipole.jwavetool.frame.SnifferFrameInterface;

public class FrameTableModel extends AbstractTableModel implements FrameContainerListener {
	private String[] columnName = {"No", "Time", "Source", "Frame"};
	private FrameContainer container;
	private int[] displayframeIndex = new int[1];
	private int[] highlightIndex = new int[1];
	
	public FrameTableModel() {
		this.container = FrameContainer.getInstance();
		this.container.addFrameContainerListener(this);
		
		this.displayframeIndex[0] = -1;
		this.highlightIndex[0] = -1;
	}

	public void frameAdded(){
		this.fireTableRowsInserted(this.getRowCount() + 1, this.getColumnCount() + 1);
	}
	
	public void framesFiltered(final int[] indexes) {
		this.displayframeIndex = indexes;
		this.fireTableDataChanged();
	}
	
	public void framesHighlighted(final int[] frameInd) {
		// TODO Auto-generated method stub
		this.highlightIndex = frameInd;
		this.fireTableDataChanged();
	}
	
	public void containerCleared() {
		this.displayframeIndex = new int[1];
		this.displayframeIndex[0] = -1;
		this.fireTableDataChanged();
	}
	
	public int getColumnCount(){
		return this.columnName.length;
	}
	 
	public String getColumnName(final int columnIndex) {
		return this.columnName[columnIndex];
	}
	 
	public int getRowCount() {
		if(this.displayframeIndex[0] == -1) {
			return this.container.getTotalFrames();
		} else {
			return this.displayframeIndex.length;
		}
	}
	 
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		StringBuffer buff = new StringBuffer();
		SnifferFrameInterface frame = null;
		
		if(this.displayframeIndex[0] == -1) {
			frame = this.container.getFrameAt(rowIndex);
		} else {
			frame = this.container.getFrameAt(this.displayframeIndex[rowIndex]);
		}
		
		switch(columnIndex){
			case 0:
				if(this.displayframeIndex[0] == -1) {
					buff.append(rowIndex);
				} else {
					buff.append(this.displayframeIndex[rowIndex]);
				}
				break;
				
			case 1:
				buff.append(frame.getDateTime(true));
				break;
				
			case 2:
				if(frame.getDirection() == SnifferFrameInterface.SNI_FROM_MOD) {
					buff.append("From WP");
				} else {
					buff.append("From WP");
				}
				break;
				
			case 3:
				buff.append(frame.getSniffedFrame());
				break;
		}
		
		int i;
		if(this.displayframeIndex[0] == -1) {
			i = Arrays.binarySearch(this.highlightIndex, rowIndex);
		} else {
			i = Arrays.binarySearch(this.highlightIndex, this.displayframeIndex[rowIndex]);
		}
		
		//FIXME: handle this better: dedicated class for example
		if(!frame.isCrcOk()) {
			buff.append(",CRCE");
		} else if (!frame.isTimeStampOk()) {
			buff.append(",TSE");
		} else if(!frame.isStxOk()) {
			buff.append(",STXE");
		} else if(!frame.isEtxOk()) {
			buff.append(",ETXE");
		} else if(frame.getCmd() == CoronisFrame.ERROR ||
					frame.getCmd() == CoronisFrame.NAK){
			buff.append(",CMDE");
		} else {
			buff.append(",OK");
		}
		
		if(i >= 0){
			buff.append(",HL");
		}
		
		return buff.toString();
	}
	
	public int getFrameIndex(final int row){
		if(this.displayframeIndex[0] == -1) {
			return row;
		} else {
			return this.displayframeIndex[row];
		}
	}

	public void refresh(){
		this.fireTableDataChanged();
	}
}
