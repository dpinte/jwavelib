package gui;

import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import frame.*;

//TODO: use this model instead of default tree model

@SuppressWarnings("unused")
public class FrameDetailModel implements TreeModel, ListSelectionListener {
	private FrameContainer container;
	
	public FrameDetailModel(MutableTreeNode root, FrameContainer container) {		
		this.container = container;
	}

	public void valueChanged(ListSelectionEvent event){
		DefaultListSelectionModel sel = (DefaultListSelectionModel) event.getSource();
		System.out.println(sel.getMaxSelectionIndex());
		//this.setFrame(this.container.getFrameAt(sel.getMaxSelectionIndex()));
	}

	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getChild(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChildCount(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIndexOfChild(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLeaf(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
}
