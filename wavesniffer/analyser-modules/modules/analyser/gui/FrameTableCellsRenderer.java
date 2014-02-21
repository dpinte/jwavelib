package modules.analyser.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import common.Settings;

public class FrameTableCellsRenderer extends JLabel implements TableCellRenderer {
	private Settings settings;
	
	public FrameTableCellsRenderer(Settings settings) {
		this.settings = settings;
		this.setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
													boolean isSelected, boolean hasFocus,
													int row, int column){	
		String[] vals = null;
		boolean hl = false;
		
		if(value != null){
			vals = value.toString().split(",");
		}
		
		if(vals.length == 3){
			if(vals[2].equals("HL"))
				hl = true;
		}
		
		if(isSelected){
			this.setBackground(this.settings.getColor(Settings.SEL_BG));
			this.setForeground(this.settings.getColor(Settings.SEL_FG));
		} else {
			if (hl){
				this.setBackground(this.settings.getColor(Settings.HIG_BG));
				this.setForeground(this.settings.getColor(Settings.HIG_FG));
			} else if(vals[1].equals("CRCE") || vals[1].equals("STXE") || vals[1].equals("ETXE")){
				this.setBackground(this.settings.getColor(Settings.CRIT_ERR_BG));
				this.setForeground(this.settings.getColor(Settings.CRIT_ERR_FG));
			} else if(vals[1].equals("TSE")){
				this.setBackground(this.settings.getColor(Settings.WARN_BG));
				this.setForeground(this.settings.getColor(Settings.WARN_FG));
			} else {
				if(row % 2 == 0){
					this.setBackground(table.getBackground());
				} else {
					this.setBackground(Color.CYAN);
				}
				this.setForeground(table.getForeground());
			}
		}
		
		this.setText(vals[0]);
		
		return this;
	}
}
