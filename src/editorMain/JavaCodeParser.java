package editorMain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.*;

public class JavaCodeParser {
	
	private String m_pSwiftFileContent = "";
	private String m_pAndroidFileContent = "";
	private ArrayList<String> m_pOptionalVars = new ArrayList<String>();
	private HashMap<String, String[]> m_pFunctionParamLabels = getPublicAPI();

	private void addToSwiftFile(String text)
	{
		// Insert code at the right position:
		if(templateContext == TemplateContext.CLASS)
		{
			m_pSwiftFileContent = m_pSwiftFileContent.replace("@classContext", text + "@classContext");
		}
		else
		{
			m_pSwiftFileContent = m_pSwiftFileContent.replace("@mainContext", text + "@mainContext");
		}
	}
	
	private void addToAndroidFile(String text)
	{
		if(templateContext == TemplateContext.CLASS)
		{
			m_pAndroidFileContent = m_pAndroidFileContent.replace("@classContext", text + "@classContext");
		}
		else
		{
			m_pAndroidFileContent = m_pAndroidFileContent.replace("@mainContext", text + "@mainContext");
		}
	}
	
	public String getSwiftFileContent()
	{
		return m_pSwiftFileContent;
	}
	
	public String getAndroidFileContent() {
		return m_pAndroidFileContent;
	}
	
	private Boolean nextDeclarationIsEventHandler = false;
	private Boolean nextDeclarationIsMainClass = false;
	
	/**
	 * Stack, in welchem geschweifte Klammern gespeichert werden
	 */
	//Stack<String> bracketStack = new Stack<String>();
	Stack<State> stateStack;
	enum State {FILE, CLASS, ENUM, FUNCTION, IF, SWITCH, CASE};
	
	// Speichert, wo die erkannten Konstrukte eingefuegt werden.
	enum TemplateContext {CLASS, MAIN};
	TemplateContext templateContext = TemplateContext.CLASS;
	
	// Speichert den Namen des Switches, damit die Optionen mit <Switch name>.Option addressiert werden koennen.
	String switchName;
	
	String regexAnySpace = "\\s*";
	String regexIdentifier = "([\\w]+)"; // variable / class identifier
	String regexDefinition = "(\\s*=\\s*([^;]*))"; // = <some value>
	String regexAccessModifier = "(public|private)\\s+"; // matches <space(s)> public | private <space(s)>
	String regexOtherModifier = "\\s*(static|override)\\s+"; // matches <space(s)> static | override <space(s)>
	String regexfinalModifier = "\\s*(final)\\s+";

	String regexMemberDeclaration = String.format("^(%s)?(%s)?(%s)?(%s)(\\[\\])?\\s*(%s)(%s)?;",
		//     public                static               final              int              MAX_COUNT       =   5
			regexAccessModifier, regexOtherModifier, regexfinalModifier, "[\\w]*?", regexIdentifier, regexDefinition);
	Pattern regexMemberDeclarationPattern = Pattern.compile(regexMemberDeclaration);

	String regexMemberFunctionDeclaration = String.format("^(%s)?(%s)?(%s)?(%s)\\s+%s\\(%s?\\)",
		//		     public                static               final        int              getValue       (int i)
			regexAccessModifier, regexOtherModifier, regexfinalModifier, regexIdentifier, regexIdentifier, "([\\s\\w,]*)");
	Pattern regexMemberFunctionDeclarationPattern = Pattern.compile(regexMemberFunctionDeclaration);
	
	String regexArrayIndexAssignment = "^([\\w_]*\\[\\d+\\])\\s*=\\s*(.*?);";
	Pattern regexArrayIndexAssignmentPattern = Pattern.compile(regexArrayIndexAssignment);
	
	String regexIfStatement = "^if\\s*\\([^\\)]*\\)";
	Pattern ifStatementPattern = Pattern.compile(regexIfStatement);
	
	String regexSwitchStatement = "^switch\\s*\\(([^\\)]*)\\)";
	Pattern switchStatementPattern = Pattern.compile(regexSwitchStatement);
	
	String regexCaseStatement = "^case\\s+([\\w_]+):";
	Pattern caseStatementPattern = Pattern.compile(regexCaseStatement);
	
	String regexBreakStatement = "^break\\s*;";
	Pattern breakStatementPattern = Pattern.compile(regexBreakStatement);
	
	String regexReturnStatement = "^return\\s+([^;]+);";
	Pattern returnStatementPattern = Pattern.compile(regexReturnStatement);
	
	String regexEnumStatement = "^enum\\s+(\\w+)";
	Pattern enumStatementPattern = Pattern.compile(regexEnumStatement);
	
	String regexEnumCaseStatement = "^(\\w+),?";
	Pattern enumCaseStatementPattern = Pattern.compile(regexEnumCaseStatement);
	
	String regexNonPrintables = "^\\s+";
	Pattern nonPrintablesPattern = Pattern.compile(regexNonPrintables);
	
	String regexEventHandlerPrefix = "^@EventHandler";
	Pattern regexEventHandlerPattern = Pattern.compile(regexEventHandlerPrefix);
	
	String regexMainFunctionPrefix = "^@Main";
	Pattern regexMainFunctionPattern = Pattern.compile(regexMainFunctionPrefix);
	
	String regexMainClassPrefix = "^@MainClass";
	Pattern regexMainClassPattern = Pattern.compile(regexMainClassPrefix);
	
	String regexConstructorCallDefinition = "^new\\s+([^;\\(\\)]+)\\s*\\(([^;]*)\\)";
	Pattern regexConstructorCallPattern = Pattern.compile(regexConstructorCallDefinition);
	
