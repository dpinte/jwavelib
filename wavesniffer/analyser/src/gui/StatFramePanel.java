package gui;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import common.Settings;

import frame.FrameAnalyser;
import frame.FrameContainer;

public class StatFramePanel extends JScrollPane {
	private FrameContainer container;
	private JTable table;
	private TableModel model;

	public StatFramePanel(FrameContainer container, Settings settings) {
		this.container = container;
		this.model = new TableModel();
		this.table = new JTable(this.model);
		this.table.setDefaultRenderer(Object.class, new Renderer(settings, this.container));
		this.table.setRowSelectionAllowed(false);
		this.table.setColumnSelectionAllowed(false);
		this.table.setShowGrid(true);
		
		this.add(this.table);
		
		this.refresh();
		
		this.setViewportView(this.table);
	}

	public void refresh(){
		this.model.setContent(FrameAnalyser.countFrameByType(this.container));
	}
	
	private class TableModel extends AbstractTableModel{
		private HashMap <Integer, Integer> content = new HashMap <Integer, Integer> ();
		private String[] column = {"Frame Command", "Count"};
		
		public TableModel(){}
		
		public void setContent(HashMap <Integer, Integer> content){
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
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(columnIndex == 0)
				return this.content.keySet().toArray()[rowIndex].toString();
			else
				return this.content.values().toArray()[rowIndex];
		}
		
		@Override
		public String getColumnName(int columnIndex) {
			return this.column[columnIndex];
		}
	}
	
	private class Renderer implements TableCellRenderer {
		private Settings settings;
		private FrameContainer container;
		
		public Renderer(Settings settings, FrameContainer container){
			this.settings = settings;
			this.container = container;
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value,
														boolean isSelected, boolean hasFocus,
														int row, int column){
			JLabel label = null;
			JProgressBar bar = null;
			
			if(value.getClass() == String.class){
				label = new JLabel(value.toString());
				label.setOpaque(true);
				
				if(isSelected){
					label.setBackground(this.settings.getColor(Settings.SEL_BG));
					label.setForeground(this.settings.getColor(Settings.SEL_FG));
				} else {
					if(row % 2 == 0)
						label.setBackground(table.getBackground());
					else
						label.setBackground(Color.CYAN);
					
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
