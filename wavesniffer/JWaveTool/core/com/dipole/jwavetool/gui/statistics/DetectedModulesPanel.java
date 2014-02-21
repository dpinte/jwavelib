package com.dipole.jwavetool.gui.statistics;

import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.dipole.jwavetool.frame.FrameAnalyser;
import com.dipole.jwavetool.frame.FrameContainer;

import com.dipole.jwavetool.common.Settings;

import com.dipole.jwavetool.gui.DefaultTableCellsRenderer;

public class DetectedModulesPanel extends JScrollPane {
	private JTable table;
	private TableModel model;
	
	public DetectedModulesPanel(final Settings settings) {
		
		this.model = new TableModel();
		this.table = new JTable(this.model);
		this.table.setDefaultRenderer(Object.class, new DefaultTableCellsRenderer(settings));
		this.table.setRowSelectionAllowed(true);
		this.table.setColumnSelectionAllowed(false);
		this.table.setShowGrid(true);
		
		this.refresh();
		
		this.add(this.table);
		
		this.setViewportView(this.table);
	}

	public void refresh() {
		ArrayList <String> list = FrameAnalyser.getAllModulesId( FrameContainer.getInstance());
		
		for(String value : list) {
			this.model.addModule(value, 1, 1);
		}
	}
	
	private class TableModel extends AbstractTableModel{
		private ArrayList <String[]> content = new ArrayList<String[]> ();
		private String[] column = {"ModuleID", "RX count", "TX count"};
		
		public TableModel(){
			
		}
		
		public void addModule(final String moduleID, final int RXCount, final int TXCount) {
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
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			return this.content.get(rowIndex)[columnIndex];
		}
		
		@Override
		public String getColumnName(final int columnIndex) {
			return this.column[columnIndex];
		}
	}
}