	//String regexVariableDeclaration = String.format(format, args)
	
	
	String regexClass = "\\s*class\\s+"; // matches <space(s)s>class<space(s)>
	String regexClassExtends = "(\\s+extends\\s+(\\w+))"; // matches "extends ClassName"
	String regexConsoleOutput = "^System\\.out\\.print(f|ln)?\\((.*)\\);"; // matches System.out.println("Any string");
	Pattern regexConsoleOutputPattern = Pattern.compile(regexConsoleOutput);
	String regexImport = "^import\\s+[\\w\\.\\*]+;";
	Pattern regexImportPattern = Pattern.compile(regexImport);
	
	String regexLineComment = "^\\/\\/(.*)"; // matches a comment like this one
	String regexBlockComment = "^\\/\\*(.*?)\\*\\/"; // /* matches a block comment like this */
	String regexBlockCommentStart = "^\\/\\*";
	String regexBlockCommentEnd = "^\\*\\/";
	Pattern lineCommentRegex = Pattern.compile(regexLineComment);
	Pattern blockCommentRegex = Pattern.compile(regexBlockComment, Pattern.DOTALL | Pattern.MULTILINE);
	Pattern blockCommentRegexStart = Pattern.compile(regexBlockCommentStart);
	Pattern blockCommentRegexEnd = Pattern.compile(regexBlockCommentEnd);

	// matches a variable definition, for example: int i = 0;
	String variableDeclaration = String.format("^%s?%s%s?[,;]", regexIdentifier /* (optional) */, regexIdentifier, regexDefinition);
	Pattern variableDeclarationRegex = Pattern.compile(variableDeclaration);
	
	// Function call in the form of myFunction(myParam);
	// matches (myObject.)myFunction(param1, param2);
	String functionCallDeclaration = String.format("^%s\\s*\\(%s\\);", "(new\\s+)?([\\w\\.]+)", "([^;]*)");
	Pattern functionCallDeclarationRegex = Pattern.compile(functionCallDeclaration);
	
	String regexCharacter = "'\\w'";
	
	// matches "public class Help (extends BaseClass)
	String classDeclaration = String.format("^(%s)?%s%s%s?", regexAccessModifier, regexClass, regexIdentifier, regexClassExtends);
	Pattern classDeclarationRegex = Pattern.compile(classDeclaration);

	public void parse(String fileInput) {
		if(stateStack == null)
		{
			stateStack = new Stack<State>();
		}
		else 
		{
			stateStack.removeAllElements();
		}
		m_pOptionalVars.clear();
		stateStack.push(State.FILE);
		m_pSwiftFileContent   = getSwiftTemplateContent();
		m_pAndroidFileContent = getAndroidTemplateContent();
		stateParserStart(fileInput);
	}
	
	private String getSwiftTemplateContent() {
		
		File sourceFile = new File("target_templates/mainClassIOS.txt");

		// Source http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html
		FileReader fileReader;
		try {
			fileReader = new FileReader(sourceFile);
		} catch (FileNotFoundException e) {
			System.out.printf("ERROR: Could not find template file %s.", sourceFile.getAbsolutePath());
			return "";
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
			e.printStackTrace();
		}
		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringBuffer.toString();
	}
	
	private String getAndroidTemplateContent() {
		File sourceFile = new File("target_templates/mainClassAndroid.txt");

		// Source http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html
		FileReader fileReader;
		try {
			fileReader = new FileReader(sourceFile);
		} catch (FileNotFoundException e) {
			System.out.printf("ERROR: Could not find template file %s.", sourceFile.getAbsolutePath());
			return "";
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
			e.printStackTrace();
		}
		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringBuffer.toString();
	}

	public void stateParserStart(String fileInput)
	{
		int lastLength = 0;
		while(fileInput.length() > 0)
		{
			if(lastLength == fileInput.length())
				break;
			lastLength = fileInput.length();
			
			fileInput = stateParserNonPrintables(fileInput);
			fileInput = stateParserLineComment(fileInput);
			fileInput = stateParserBlockComment(fileInput);
			
			switch(stateStack.peek()) // Check what the current state is
			{
			case FILE:
				fileInput = stateParserImport(fileInput);
				fileInput = stateParserMainClassTag(fileInput);
				fileInput = stateParserClassDeclaration(fileInput);
				break;

			case CLASS:
				// We are in a class. Handle member and function declaration:
				fileInput = stateParserClassDeclaration(fileInput);
				fileInput = stateParserEnumDeclaration(fileInput);
				fileInput = stateParserMemberDeclaration(fileInput);
				fileInput = stateParserMainFunctionTag(fileInput);
				fileInput = stateParserEventHandlerTag(fileInput);
				fileInput = stateParserFunctionDeclaration(fileInput);
				break;
				
			case ENUM:
				fileInput = stateParserEnumCaseDeclaration(fileInput);
				break;
				
			case SWITCH:
				fileInput = stateParserCaseDefinition(fileInput);
				break;
			
			case CASE:
				fileInput = stateParserConsoleOutput(fileInput);
				fileInput = stateParserIfStatement(fileInput);
				fileInput = stateParserBreakStatement(fileInput);
				fileInput = stateParserAssignment(fileInput);
				fileInput = stateParserReturnStatement(fileInput);
				fileInput = stateParserFunctionCall(fileInput);
				fileInput = stateParserMemberDeclaration(fileInput); // Gleiches RegEx fuer lokale Variablen nehmen!!!
				break;

			case IF:
			case FUNCTION:
				fileInput = stateParserConsoleOutput(fileInput);
				fileInput = stateParserIfStatement(fileInput);
				fileInput = stateParserSwitchStatement(fileInput);
				fileInput = stateParserAssignment(fileInput);
				fileInput = stateParserReturnStatement(fileInput);
				fileInput = stateParserMemberDeclaration(fileInput); // Gleiches RegEx fuer lokale Variablen nehmen!!!
				fileInput = stateParserFunctionCall(fileInput);
				break;

			default:
				break;
			}

			// bei einer schliessenden geschweiften Klammer, State vom Stack nehmen:
			fileInput = stateParserBraces(fileInput);
		}
		
		if(fileInput.length() > 0) {
			System.out.println("Unhandled java source, starting with this:\r\n" + fileInput);
			return;
		}

		m_pSwiftFileContent = m_pSwiftFileContent.replace("@classContext", "");
		m_pSwiftFileContent = m_pSwiftFileContent.replace("@mainContext", "");
		m_pAndroidFileContent = m_pAndroidFileContent.replace("@classContext", "");
		m_pAndroidFileContent = m_pAndroidFileContent.replace("@mainContext", "");
		
		System.out.println("------------------ Swift file -------------------------");
		System.out.println(m_pSwiftFileContent);
		System.out.println("-------------------------------------------------------");
		
		System.out.println("----------------- Android file ------------------------");
		System.out.println(m_pAndroidFileContent);
		System.out.println("-------------------------------------------------------");
	}
	
