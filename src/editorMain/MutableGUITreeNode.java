package editorMain;

import javax.swing.tree.DefaultMutableTreeNode;

import editorMain.guitypes.BaseGUIType;

public class MutableGUITreeNode extends DefaultMutableTreeNode {

	public MutableGUITreeNode(String label)
	{
		super(label);
	}
	
	/**
	 * Serial VersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	BaseGUIType linkedElement;
}
