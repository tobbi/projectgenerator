package editorMain;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;

public class MainDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	JProgressBar m_pLoadingProgressBar = new JProgressBar();
	JavaCodeParser m_pJavaCodeParser = new JavaCodeParser();
	JLabel m_pLoadingLabel = new JLabel("Ready");
	JButton m_pOkButton = new JButton("Generate");
	private static MainDialog dialog;
    private String m_pFileExtension = ".java";

	private File m_pOutputDirectory;
    
    private void addStatusMessage(String message)
    {
    	//m_pStatusMessagesPane.setText(m_pStatusMessagesPane.getText() + "\r\n" + message);
    }
	
	private final ActionListener m_pfileOpenListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			FileDialog dlg = new FileDialog(MainDialog.this);
			dlg.setFilenameFilter(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(m_pFileExtension);
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

			textField.setText(dlg.getDirectory() + dlg.getFile());
			if(m_pOutputDirectory != null)
			{
				m_pOkButton.setEnabled(true);
			}
			m_pLoadingProgressBar.setIndeterminate(true);
			m_pLoadingLabel.setText("Loading...");
			parseSourceFile(dlg.getDirectory() + dlg.getFile());
		}
	};
	
	public void parseSourceFile(String sourceFilePath) {
		File sourceFile = new File(sourceFilePath);

		// Source http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html
		FileReader fileReader;
		try {
			fileReader = new FileReader(sourceFile);
		} catch (FileNotFoundException e) {
			System.out.printf("Specified source file %s does not exist. Ignoring.",
					  sourceFile.getAbsolutePath());
			return;
		}
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuffer stringBuffer = new StringBuffer();
		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fileReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.printf("== Parsing Java source file %s ==\r\n", sourceFilePath);
		m_pJavaCodeParser.parse(stringBuffer.toString());
	}
	
	private final ActionListener m_pGenerateProjectsButtonListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			//m_pStatusMessagesPane.setText("");
			File projectDir;
			String outPath = m_pOutputDirectory + "/JavaToMobileConverter/";
			projectDir = new File(outPath);
			
			addStatusMessage("Creating path...");
			
			try {
				// IOS
				addStatusMessage("Creating IOS Storyboard file...");
				File projectDirIOS = new File(projectDir + "/IOS");
				projectDirIOS.mkdirs();

				// Copy include files:
				File iosBridgeFiles = new File("target_includes/ios_bridges");
				FileUtils.copyDirectory(iosBridgeFiles, projectDirIOS);
				
				File mainSwiftFile = new File(projectDirIOS + "/ViewController.swift");
				FileWriter swiftWriter = new FileWriter(mainSwiftFile);
				swiftWriter.write(m_pJavaCodeParser.getSwiftFileContent());
				swiftWriter.close();
				
				// Android
				addStatusMessage("Creating Android activity...");
				File projectDirAndroid = new File(projectDir + "/Android");
				projectDirAndroid.mkdirs();
				
				// Copy include files
				File androidBridgeFiles = new File("target_includes/android_bridges");
				FileUtils.copyDirectory(androidBridgeFiles, projectDirAndroid);

				File mainAndroidFile = new File(projectDirAndroid + "/MainActivity.java");
				FileWriter androidWriter = new FileWriter(mainAndroidFile);
				androidWriter.write(m_pJavaCodeParser.getAndroidFileContent());
				androidWriter.close();
				
				
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			addStatusMessage("Finished.");
			m_pLoadingProgressBar.setIndeterminate(false);

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

			if(dlg.getSelectedFile() == null)
				return;

			m_pDirectoryField.setText(dlg.getSelectedFile().getAbsolutePath());
			if(!textField.getText().trim().equals(""))
			{
				m_pOkButton.setEnabled(true);
			}
			m_pOutputDirectory = dlg.getSelectedFile();
		}
	};
	
	private final ActionListener m_pQuitApplicationListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	};
	private JTextField m_pDirectoryField;
	private JTextField textField;

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

	/**
	 * Create the dialog.
	 */
	public MainDialog() {
		setBounds(100, 100, 700, 520);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane);
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab("Generator", null, panel, null);
				panel.setLayout(null);
				
				JLabel lblSelectOutputDirectory = new JLabel("2. Select output directory...");
				lblSelectOutputDirectory.setFont(new Font("Lucida Grande", Font.BOLD, 15));
				lblSelectOutputDirectory.setBounds(6, 74, 239, 16);
				panel.add(lblSelectOutputDirectory);
				
				m_pDirectoryField = new JTextField();
				m_pDirectoryField.setEnabled(false);
				m_pDirectoryField.setEditable(false);
				m_pDirectoryField.setBounds(91, 102, 443, 28);
				panel.add(m_pDirectoryField);
				m_pDirectoryField.setColumns(10);
				
				JButton btnSelect = new JButton("Browse...");
				btnSelect.addActionListener(m_pSelectDirectoryListener);
				btnSelect.setBounds(546, 103, 117, 29);
				panel.add(btnSelect);
				
				JLabel lblLoadDefinition = new JLabel("1. Load definition file...");
				lblLoadDefinition.setFont(new Font("Lucida Grande", Font.BOLD, 15));
				lblLoadDefinition.setBounds(6, 6, 239, 16);
				panel.add(lblLoadDefinition);
				
				textField = new JTextField();
				textField.setEnabled(false);
				textField.setEditable(false);
				textField.setColumns(10);
				textField.setBounds(91, 34, 443, 28);
				panel.add(textField);
				
				JButton button = new JButton("Browse...");
				button.setBounds(546, 35, 117, 29);
				button.addActionListener(m_pfileOpenListener);
				panel.add(button);
				m_pOkButton.setFont(new Font("Lucida Grande", Font.BOLD, 20));
				m_pOkButton.setBounds(224, 316, 231, 59);
				panel.add(m_pOkButton);
				m_pOkButton.addActionListener(m_pGenerateProjectsButtonListener);
				m_pOkButton.setActionCommand("OK");
				getRootPane().setDefaultButton(m_pOkButton);
				
				JLabel lblClickgenerate = new JLabel("3. Click \"Generate\" button");
				lblClickgenerate.setFont(new Font("Lucida Grande", Font.BOLD, 15));
				lblClickgenerate.setBounds(6, 142, 239, 16);
				panel.add(lblClickgenerate);
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
				if(m_pOutputDirectory == null)
				{
					m_pOkButton.setEnabled(false);
				}
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
