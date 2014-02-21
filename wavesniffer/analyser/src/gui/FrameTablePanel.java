package gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import common.Settings;

import frame.FrameAnalyser;
import frame.FrameContainer;

public class FrameTablePanel extends JScrollPane implements MouseListener,
															ActionListener {
	private FrameContainer container;
	private JTable table;
	private FrameTableModel tableModel;
	private JPopupMenu poppuMenu;
	private JMenuItem showRelatedItem, hlRelatedItem; 
	
	public FrameTablePanel(FrameContainer container, Settings settings){
		super();
		
		this.container = container;
		
		this.showRelatedItem = new JMenuItem("Show related frames");
		this.showRelatedItem.addActionListener(this);
		this.hlRelatedItem = new JMenuItem("Highlight related frames");
		this.hlRelatedItem.addActionListener(this);
		this.poppuMenu = new JPopupMenu();
		this.poppuMenu.add(this.showRelatedItem);
		this.poppuMenu.add(this.hlRelatedItem);
		
		this.tableModel = new FrameTableModel(this.container);
		this.container.addFrameContainerListener(this.tableModel);
		
		this.table = new JTable(this.tableModel);
		this.table.setDefaultRenderer(Object.class, new FrameTableCellsRenderer(settings));
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setColumnSelectionAllowed(false);
		this.table.setShowGrid(true);
		
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.table.getColumnModel().getColumn(0).setMaxWidth(50);
		this.table.getColumnModel().getColumn(1).setMaxWidth(150);
		this.table.getColumnModel().getColumn(2).setMaxWidth(100);
		this.table.addMouseListener(this);
		
		this.setViewportView(this.table);
	}
	
	public void setSelectionListener(ListSelectionListener listener){
		this.table.getSelectionModel().addListSelectionListener(listener);
	}
	
	public JTable getTable(){
		return this.table;
	}
	
	/**
	 * select the next row in the table
	 * @return true the end of the table isn't reached
	 */
	public boolean selectNextRow(){
		if(this.table.getRowCount() > 0){
			if(this.table.getSelectedRow() < (this.table.getRowCount() - 1)){
				this.table.setRowSelectionInterval(this.table.getSelectedRow() + 1, this.table.getSelectedRow() + 1);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * select the previous row in the table
	 * @return true if the beginning of the table isn't reached
	 */
	public boolean selectPrevRow(){
		if(this.table.getRowCount() > 0){
			if(this.table.getSelectedRow() > 0) {
				this.table.setRowSelectionInterval(this.table.getSelectedRow() - 1, this.table.getSelectedRow() - 1);
				return true;
			}
		}
		return false;
	}

	public void mouseClicked(MouseEvent event) {
		if(event.getButton() == 3){
			Point point = new Point(event.getX(), event.getY());
			this.table.setRowSelectionInterval(this.table.rowAtPoint(point), this.table.rowAtPoint(point));
			this.poppuMenu.show(this.table, event.getX(), event.getY());
		}		
	}

	public void mouseEntered(MouseEvent event) {}

	public void mouseExited(MouseEvent event) {}

	public void mousePressed(MouseEvent event) {}

	public void mouseReleased(MouseEvent event) {}

	public void actionPerformed(ActionEvent event) {
		FrameTableModel model = (FrameTableModel)this.table.getModel();
		int frameInd = model.getFrameIndex(this.table.getSelectedRow());
		
		if(event.getSource() == this.showRelatedItem){
			FrameAnalyser.filterRelatedFrame(this.container, frameInd, true);
		} else {
			FrameAnalyser.highlightRelatedFrames(this.container, frameInd, false);
		}
	}
}
