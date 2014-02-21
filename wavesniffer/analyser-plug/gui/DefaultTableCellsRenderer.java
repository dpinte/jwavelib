package gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import common.Settings;

public class DefaultTableCellsRenderer extends JLabel implements TableCellRenderer{
	private Settings settings;
	
	public DefaultTableCellsRenderer(final Settings settings) {
		super();
		this.setOpaque(true);
		this.settings = settings;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
													boolean isSelected, boolean hasFocus,
													int row, int column){
		if(isSelected){
			this.setBackground(this.settings.getColor(Settings.SEL_BG));
			this.setForeground(this.settings.getColor(Settings.SEL_FG));
		} else {
			if(row % 2 == 0) {
				this.setBackground(table.getBackground());
			} else {
				this.setBackground(Color.CYAN);
			}
			
			this.setForeground(table.getForeground());
		}
		
		this.setText(value.toString());
		return this;
	}
}
