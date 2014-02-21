package com.dipole.jwavetool.gui.statistics;

import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.dipole.jwavetool.common.Common;
import com.dipole.jwavetool.common.Settings;
import com.dipole.jwavetool.frame.FrameAnalyser;
import com.dipole.jwavetool.frame.FrameContainer;
import com.dipole.jwavetool.frame.FrameParser;
import com.dipole.jwavetool.gui.DefaultTableCellsRenderer;


public class WpConfigPanel extends JScrollPane{
	
	private JTable table;
	private TableModel model;

	public WpConfigPanel(final Settings settings) {
		
		this.model = new TableModel();
		this.table = new JTable(this.model);
		this.table.setDefaultRenderer(Object.class, new DefaultTableCellsRenderer(settings));
		this.table.setRowSelectionAllowed(true);
		this.table.setColumnSelectionAllowed(false);
		this.table.setShowGrid(true);
		
		this.add(this.table);
		
		this.refresh();
		
		this.setViewportView(this.table);
	}

	public void refresh(){
		this.model.setContent(FrameAnalyser.scanRadioParameters(FrameContainer.getInstance()));
	}
	
	private class TableModel extends AbstractTableModel{
		private HashMap <Integer, Integer[]> content = new HashMap <Integer, Integer[]> ();
		private String[] column = {"Parameter Name", "Parameter Value"};
		
		public TableModel(){
			
		}
		
		public void setContent(final HashMap <Integer, Integer[]> content){
			this.content = content;
			this.fireTableDataChanged();
		}
		
		
		@Override
		public int getColumnCount() {
			return this.column.length;
		}

		@Override
		public int getRowCount() {
			//return this.content.size();
			return 10;
		}
		
		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			String value = null;
			switch(rowIndex){
				case 0:
					if(columnIndex == 0) {
						value = Common.paramDescription.get(FrameParser.AWAKENING_PERIOD).getName();
					} else {
						value = FrameParser.parseParameterValue(FrameParser.AWAKENING_PERIOD,
																Common.integerArrayToIntArray(this.content.get(FrameParser.AWAKENING_PERIOD)));
					}
					break;
					
				case 1:
					if(columnIndex == 0) {
						value = Common.paramDescription.get(FrameParser.WAKEUP_TYPE).getName();
					} else {
						value = FrameParser.parseParameterValue(FrameParser.WAKEUP_TYPE,
								Common.integerArrayToIntArray(this.content.get(FrameParser.WAKEUP_TYPE)));
					}
					break;
					
				case 2:
					if(columnIndex == 0) {
						value = Common.paramDescription.get(FrameParser.WAKEUP_LENGTH).getName();
					} else {
						value = FrameParser.parseParameterValue(FrameParser.WAKEUP_LENGTH,
								Common.integerArrayToIntArray(this.content.get(FrameParser.WAKEUP_LENGTH)));
					}
					break;
					
				case 3:
					if(columnIndex == 0) {
						value = Common.paramDescription.get(FrameParser.RADIO_ACKNOWLEGDE).getName();
					} else {
						value = FrameParser.parseParameterValue(FrameParser.RADIO_ACKNOWLEGDE,
								Common.integerArrayToIntArray(this.content.get(FrameParser.RADIO_ACKNOWLEGDE)));
					}
					break;
					
				case 4:
					if(columnIndex == 0) {
						value = Common.paramDescription.get(FrameParser.RADIO_ADDRESS).getName();
					} else {
						value = FrameParser.parseParameterValue(FrameParser.RADIO_ADDRESS,
								Common.integerArrayToIntArray(this.content.get(FrameParser.RADIO_ADDRESS)));
					}
					break;
					
				case 5:
					if(columnIndex == 0) {
						value = Common.paramDescription.get(FrameParser.RELAY_ROUTE_STATUS).getName();
					} else {
						value = FrameParser.parseParameterValue(FrameParser.RELAY_ROUTE_STATUS,
								Common.integerArrayToIntArray(this.content.get(FrameParser.RELAY_ROUTE_STATUS)));
					}
					break;
					
				case 6:
					if(columnIndex == 0) {
						value = Common.paramDescription.get(FrameParser.POLLING_TIME).getName();
					} else {
						value = FrameParser.parseParameterValue(FrameParser.POLLING_TIME,
								Common.integerArrayToIntArray(this.content.get(FrameParser.POLLING_TIME)));
					}
					break;
					
				case 7:
					if(columnIndex == 0) {
						value = Common.paramDescription.get(FrameParser.RADIO_USER_TIMEOUT).getName();
					} else {
						value = FrameParser.parseParameterValue(FrameParser.RADIO_USER_TIMEOUT,
								Common.integerArrayToIntArray(this.content.get(FrameParser.RADIO_USER_TIMEOUT)));
					}
					break;
					
				case 8:
					if(columnIndex == 0) {
						value = Common.paramDescription.get(FrameParser.EXCHANGE_STATUS).getName();
					} else {
						value = FrameParser.parseParameterValue(FrameParser.EXCHANGE_STATUS,
								Common.integerArrayToIntArray(this.content.get(FrameParser.EXCHANGE_STATUS)));
					}
					break;
					
				case 9:
					if(columnIndex == 0) {
						value = Common.paramDescription.get(FrameParser.SWITCH_MODE_SATUS).getName();
					} else {
						value = FrameParser.parseParameterValue(FrameParser.SWITCH_MODE_SATUS,
								Common.integerArrayToIntArray(this.content.get(FrameParser.SWITCH_MODE_SATUS)));
					}
					break;
					
				case 10:
					if(columnIndex == 0) {
						value = Common.paramDescription.get(FrameParser.BCST_RECEPTION_TIMEOUT).getName();
					} else {
						value = FrameParser.parseParameterValue(FrameParser.BCST_RECEPTION_TIMEOUT,
								Common.integerArrayToIntArray(this.content.get(FrameParser.BCST_RECEPTION_TIMEOUT)));
					}
					break;
				
				default:
					break;
			}
			
			return value;
		}
		
		@Override
		public String getColumnName(final int columnIndex) {
			return this.column[columnIndex];
		}
	}
}
