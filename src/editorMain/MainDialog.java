package editorMain;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import editorMain.guitypes.BaseGUIType;
import editorMain.guitypes.GUIActivity;
import editorMain.guitypes.GUIElement;
import editorMain.guitypes.MobileApplication;

import javax.swing.JTextPane;

public class MainDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	JProgressBar m_pLoadingProgressBar = new JProgressBar();
	JSONParserClass m_pJsonParser = new JSONParserClass(this);
	JLabel m_pLoadingLabel = new JLabel("Ready");
	private static MainDialog dialog;
    private JTree m_pCategoryTree;
    private String m_pFileExtension = ".json";

	private File m_pOutputDirectory;
    private MobileApplication m_pMobileApplication;
    
    private JTextPane m_pStatusMessagesPane;
    
    private void addStatusMessage(String message)
    {
    	m_pStatusMessagesPane.setText(m_pStatusMessagesPane.getText() + "\r\n" + message);
    }
	
	private final ActionListener m_pfileOpenListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			FileDialog dlg = new FileDialog(MainDialog.this);
			dlg.setFilenameFilter(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".json");
				}
			});
			dlg.setVisible(true);
			
			if(dlg.getFile() == "" || dlg.getFile() == null)
				return;
			
			if(!dlg.getFile().endsWith(m_pFileExtension))
			{
				JOptionPane.showMessageDialog(MainDialog.this, "Bitte waehlen Sie eine gueltige JSON-Datei aus!");
				return;
			}

			m_pLoadingProgressBar.setIndeterminate(true);
			m_pLoadingLabel.setText("Loading...");
			m_pJsonParser.parse(dlg.getDirectory(), dlg.getFile());
		}
	};
	
	private final ActionListener m_pGenerateProjectsButtonListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			m_pStatusMessagesPane.setText("");
			File projectDir;
			String outPath = m_pOutputDirectory + "/MobileApplicationProjects/";
			if(m_pMobileApplication.getName() != null)
				projectDir = new File(outPath + m_pMobileApplication.getName());
			else
				projectDir = new File(outPath + "testProject");
			
			addStatusMessage("Creating path...");
			
			try {
				// IOS
				addStatusMessage("Creating IOS Storyboard file...");
				File projectDirIOS = new File(projectDir + "/IOS");
				projectDirIOS.mkdirs();

				File mainStoryboard = new File(projectDirIOS + "/Main.storyboard");
				FileWriter writer = new FileWriter(mainStoryboard);
				writer.write(m_pMobileApplication.toIOSXMLString());
				writer.close();
				
				for(GUIActivity activity: m_pMobileApplication.getActivities())
				{
					if(activity.getSwiftFileContent() != null)
					{
						File mainSwiftFile = new File(projectDirIOS + "/Main.swift");
						FileWriter swiftWriter = new FileWriter(mainSwiftFile);
						swiftWriter.write(activity.getSwiftFileContent());
						swiftWriter.close();
					}
				}
				
				// Android
				addStatusMessage("Creating Android activity...");
				File projectDirAndroid = new File(projectDir + "/Android");
				projectDirAndroid.mkdirs();

				int activity_index = 0;
				for(String fileContent: m_pMobileApplication.toAndroidXMLString())
				{
					activity_index++;
					File mainActivity = new File(projectDirAndroid + "/activity" + activity_index + ".xml");
					writer = new FileWriter(mainActivity);
					writer.write(fileContent);
					writer.close();
				}
				
				for(GUIActivity activity: m_pMobileApplication.getActivities())
				{
					if(activity.getJavaFileContent() != null)
					{
						File mainAndroidFile = new File(projectDirAndroid + "/MainActivity.java");
						FileWriter androidWriter = new FileWriter(mainAndroidFile);
						androidWriter.write(activity.getJavaFileContent());
						androidWriter.close();
					}
				}
				
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			addStatusMessage("Finished.");

			String confirmMessage = "Your project files have been generated successfully. Do you want to open the folder?";

			int result = JOptionPane.showConfirmDialog(MainDialog.this, confirmMessage);
			if(result == 0)
			{
				try {
					Desktop.getDesktop().open(projectDir);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	};
	
	private final ActionListener m_pSelectDirectoryListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JFileChooser dlg = new JFileChooser();
			dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			dlg.showOpenDialog(MainDialog.this);
			m_pDirectoryField.setText(dlg.getSelectedFile().getAbsolutePath());
			m_pOutputDirectory = dlg.getSelectedFile();
		}
	};
	
	private final TreeSelectionListener m_pTreeSelectionListener = new TreeSelectionListener() {
		
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			// TODO Auto-generated method stub
			MutableGUITreeNode source = (MutableGUITreeNode)m_pCategoryTree.getLastSelectedPathComponent();
			BaseGUIType sourceElement = (BaseGUIType)source.linkedElement;
			if(sourceElement == null)
			{
				return;
			}
			TableModel model = m_pTable.getModel();
			model.setValueAt("Name", 0, 0);
			model.setValueAt("PositionX", 1, 0);
			model.setValueAt("PositionY", 2, 0);
			model.setValueAt("Width", 3, 0);
			model.setValueAt("Height", 4, 0);
			model.setValueAt("Label", 5, 0);
			model.setValueAt("Generated ID", 6, 0);

			if(sourceElement == null)
				return;
			
			model.setValueAt(sourceElement.getName(), 0, 1);
			model.setValueAt(sourceElement.getPosition().getX(), 1, 1);
			model.setValueAt(sourceElement.getPosition().getY(), 2, 1);
			model.setValueAt(sourceElement.getSize().getX(), 3, 1);
			model.setValueAt(sourceElement.getSize().getY(), 4, 1);
			model.setValueAt(sourceElement.getGeneratedID(), 6, 1);

			if(sourceElement.getType() != null)
			{
				if(sourceElement.getType().equals("button"))
				{
					GUIElement p_element = (GUIElement)sourceElement;
					model.setValueAt(p_element.getLabel(), 5, 1);
				}
				else
				{
					model.setValueAt("<element type does not support label>", 5, 1);
				}
			}
		}
	};
	
	private final ActionListener m_pQuitApplicationListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	};
	private JTable m_pTable;
	private JTextField m_pDirectoryField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			dialog = new MainDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void createTreeFromActivityList(final MobileApplication application)
    {
    	m_pMobileApplication = application;
    	MutableGUITreeNode _defMutableTreeNode = new MutableGUITreeNode("Activities") {
	    	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
		    	for(GUIActivity activity: application.m_pActivities)
		    	{
		    		MutableGUITreeNode node = new MutableGUITreeNode(activity.getName());
		    		node.linkedElement = activity;
		    		add(node);

		    		for(GUIElement element: activity.m_pElements)
		    		{
		    			MutableGUITreeNode elementNode = new MutableGUITreeNode(element.getObjectName());
		    			elementNode.linkedElement = element;
		    			node.add(elementNode);
		    		}
		    	}
	    	}
    	};
    	DefaultTreeModel _defDefaultTreeModel = new DefaultTreeModel(_defMutableTreeNode);
    	m_pCategoryTree.setModel(_defDefaultTreeModel);
        m_pCategoryTree.addTreeSelectionListener(this.m_pTreeSelectionListener);
        m_pLoadingProgressBar.setIndeterminate(false);
        m_pLoadingLabel.setText("Ready");
    }

	/**
	 * Create the dialog.
	 */
	public MainDialog() {
		setBounds(100, 100, 700, 520);
		getContentPane().setLayout(new BorderLayout());
		{
			JMenuBar menuBar = new JMenuBar();
			getContentPane().add(menuBar, BorderLayout.NORTH);
			{
				JMenu mnNewMenu = new JMenu("File");
				mnNewMenu.setMnemonic('F');
				menuBar.add(mnNewMenu);
				{
					JMenuItem mntmFile = new JMenuItem("Load definition...");
					mntmFile.addActionListener(m_pfileOpenListener);
					mnNewMenu.add(mntmFile);
				}
			}
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane);
			JSplitPane splitPane = new JSplitPane();
			tabbedPane.addTab("Application data", null, splitPane, null);
			{
				JPanel panel = new JPanel();
				splitPane.setLeftComponent(panel);
				{
					m_pCategoryTree = new JTree();
					m_pCategoryTree.setModel(new DefaultTreeModel(
						new DefaultMutableTreeNode("Application") {
							/**
							 * 
							 */
							private static final long serialVersionUID = 1L;

							{
						//		getContentPane().add(new DefaultMutableTreeNode("(Activities)"));
							}
						}
					));
					m_pCategoryTree.setBorder(null);
					panel.add(m_pCategoryTree);
				}
			}
			JPanel panel_1 = new JPanel();
			splitPane.setRightComponent(panel_1);
			panel_1.setLayout(new CardLayout(0, 0));
			
			String[] tableNames = {"Name", "Value"};
			JTableHeader header = new JTableHeader();
			TableModel model = new DefaultTableModel(tableNames, 10);
			m_pTable = new JTable();
			m_pTable.setModel(model);
			m_pTable.setEnabled(false);
			m_pTable.setTableHeader(header);
			panel_1.add(m_pTable, "name_54266533460969");
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab("Generator", null, panel, null);
				panel.setLayout(null);
				
				JLabel lblSelectOutputDirectory = new JLabel("Select output directory...");
				lblSelectOutputDirectory.setFont(new Font("Lucida Grande", Font.BOLD, 15));
				lblSelectOutputDirectory.setBounds(6, 6, 239, 16);
				panel.add(lblSelectOutputDirectory);
				
				m_pDirectoryField = new JTextField();
				m_pDirectoryField.setEnabled(false);
				m_pDirectoryField.setEditable(false);
				m_pDirectoryField.setBounds(91, 34, 443, 28);
				panel.add(m_pDirectoryField);
				m_pDirectoryField.setColumns(10);
				
				JButton btnSelect = new JButton("Browse...");
				btnSelect.addActionListener(m_pSelectDirectoryListener);
				btnSelect.setBounds(546, 35, 117, 29);
				panel.add(btnSelect);
				
				m_pStatusMessagesPane = new JTextPane();
				m_pStatusMessagesPane.setBounds(6, 73, 657, 302);
				panel.add(m_pStatusMessagesPane);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				buttonPane.add(m_pLoadingProgressBar);
			}
			{
				buttonPane.add(m_pLoadingLabel);
			}
			{
				JButton okButton = new JButton("Generate projects");
				okButton.addActionListener(m_pGenerateProjectsButtonListener);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(m_pQuitApplicationListener);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
