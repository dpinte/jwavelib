package com.dipole.jwavetool.gui.statistics;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import com.dipole.jwavetool.frame.Description;
import com.dipole.jwavetool.frame.FrameAnalyser;
import com.dipole.jwavetool.frame.FrameContainer;

import com.dipole.jwavetool.common.Common;
import com.dipole.jwavetool.common.Log;
import com.dipole.jwavetool.common.Settings;

public class StatFramePanel extends JScrollPane {

	private JTable table;
	private TableModel model;

	public StatFramePanel(final Settings settings) {
		this.model = new TableModel();
		this.table = new JTable(this.model);
		this.table.setDefaultRenderer(Object.class, new Renderer(settings));
		this.table.setRowSelectionAllowed(false);
		this.table.setColumnSelectionAllowed(false);
		this.table.setShowGrid(true);
		
		this.add(this.table);
		
		this.refresh();
		
		this.setViewportView(this.table);
	}

	public void refresh(){
		this.model.setContent(FrameAnalyser.countFrameByType(FrameContainer.getInstance()));
	}
	
	private class TableModel extends AbstractTableModel{
		private HashMap <Integer, Integer> content = new HashMap <Integer, Integer> ();
		private String[] column = {"Frame Command", "Count"};
		
		public TableModel(){}
		
		public void setContent(final HashMap <Integer, Integer> content){
			this.content = content;

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
			if(columnIndex == 0) {
				int cmd = Integer.parseInt(this.content.keySet().toArray()[rowIndex].toString());
				Description desc = Common.cmdDescription.get(cmd);
				
				if( desc == null){
					return "Unknown: "+ cmd;
				} else {
					return desc.getName();
				}
				
			} else {
				return this.content.values().toArray()[rowIndex];
			}
		}
		
		@Override
		public String getColumnName(final int columnIndex) {
			return this.column[columnIndex];
		}
	}
	
	private class Renderer implements TableCellRenderer {
		private Settings settings;
		private FrameContainer container;
		
		public Renderer(final Settings settings){
			this.settings = settings;
			this.container = FrameContainer.getInstance();
		}
		
		public Component getTableCellRendererComponent(final JTable table, final Object value,
														final boolean isSelected, final boolean hasFocus,
														final int row, final int column){
			JLabel label = null;
			JProgressBar bar = null;
			
			if(value.getClass() == String.class){
				label = new JLabel(value.toString());
				label.setOpaque(true);
				
				if(isSelected){
					label.setBackground(this.settings.getColor(Settings.SEL_BG));
					label.setForeground(this.settings.getColor(Settings.SEL_FG));
				} else {
					if(row % 2 == 0) {
						label.setBackground(table.getBackground());
					} else {
						label.setBackground(Color.CYAN);
					}
					
					label.setForeground(table.getForeground());
				}
				return label;
			} else {
				bar = new JProgressBar(0, this.container.getTotalFrames());
				bar.setString(((Integer)value).toString());
				bar.setStringPainted(true);
				bar.setValue(((Integer)value).intValue());
				return bar;
			}
			
		}
	}
}