	private String stateParserEnumCaseDeclaration(String fileInput) {
		Matcher enumCaseMatcher	 = enumCaseStatementPattern.matcher(fileInput);
		if(enumCaseMatcher.find())
		{
			String caseName = "";
			int i = 0;
			while(i <= enumCaseMatcher.groupCount())
			{
				String currentGroupMatch = enumCaseMatcher.group(i);
				switch(i)
				{
				case 0: // Whole group match. Ignore!
					break;
					
				case 1:
					caseName = currentGroupMatch.trim();
					break;
				}
				i++;
			}
			fileInput = fileInput.replaceFirst(regexEnumCaseStatement, "");
			addToSwiftFile(String.format("case %s;", caseName));
			addToAndroidFile(enumCaseMatcher.group(0));
		}
		return fileInput;
	}

	private String stateParserEventHandlerTag(String fileInput) {
		Matcher eventHandlerTagMatcher = regexEventHandlerPattern.matcher(fileInput);
		if(eventHandlerTagMatcher.find())
		{
			fileInput = fileInput.replaceFirst(regexEventHandlerPrefix, "");
			nextDeclarationIsEventHandler = true;
		}
		return fileInput;
	}
	
	private String stateParserMainFunctionTag(String fileInput) {
		Matcher mainFunctionTagMatcher = regexMainFunctionPattern.matcher(fileInput);
		if(mainFunctionTagMatcher.find())
		{
			fileInput = fileInput.replaceFirst(regexMainFunctionPrefix, "");
			templateContext = TemplateContext.MAIN;
		}
		return fileInput;
	}
	
	private String stateParserMainClassTag(String fileInput) {
		Matcher mainClassTagMatcher = regexMainClassPattern.matcher(fileInput);
		if(mainClassTagMatcher.find())
		{
			fileInput = fileInput.replaceFirst(regexMainClassPrefix, "");
			nextDeclarationIsMainClass = true;
		}
		return fileInput;
	}

