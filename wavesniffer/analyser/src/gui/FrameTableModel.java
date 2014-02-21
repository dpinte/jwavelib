package gui;

import java.util.Arrays;

import javax.swing.table.AbstractTableModel;

import events.FrameContainerListener;

import frame.FrameContainer;
import frame.SnifferFrameInterface;

public class FrameTableModel extends AbstractTableModel implements FrameContainerListener {
	private String[] columnName = {"No", "Time", "Source", "Frame"};
	private FrameContainer container;
	private int[] displayframeIndex = new int[1];
	private int[] highlightIndex = new int[1];
	
	public FrameTableModel(FrameContainer container) {
		this.container = container;
		this.displayframeIndex[0] = -1;
		this.highlightIndex[0] = -1;
	}

	public void frameAdded(){
		this.fireTableRowsInserted(this.getRowCount() + 1, this.getColumnCount() + 1);
	}
	
	public void framesFiltered(int[] indexes) {
		this.displayframeIndex = indexes;
		this.fireTableDataChanged();
	}
	
	public void framesHighlighted(int[] frameInd) {
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
	 
	public String getColumnName(int columnIndex) {
		return this.columnName[columnIndex];
	}
	 
	public int getRowCount(){
		if(this.displayframeIndex[0] == -1){
			return this.container.getTotalFrames();
		} else {
			return this.displayframeIndex.length;
		}
	}
	 
	public Object getValueAt(int rowIndex, int columnIndex) {
		String tmpString = "";
		SnifferFrameInterface frame = null;
		
		if(this.displayframeIndex[0] == -1){
			frame = this.container.getFrameAt(rowIndex);
		} else {
			frame = this.container.getFrameAt(this.displayframeIndex[rowIndex]);
		}
		
		switch(columnIndex){
			case 0:
				if(this.displayframeIndex[0] == -1){
					tmpString = Integer.toString(rowIndex);
				} else {
					tmpString = Integer.toString(this.displayframeIndex[rowIndex]);
				}
				break;
			case 1:
				tmpString = frame.getDateTime(true);
				break;
			case 2:
				if(frame.getDirection() == SnifferFrameInterface.SNI_FROM_MOD){
					tmpString = "From WP";
				} else {
					tmpString = "To WP";
				}
				break;
			case 3:
				tmpString = frame.getSniffedFrame();
				break;
		}
		
		int i;
		if(this.displayframeIndex[0] == -1){
			i = Arrays.binarySearch(this.highlightIndex, rowIndex);
		} else {
			i = Arrays.binarySearch(this.highlightIndex, this.displayframeIndex[rowIndex]);
		}
		
		if(!frame.isCrcOk()){
			tmpString = tmpString + ",CRCE";
		} else if (!frame.isTimeStampOk()){
			tmpString = tmpString +",TSE";
		} else if(!frame.isStxOk()){
			tmpString = tmpString +",STXE";
		} else if(!frame.isEtxOk()){
			tmpString = tmpString +",ETXE";
		} else {
			tmpString = tmpString +",OK";
		}
		
		if(i >= 0){
			tmpString = tmpString +",HL";
		}
		
		return tmpString;
	}
	
	public int getFrameIndex(int row){
		if(this.displayframeIndex[0] == -1){
			return row;
		} else {
			return this.displayframeIndex[row];
		}
	}


}
