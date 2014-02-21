package gui;

import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import common.Common;
import common.ParameterList;
import common.Settings;

import frame.FrameAnalyser;
import frame.FrameContainer;

public class WpConfigPanel extends JScrollPane
{
	private FrameContainer container;
	private JTable table;
	private TableModel model;

	public WpConfigPanel(FrameContainer container, Settings settings) {
		this.container = container;
		
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
		this.model.setContent(FrameAnalyser.scanRadioParameters(this.container));
	}
	
	private class TableModel extends AbstractTableModel{
		private HashMap <Integer, Integer[]> content = new HashMap <Integer, Integer[]> ();
		private String[] column = {"Parameter Name", "Parameter Value"};
		
		public TableModel(){
			
		}
		
		public void setContent(HashMap <Integer, Integer[]> content){
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
		public Object getValueAt(int rowIndex, int columnIndex) {
			String value = null;
			switch(rowIndex){
				case 0:
					if(columnIndex == 0)
						value = ParameterList.getParameterName(ParameterList.AWAKENING_PERIOD);
					else
						value = ParameterList.parseParameterValue(ParameterList.AWAKENING_PERIOD,
																Common.integerArrayToIntArray(this.content.get(ParameterList.AWAKENING_PERIOD)));
					break;
					
				case 1:
					if(columnIndex == 0)
						value = ParameterList.getParameterName(ParameterList.WAKEUP_TYPE);
					else
						value = ParameterList.parseParameterValue(ParameterList.WAKEUP_TYPE,
								Common.integerArrayToIntArray(this.content.get(ParameterList.WAKEUP_TYPE)));
					break;
					
				case 2:
					if(columnIndex == 0)
						value = ParameterList.getParameterName(ParameterList.WAKEUP_LENGTH);
					else
						value = ParameterList.parseParameterValue(ParameterList.WAKEUP_LENGTH,
								Common.integerArrayToIntArray(this.content.get(ParameterList.WAKEUP_LENGTH)));
					break;
					
				case 3:
					if(columnIndex == 0)
						value = ParameterList.getParameterName(ParameterList.RADIO_ACKNOWLEGDE);
					else
						value = ParameterList.parseParameterValue(ParameterList.RADIO_ACKNOWLEGDE,
								Common.integerArrayToIntArray(this.content.get(ParameterList.RADIO_ACKNOWLEGDE)));
					break;
					
				case 4:
					if(columnIndex == 0)
						value = ParameterList.getParameterName(ParameterList.RADIO_ADDRESS);
					else
						value = ParameterList.parseParameterValue(ParameterList.RADIO_ADDRESS,
								Common.integerArrayToIntArray(this.content.get(ParameterList.RADIO_ADDRESS)));
					break;
					
				case 5:
					if(columnIndex == 0)
						value = ParameterList.getParameterName(ParameterList.RELAY_ROUTE_STATUS);
					else
						value = ParameterList.parseParameterValue(ParameterList.RELAY_ROUTE_STATUS,
								Common.integerArrayToIntArray(this.content.get(ParameterList.RELAY_ROUTE_STATUS)));
					break;
					
				case 6:
					if(columnIndex == 0)
						value = ParameterList.getParameterName(ParameterList.POLLING_TIME);
					else
						value = ParameterList.parseParameterValue(ParameterList.POLLING_TIME,
								Common.integerArrayToIntArray(this.content.get(ParameterList.POLLING_TIME)));
					break;
					
				case 7:
					if(columnIndex == 0)
						value = ParameterList.getParameterName(ParameterList.RADIO_USER_TIMEOUT);
					else
						value = ParameterList.parseParameterValue(ParameterList.RADIO_USER_TIMEOUT,
								Common.integerArrayToIntArray(this.content.get(ParameterList.RADIO_USER_TIMEOUT)));
					break;
					
				case 8:
					if(columnIndex == 0)
						value = ParameterList.getParameterName(ParameterList.EXCHANGE_STATUS);
					else
						value = ParameterList.parseParameterValue(ParameterList.EXCHANGE_STATUS,
								Common.integerArrayToIntArray(this.content.get(ParameterList.EXCHANGE_STATUS)));
					break;
					
				case 9:
					if(columnIndex == 0)
						value = ParameterList.getParameterName(ParameterList.SWITCH_MODE_SATUS);
					else
						value = ParameterList.parseParameterValue(ParameterList.SWITCH_MODE_SATUS,
								Common.integerArrayToIntArray(this.content.get(ParameterList.SWITCH_MODE_SATUS)));
					break;
					
				case 10:
					if(columnIndex == 0)
						value = ParameterList.getParameterName(ParameterList.BCST_RECEPTION_TIMEOUT);
					else
						value = ParameterList.parseParameterValue(ParameterList.BCST_RECEPTION_TIMEOUT,
								Common.integerArrayToIntArray(this.content.get(ParameterList.BCST_RECEPTION_TIMEOUT)));
					break;
			}
			
			return value;
		}
		
		@Override
		public String getColumnName(int columnIndex) {
			return this.column[columnIndex];
		}
	}
}