	private String stateParserReturnStatement(String fileInput) {
		Matcher returnStatementMatcher = returnStatementPattern.matcher(fileInput);
		if(returnStatementMatcher.find())
		{
			int i = 0;
			String returnStatement = "";
			while(i <= returnStatementMatcher.groupCount())
			{
				String currentGroupMatch = returnStatementMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					break;
				case 1: // case name
					returnStatement = currentGroupMatch;
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			addToSwiftFile(String.format("return %s;", returnStatement));
			addToAndroidFile(returnStatementMatcher.group(0));
			fileInput = fileInput.replaceFirst(regexReturnStatement, "");
		}
		return fileInput;
	}

	private String stateParserAssignment(String fileInput) {
		Matcher arrayAssignmentMatcher = regexArrayIndexAssignmentPattern.matcher(fileInput);
		if(arrayAssignmentMatcher.find())
		{
			fileInput = fileInput.replaceFirst(regexArrayIndexAssignment, "");
		}
		return fileInput;
	}

	private String stateParserBreakStatement(String fileInput) {
		Matcher breakStatementMatcher = breakStatementPattern.matcher(fileInput);
		if(breakStatementMatcher.find())
		{
			if(stateStack.peek() != State.CASE)
			{
				System.out.println("Not a case statement");
			}
			else
			{
				while(stateStack.peek() == State.CASE)
				{
					stateStack.pop();
				}
				addToSwiftFile("break;");
			}
			fileInput = fileInput.replaceFirst(regexBreakStatement, "");
		}
		return fileInput;
	}

	private String stateParserCaseDefinition(String fileInput) {
		
		Matcher caseStatementMatcher = caseStatementPattern.matcher(fileInput);
		if(caseStatementMatcher.find())
		{
			int i = 0;
			String caseName = "";
			while(i <= caseStatementMatcher.groupCount())
			{
				String currentGroupMatch = caseStatementMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					break;
				case 1: // case name
					caseName = currentGroupMatch;
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			addToSwiftFile(String.format("case %s:", caseName));
			addToAndroidFile(caseStatementMatcher.group(0));
			fileInput = fileInput.replaceFirst(regexCaseStatement, "");
			stateStack.push(State.CASE);
		}
		return fileInput;
	}

	private String stateParserIfStatement(String fileInput)
	{	
		Matcher ifStatementMatcher = ifStatementPattern.matcher(fileInput);
		if(ifStatementMatcher.find())
		{
			int i = 0;
			while(i <= ifStatementMatcher.groupCount())
			{
				String currentGroupMatch = ifStatementMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					break;
				case 1:
					System.out.println("Group 1: " + currentGroupMatch);
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			fileInput = fileInput.replaceFirst(regexIfStatement, "");
			addToSwiftFile(ifStatementMatcher.group(0));
			addToAndroidFile(ifStatementMatcher.group(0));
			
			// Nicht druckbare Zeichen und Kommentare entfernen:
			fileInput = stateParserNonPrintables(fileInput);
			fileInput = stateParserBlockComment(fileInput);
			fileInput = stateParserLineComment(fileInput);
			
			if(!fileInput.substring(0, 1).equals("{"))
			{
				System.out.println("Expected {, but found " + fileInput.substring(0, 1));
			}
			else
			{
				addToAndroidFile("{");
				addToSwiftFile("{");
				fileInput = fileInput.replaceFirst("\\{", "");
				stateStack.push(State.IF);
			}
		}
		return fileInput;
	}
	
	private String stateParserSwitchStatement(String fileInput)
	{
		Matcher switchStatementMatcher = switchStatementPattern.matcher(fileInput);
		if(switchStatementMatcher.find())
		{
			int i = 0;
			while(i <= switchStatementMatcher.groupCount())
			{
				String currentGroupMatch = switchStatementMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					System.out.println("Switch statement: " + currentGroupMatch);
					break;
				case 1:
					System.out.println("Group 1: " + currentGroupMatch);
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			fileInput = fileInput.replaceFirst(regexSwitchStatement, "");
			addToSwiftFile(switchStatementMatcher.group(0));
			addToAndroidFile(switchStatementMatcher.group(0));
			
			// Nicht druckbare Zeichen und Kommentare entfernen:
			fileInput = stateParserNonPrintables(fileInput);
			fileInput = stateParserBlockComment(fileInput);
			fileInput = stateParserLineComment(fileInput);
			
			if(!fileInput.substring(0, 1).equals("{"))
			{
				System.out.println("Expected {, but found " + fileInput.substring(0, 1));
			}
			else
			{
				addToSwiftFile("{");
				fileInput = fileInput.replaceFirst("\\{", "");
				stateStack.push(State.SWITCH);
			}
		}
		return fileInput;
	}

	private String stateParserNonPrintables(String fileInput)
	{
		// Nicht-druckbare Zeichen
		Matcher nonPrintablesMatcher = nonPrintablesPattern.matcher(fileInput);
		if(nonPrintablesMatcher.find())
		{
			addToSwiftFile(nonPrintablesMatcher.group(0));
			addToAndroidFile(nonPrintablesMatcher.group(0));
			fileInput = fileInput.replaceFirst(regexNonPrintables, "");
		}
		return fileInput;
	}
	
	private String stateParserBraces(String fileInput)
	{
		if(fileInput.length() == 0)
			return "";

		if(fileInput.substring(0, 1).matches("}"))
		{
			fileInput = fileInput.replaceFirst("}", "");

			// if previous function was main function, reset template context to MAIN.
			// Since this function was never added to the file in the first place, 
			// we don't add a closing brace to the file.
			if(stateStack.peek() == State.FUNCTION && templateContext == TemplateContext.MAIN)
			{
				templateContext = TemplateContext.CLASS;
			}
			else if(stateStack.peek() == State.CLASS && nextDeclarationIsMainClass)
			{
				// Do nothing, main class was not added in the first place,
				// thus does not need to be closed!
				nextDeclarationIsMainClass = false;
			}
			else
			{
				addToSwiftFile("}");
				addToAndroidFile("}");
			}
			
			stateStack.pop();
		}
		return fileInput;
	}
	
	private String stateParserBlockComment(String fileInput) {
		Matcher blockCommentMatcherStart = blockCommentRegexStart.matcher(fileInput);
		if(blockCommentMatcherStart.find())
		{
			String blockComment = "";
			Matcher blockCommentMatcherEnd = blockCommentRegexEnd.matcher(fileInput);
			while(!blockCommentMatcherEnd.find())
			{
				blockComment += fileInput.substring(0, 1);
				fileInput = fileInput.substring(1, fileInput.length() - 1);
				blockCommentMatcherEnd = blockCommentRegexEnd.matcher(fileInput);
			}
			blockComment += blockCommentMatcherEnd.group(0); // Add <end of block comment marker> to file contents;
			addToSwiftFile(blockComment);
			addToAndroidFile(blockComment);
			fileInput = fileInput.replaceFirst(regexBlockComment, "");
			fileInput = fileInput.replaceFirst(regexBlockCommentEnd, "");
			//JOptionPane.showMessageDialog(null, blockComment);
		}
		return fileInput;
	}
	
	private String stateParserLineComment(String fileInput) {
		Matcher lineCommentMatcher = lineCommentRegex.matcher(fileInput);
		if(lineCommentMatcher.find())
		{
			int i = 0;
			while(i <= lineCommentMatcher.groupCount())
			{
				String currentGroupMatch = lineCommentMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					addToSwiftFile(currentGroupMatch);
					addToAndroidFile(currentGroupMatch);
					break;
				case 1:
					//System.out.println("Line comment is: " + currentGroupMatch);
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			fileInput = fileInput.replaceFirst(regexLineComment, "");
		}
		return fileInput;
	}

	private String stateParserImport(String fileInput)
	{
		Matcher regexImportMatcher = regexImportPattern.matcher(fileInput);
		if(regexImportMatcher.find())
		{
			int i = 0;
			while(i <= regexImportMatcher.groupCount())
			{
				String currentGroupMatch = regexImportMatcher.group(i);
				switch(i)
				{
				case 0:
					// Imports should work in android files
					addToAndroidFile(currentGroupMatch);
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			fileInput = fileInput.replaceFirst(regexImport, "");
		}
		return fileInput;
	}
	
	private String stateParserClassDeclaration(String fileInput)
	{
		Matcher classDeclarationMatcher = classDeclarationRegex.matcher(fileInput);
		if(classDeclarationMatcher.find())
		{
			int i = 0;
			String accessModifiers = "";
			String className = "";
			String superClassName = "";
			while(i <= classDeclarationMatcher.groupCount())
			{
				String currentGroupMatch = classDeclarationMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					break;
					
				case 1: // Outer access modifiers group
					break;
					 
				case 2: // Access modifiers
					if(currentGroupMatch != null)
					{
						accessModifiers = currentGroupMatch.trim();
					}
					break;
					
				case 3: // Class name
					if(currentGroupMatch != null)
					{
						className = currentGroupMatch.trim();
					}
					break;
					
				case 4: // If this group exists, the class extends from a superclass.
					break;
				case 5: // Super class name
					if(currentGroupMatch != null) {
						superClassName = currentGroupMatch.trim();
					}
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			fileInput = fileInput.replaceFirst(classDeclaration, "");

			if(nextDeclarationIsMainClass)
			{
				// main class is ignored because it's already in the template
			}
			else if(superClassName != "")
			{
				addToSwiftFile(String.format("%s class %s: %s", accessModifiers, className, superClassName));
			}
			else
			{
				addToSwiftFile(String.format("%s class %s", accessModifiers, className));
			}
			
			if(!nextDeclarationIsMainClass)
				addToAndroidFile(classDeclarationMatcher.group(0));
			
			// Nicht druckbare Zeichen entfernen:
			fileInput = stateParserNonPrintables(fileInput);
			fileInput = stateParserBlockComment(fileInput);
			fileInput = stateParserLineComment(fileInput);

			if(!fileInput.substring(0, 1).equals("{"))
			{
				System.out.println("Expected {, but found " + fileInput.substring(0, 1));
			}
			else
			{
				if(!nextDeclarationIsMainClass)
				{
					addToSwiftFile("{");
				}
				fileInput = fileInput.substring(1, fileInput.length() - 1);
				stateStack.push(State.CLASS);
			}
		}
		return fileInput;
	}
	
	private String stateParserMemberDeclaration(String fileInput)
	{
		Matcher variableDeclarationMatcher = regexMemberDeclarationPattern.matcher(fileInput);
		if(variableDeclarationMatcher.find())
		{
			int i = 0;
			String accessModifiers = "";
			String variableName = "";
			String dataType = "";
			Boolean isArray = false;
			String definition = "";
			while(i <= variableDeclarationMatcher.groupCount())
			{
				String currentGroupMatch = variableDeclarationMatcher.group(i);
				if(currentGroupMatch != null)
					currentGroupMatch = currentGroupMatch.trim();
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					break;
				case 1: // public / private outer group
					break;
				case 2: // public / private inner group
					if(currentGroupMatch != null)
					{
						//System.out.println("Variable has access modifier: " + currentGroupMatch);
						accessModifiers += String.format("%s ", currentGroupMatch);
					}
					break;
				case 3: // static / override outer group
					break;
				case 4: // static / override inner group
					if(currentGroupMatch != null)
					{
						//System.out.println("Variable has access modifier #2: " + currentGroupMatch);
						accessModifiers += String.format("%s ", currentGroupMatch);
					}
					break;
				case 5: // final outer group
					break;
				case 6: // final inner group
					if(currentGroupMatch != null)
					{   // Final variables can only be defined once. We "assume" that this is const in Swift, even though it isn't
						// "Final" in Swift means: no subclassing
						//System.out.println("Variable is final!");
						accessModifiers += String.format("%s ", "let");
					}
					else
					{
						// Variable:
						accessModifiers += String.format("%s ", "var");
					}
					break;
				case 7: // data type
					//System.out.println("Variable has the data type " + currentGroupMatch);
					dataType = toSwiftDataType(currentGroupMatch);
					break;
				case 8: // Array brackets []
					if(currentGroupMatch != null)
					{
						isArray = true;
					}
					break;
				case 9: // Name outer
					break;
				case 10: // Name inner
					variableName = currentGroupMatch;
					break;
				case 11: // Variable definition outer
					break;
				case 12: // Variable definition inner
					break;
				case 13: // Variable definition
					if(currentGroupMatch == null || currentGroupMatch.trim().equals("null"))
					{
						// When data type equals null or is empty, we need to make this an optional value,
						// denoted by a question mark.
						dataType += "?";
						m_pOptionalVars.add(variableName.trim());
					}
					definition = toSwiftDefinition(currentGroupMatch);
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			
			if(!isArray)
			{
				String swiftVarDeclaration = String.format("%s %s%s%s;", 
						dataType.equals("") ? "": accessModifiers, // if there is no data type, we are accessing a declared variable.
						variableName, 
						dataType.equals("") ? "": ": " + dataType, 
						definition.equals("") ? "" : " = " + definition);
				addToSwiftFile(swiftVarDeclaration);

				// If we declared a window, we need to add the element as a private variable:
				if(dataType.equals("Application") && !definition.equals(""))
				{
					addToSwiftFile(String.format("\r\nself.window = %s.getWrappedElement();", variableName));
				}
			}
			else
			{
				String swiftVarDeclaration = String.format("%s %s:[%s]%s;", accessModifiers, variableName, dataType, definition.equals("") ? "" : " = " + definition);
				addToSwiftFile(swiftVarDeclaration);
			}
			addToAndroidFile(variableDeclarationMatcher.group(0));
			fileInput = fileInput.replaceFirst(regexMemberDeclaration, "");
		}
		return fileInput;
	}
	
	private String toSwiftDefinition(String definitionValue) {
		if(definitionValue == null)
			return "";
		
		definitionValue = definitionValue.trim();

		// NULL-Werte korrekt handeln
		if(definitionValue.equals("null"))
		{
			return "nil";
		}
		
		// Check if what we're calling is a constructor:
		Matcher constructorCallMatcher = regexConstructorCallPattern.matcher(definitionValue);
		if(constructorCallMatcher.find())
		{
			int i = 0;
			String constructorName = "";
			String parameters = "";
			while( i <= constructorCallMatcher.groupCount())
			{
				String currentGroupMatch = constructorCallMatcher.group(i);
				switch(i)
				{
				case 0: // Whole group match. Ignore!
					break;
					
				case 1: // constructor name
					constructorName = currentGroupMatch.trim();
					break;
					
				case 2: // parameters
					parameters = unwrapAndLabelParameterList(constructorName, currentGroupMatch, true);
					break;
				}
				i++;
			}
			return String.format("%s(%s)", constructorName, parameters.replace("this", "self"));
		}
		
		// Check if this is a member function:
		Matcher functionCallMatcher = functionCallDeclarationRegex.matcher(definitionValue + ";");
		if(functionCallMatcher.find())
		{
			int i = 0;
			String functionName = "";
			String parameters = "";
			while(i <= functionCallMatcher.groupCount())
			{
				String currentGroupMatch = functionCallMatcher.group(i);
				switch(i)
				{
				case 0: // Whole group match. Ignore!
					break;
				case 1: // new... Ignore!
					break;
				case 2: // Function name!
					functionName = unwrapMemberFunctionSignature(currentGroupMatch.trim());
					break;
				case 3: // Function parameters!
					//System.out.println("Trying to unwrap function parameters: " + currentGroupMatch.trim());
					parameters = unwrapAndLabelParameterList(functionName, currentGroupMatch, false);
					break;
				}
				i++;
			}
			return String.format("%s(%s)", functionName, parameters.replace("this", "self"));
		}
		
		// Check if this is a known variable:
		definitionValue = unwrapVariable(definitionValue);
		
		// this durch self ersetzen:
		definitionValue = definitionValue.replace("this", "self");
		
		return definitionValue;
	}

	private String toSwiftDataType(String dataType) {
		dataType = dataType.trim();
		if(dataType.equals("void")) // void is treated like no return type set
			return "";
		if(dataType.equals("int") || dataType.equals("Integer"))
			return "Int";
		if(dataType.equals("float"))
			return "Float";
		if(dataType.equals("char"))
			return "Character";
		if(dataType.equals("byte"))
			return "UInt8";
		if(dataType.equalsIgnoreCase("boolean"))
			return "Bool";
		if(dataType.equals("double"))
			return "Double";
		if(dataType.equals("String"))
			return "String";
		
		// We assume a user-defined datatype:
		return dataType;
	}

	private String stateParserFunctionDeclaration(String fileInput)
	{
		Matcher functionDeclarationMatcher = regexMemberFunctionDeclarationPattern.matcher(fileInput);
		String accessModifiers = "";
		String returnType = "";
		String functionName = "";
		String parameters = "";
		if(functionDeclarationMatcher.find())
		{
			//System.out.println("Found function: " + functionDeclarationMatcher.group(0));
			int i = 0;
			while(i <= functionDeclarationMatcher.groupCount())
			{
				String currentGroupMatch = functionDeclarationMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					break;
				case 1: // outer private / public group
					break;
				case 2: // private / public
					if(currentGroupMatch != null)
					{
						accessModifiers += String.format("%s ", currentGroupMatch.trim());
					}
					break;
				case 3: // outer static / override group
					break;
				case 4: // static / override
					if(currentGroupMatch != null)
					{
					   if(!currentGroupMatch.equals("static")) // static methods are supposed to be class functions
					   {
					      accessModifiers += String.format("%s ", "func");
					   }
					   else
					   {
						   accessModifiers += String.format("%s ", currentGroupMatch);
					   }
					}
					break;
				case 5: // outer final group
					break;
				case 6: // inner final group
					if(currentGroupMatch != null)
					{
						accessModifiers += String.format("%s ", "final");
					}
					break;
				case 7: // outer return type group
					break;
				case 8: // inner return type group
					if(currentGroupMatch == null)
					{
						System.out.println("Function without return type declared?");
					}
					else
					{
						returnType += toSwiftDataType(currentGroupMatch.trim());
					}
					break;
				case 9: // function name
					if(currentGroupMatch != null)
					{
						functionName += currentGroupMatch.trim();
					}
					break;
					
				case 10: // parameters
					if(currentGroupMatch != null)
					{
						addFunctionToKnownFunctionsList(functionName, currentGroupMatch);
						parameters = toSwiftParameterList(currentGroupMatch);
					}
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}

			if(templateContext == TemplateContext.MAIN)
			{
				// For main functions, we don't add anything to the Swift file
				// (main function is handled externally)
			}
			else if(!returnType.equals(""))
			{
				if(!nextDeclarationIsEventHandler)
					addToSwiftFile(String.format("%s %s %s(%s) -> %s", accessModifiers, "func", functionName, parameters, returnType));
				else
					addToSwiftFile(String.format("%s %s(%s) -> %s", "func", functionName, parameters, returnType));
			}
			else
			{
				if(!nextDeclarationIsEventHandler)
					addToSwiftFile(String.format("%s %s %s(%s)", accessModifiers, "func", functionName, parameters));
				else
					addToSwiftFile(String.format("%s %s(%s)", "func", functionName, parameters));
			}
			
			if(templateContext != TemplateContext.MAIN)
			{
				// Event handlers need to be public functions in order to work properly
				if(nextDeclarationIsEventHandler && !accessModifiers.contains("public"))
				    addToAndroidFile("public " + functionDeclarationMatcher.group(0));
				else
					addToAndroidFile(functionDeclarationMatcher.group(0));
			}
			nextDeclarationIsEventHandler = false;
			
			fileInput = fileInput.replaceFirst(regexMemberFunctionDeclaration, "");

			// Nicht druckbare Zeichen entfernen:
			fileInput = stateParserNonPrintables(fileInput);
			fileInput = stateParserBlockComment(fileInput);
			fileInput = stateParserLineComment(fileInput);

			if(!fileInput.substring(0, 1).equals("{"))
			{
				System.out.println("Expected {, but found " + fileInput.substring(0, 1));
			}
			else
			{
				if(templateContext != TemplateContext.MAIN) {
					// Only add an opening brace when we are not in the main function, 
					// as that function is handled externally.
					addToSwiftFile("{");
					addToAndroidFile("{");
				}
				fileInput = fileInput.replaceFirst("\\{", "");
				stateStack.push(State.FUNCTION);
			}
		}
		return fileInput;
	}
	
	private String stateParserFunctionCall(String fileInput)
	{
		Matcher functionCallMatcher = functionCallDeclarationRegex.matcher(fileInput);
		String functionName = "";
		String parameters = "";
		if(functionCallMatcher.find()) {
			int i = 0;
			while(i <= functionCallMatcher.groupCount())
			{
				String currentGroupMatch = functionCallMatcher.group(i);
				switch(i)
				{
				case 0: // Whole match. Ignore!
					break;
				case 1: // "new" (for constructors; handled elsewhere)
					break;
				case 2: // Function name
					//queryFunctionName(currentGroupMatch);
					if(!currentGroupMatch.isEmpty())
					{
					  functionName = unwrapMemberFunctionSignature(currentGroupMatch.trim());
					}
					break;
				case 3: // Function parameter?
					if(!currentGroupMatch.isEmpty())
					{
						parameters = unwrapAndLabelParameterList(functionName, currentGroupMatch, false);
					}
					break;
				}
				i++;
			}
			fileInput = fileInput.replaceFirst(functionCallDeclaration, "");
			
			if(!functionName.equals(""))
			{
				addToSwiftFile(String.format("%s(%s);", functionName, parameters));
			}
			else
			{
				System.out.println("Function without function name found. ERROR!");
			}
			addToAndroidFile(functionCallMatcher.group(0));
		}
		return fileInput;
	}
	
	private String toSwiftParameterList(String currentGroupMatch) {
		
		// Handle empty parameter lists:
		if(currentGroupMatch.trim().equals(""))
			return "";
		
		String[] parameters = currentGroupMatch.split(",");
		String paramList = "";
		int i = 0;
		for(String parameter: parameters)
		{
			parameter = parameter.trim();
			if(i > 0)
				paramList += ", ";
			String dataType = parameter.split(" ")[0].trim();
			String variableName = parameter.split(" ")[1].trim();
			
			// Work around for a API difference: Swift event handlers
			// generally pass a UIButton! as evt handler parameter.
			// Java uses a variable of type Button
			if(nextDeclarationIsEventHandler && dataType.equals("Button"))
			{
				dataType = "UIButton!";
			}
			
			paramList += String.format("%s: %s", variableName, toSwiftDataType(dataType));
			i++;
		}
		return paramList;
	}

	private String stateParserConsoleOutput(String fileInput)
	{
		Matcher consoleOutputMatcher = regexConsoleOutputPattern.matcher(fileInput);
		if(consoleOutputMatcher.find())
		{
			int i = 0;
			String whichPrintFunc = "";
			String outputString = "";
			while(i <= consoleOutputMatcher.groupCount())
			{
				String currentGroupMatch = consoleOutputMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					break;
				case 1:
					if(currentGroupMatch != null)
					{
						whichPrintFunc = currentGroupMatch.trim();
					}
					break;
				case 2:
					System.out.println("Group 2: " + currentGroupMatch);
					if(currentGroupMatch != null)
					{
						outputString = currentGroupMatch.trim();
					}
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			if(!whichPrintFunc.equals(""))
			{
				if(whichPrintFunc.equals("ln"))
				{
					addToSwiftFile(String.format("println(%s);", outputString));
				}
			}
			else
			{
				addToSwiftFile(String.format("print(%s);", outputString));
			}
			addToAndroidFile(consoleOutputMatcher.group(0));
			fileInput = fileInput.replaceFirst(regexConsoleOutput, "");
		}
		return fileInput;
	}
	
	private String stateParserEnumDeclaration(String fileInput)
	{
		Matcher enumMatcher = enumStatementPattern.matcher(fileInput);
		if(enumMatcher.find())
		{
			int i = 0;
			String enumName = "";
			while(i <= enumMatcher.groupCount())
			{
				String currentGroupMatch = enumMatcher.group(i);
				switch(i)
				{
				case 0: // Whole group match: Ignore!
					break;
					
				case 1: // enum name
					if(currentGroupMatch != null)
					{
						enumName = currentGroupMatch;
					}
					break;
				default:
					System.out.println("Unexpected group #" + i + " in enum declaration");
					break;	
				}
				i++;
			}
			fileInput = fileInput.replaceFirst(regexEnumStatement, "");
			addToSwiftFile(String.format("enum %s", enumName.trim()));
			addToAndroidFile(enumMatcher.group(0));

			// Nicht druckbare Zeichen entfernen:
			fileInput = stateParserNonPrintables(fileInput);
			fileInput = stateParserBlockComment(fileInput);
			fileInput = stateParserLineComment(fileInput);

			if(!fileInput.substring(0, 1).equals("{"))
			{
				System.out.println("Expected {, but found " + fileInput.substring(0, 1));
			}
			else
			{
				addToSwiftFile("{");
				addToAndroidFile("{");
				fileInput = fileInput.substring(1, fileInput.length() - 1);
				stateStack.push(State.ENUM);
			}
		}
		return fileInput;
	}

	/**
	 * Returns the publicly accessible API for this element
	 * @param element The element to generate an API list for
	 * @return An ArrayList of method signatures with parameters for this element
	 */
	public HashMap<String, String[]> getPublicAPI() {
		HashMap<String, String[]> methodToParamNamesMap = new HashMap<String, String[]>();
		
		/* Constructors */
		methodToParamNamesMap.put("Activity",       new String[] {"context"});
		methodToParamNamesMap.put("Application",    new String[] {"context"});
		methodToParamNamesMap.put("Button",         new String[] {"context"});
		methodToParamNamesMap.put("Textfield",      new String[] {"context"});
		
		/* Application public members: */		
		methodToParamNamesMap.put("createActivity", null);
		methodToParamNamesMap.put("getActivity",    null);
		
		/* Activity public members: */
		methodToParamNamesMap.put("addButton",     new String[] {"button"});
		methodToParamNamesMap.put("addTextfield",  new String[] {"textfield"});
		methodToParamNamesMap.put("createButton",  null);
		methodToParamNamesMap.put("addTextfield",  null);
		methodToParamNamesMap.put("getWrappedElement", null);
		
		/* GUI element public members (shared with all GUI elements): */
		methodToParamNamesMap.put("getLabel",      null);
		methodToParamNamesMap.put("setLabel",      new String[] {"label"});
		methodToParamNamesMap.put("setPosition",   new String[] {"x", "y"});
		methodToParamNamesMap.put("setSize",       new String[] {"width", "height"});
		methodToParamNamesMap.put("addToActivity", null);
		
		/* Button public members: */
		methodToParamNamesMap.put("addOnClickListener", 
												  new String[] {"methodName"});
		
		/* Textfield public members: */
		methodToParamNamesMap.put("addText",      new String[] {"text"});
		methodToParamNamesMap.put("setText",      new String[] {"text"});
		methodToParamNamesMap.put("getText",      null);
		methodToParamNamesMap.put("setNumber",    new String[] {"number"});
		methodToParamNamesMap.put("getNumber",    null);
		return methodToParamNamesMap;
	}
	
	public String[] getParameterLabelsForFunction(String methodName)
	{
		// Get last bit of the function name (the part after the class / object name)
		methodName = methodName.split("\\.")[methodName.split("\\.").length - 1];
		return m_pFunctionParamLabels.get(methodName);
	}
	
	/**
	 * Unwraps all optional parameters in the parameters list, and labels all necessary variables
	 * @param functionName The name of the function to label
	 * @param paramList List of parameter values to pass to this function (String, comma-separated)
	 * @param isConstructor true if the instance is a constructor (constructors need all parameter values labeled)
	 * @return
	 */
	private String unwrapAndLabelParameterList(String functionName, String paramList, Boolean isConstructor)
	{
		String parameters = "";
		
		// Check if we need to unwrap the function parameter
		// and add parameter labels to the function
		String[] parametersTemp = paramList.split(",");
		int j = 0;
		for(String parameter: parametersTemp) {
			parameter = parameter.trim();
			for(String optionalVar: m_pOptionalVars)
			{
				// Normal parameter passes!
				if(parameter.equals(optionalVar))
				{
					parametersTemp[j] += "!";
				}
				// Parameter passes that are part of a function
				if(parameter.startsWith(optionalVar + "."))
				{
					parametersTemp[j] = parametersTemp[j].replace(optionalVar, optionalVar + "!");
				}
			}
			// Konstruktoren muessen alle Parameter gelabeled haben,
			// normale Funktionsaufrufe nur ab dem zweiten.
			if(j > 0 || isConstructor)
			{
				// Funktionsaufruf unterteilen:
				String[] paramLabels = getParameterLabelsForFunction(functionName.trim());
				if(paramLabels != null && paramLabels[j] != null) {
					parametersTemp[j] = paramLabels[j] + ": " + parametersTemp[j].trim();
				}
			}
			j++;
		}
		// Parameter array -> String conversion
		j = 0;
		for(String parameter: parametersTemp)
		{
			if(j > 0)
				parameters += ", ";
			parameters += parameter;
			j++;
		}
		
		return parameters;
	}
	
	/**
	 * Unwraps a member function (adds exclamation marks to optional values)
	 * @param functionSignature The complete function signature
	 * @return Unwrapped function name
	 */
	private String unwrapMemberFunctionSignature(String functionSignature)
	{
		for(String optionalVar: m_pOptionalVars)
			if(functionSignature.startsWith(optionalVar + ".") || functionSignature.startsWith(optionalVar + " ") || functionSignature.startsWith(optionalVar + ";"))
				functionSignature = functionSignature.replaceFirst(optionalVar, optionalVar + "!");
		return functionSignature;
	}
	
	/**
	 * Unwraps a variable (adds exclamation mark to optional value)
	 * @param variable The variable to unwrap
	 * @return Unwrapped version of the variable
	 */
	private String unwrapVariable(String variable)
	{
		variable = variable.trim();
		for(String optionalVar: m_pOptionalVars)
			if(variable.equals(optionalVar))
				variable += "!";
		return variable;
	}
	
	/**
	 * Adds a function to the list of known functions (containing function name -> parameter labels mapping)
	 * @param functionName The name of the function
	 * @param parameterList Stringified list of parameter labels
	 */
	private void addFunctionToKnownFunctionsList(String functionName, String parameterList)
	{
		String[] parameterNameList = new String[parameterList.split(",").length];
		int j = 0;
		for(String parameter: parameterList.split(","))
		{
			parameter = parameter.trim();
			if(parameter.equals(""))
				continue;
			String varName = parameter.split(" ")[1];
			parameterNameList[j] = varName;
			j++;
		}
		m_pFunctionParamLabels.put(functionName.trim(), parameterNameList);
	}
	
}
