package gui;

import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import common.Settings;

import frame.FrameAnalyser;
import frame.FrameContainer;

public class DetectedModulesPanel extends JScrollPane {
	private FrameContainer container;
	private JTable table;
	private TableModel model;
	
	public DetectedModulesPanel(FrameContainer container, Settings settings) {
		this.container = container;
		
		this.model = new TableModel();
		this.table = new JTable(this.model);
		this.table.setDefaultRenderer(Object.class, new DefaultTableCellsRenderer(settings));
		this.table.setRowSelectionAllowed(true);
		this.table.setColumnSelectionAllowed(false);
		this.table.setShowGrid(true);
		
		this.add(this.table);
		
		
		
		this.setViewportView(this.table);
	}

	public void refresh(){
		ArrayList <String> list = FrameAnalyser.getAllModulesId(this.container);
		
		for(String value : list){
			this.model.addModule(value, 1, 1);
		}
		
	}
	
	private class TableModel extends AbstractTableModel{
		private ArrayList <String[]> content = new ArrayList<String[]> ();
		private String[] column = {"ModuleID", "RX count", "TX count"};
		
		public TableModel(){
			
		}
		
		public void addModule(String moduleID, int RXCount, int TXCount){
			String[] str = new String[3];
			
			str[0] = moduleID;
			str[1] = Integer.toString(RXCount);
			str[2] = Integer.toString(TXCount);
			
			this.content.add(str);
			this.fireTableDataChanged();
		}
		
		@Override
		public int getColumnCount() {
			return this.column.length;
		}

		@Override
		public int getRowCount() {
			return this.content.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return this.content.get(rowIndex)[columnIndex];
		}
		
		@Override
		public String getColumnName(int columnIndex) {
			return this.column[columnIndex];
		}
	}
}
