package editorMain;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.*;

import editorMain.guitypes.BaseGUIType;
import editorMain.guitypes.GUIElement;

public class JavaCodeParser {
	
	private String m_pSwiftFileContent = "";
	private ArrayList<String> m_pOptionalVars = new ArrayList<String>();

	private void addToSwiftFile(String text)
	{
		m_pSwiftFileContent += text;
	}
	
	public String getSwiftFileContent()
	{
		return m_pSwiftFileContent;
	}
	
	private Boolean nextDeclarationIsEventHandler = false;
	
	/**
	 * Stack, in welchem geschweifte Klammern gespeichert werden
	 */
	//Stack<String> bracketStack = new Stack<String>();
	Stack<State> stateStack;
	enum State {FILE, CLASS, ENUM, FUNCTION, IF, SWITCH, CASE};
	
	// Speichert den Namen des Switches, damit die Optionen mit <Switch name>.Option addressiert werden koennen.
	String switchName;
	
	String regexAnySpace = "\\s*";
	String regexIdentifier = "([\\w]+)"; // variable / class identifier
	String regexDefinition = "(\\s*=\\s*([^;]*))"; // = <some value>
	String regexDataType; //= "\\s*(int|float|String|boolean|char|double)\\s+"; // matches primitive data type
	String regexAccessModifier = "(public|private)\\s+"; // matches <space(s)> public | private <space(s)>
	String regexOtherModifier = "\\s*(static|override)\\s+"; // matches <space(s)> static | override <space(s)>
	String regexfinalModifier = "\\s*(final)\\s+";

	String regexMemberDeclaration = String.format("^(%s)?(%s)?(%s)?(%s)(\\[\\])?\\s+(%s)(%s)?;",
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
	
	String regexNonPrintables = "^\\s+";
	Pattern nonPrintablesPattern = Pattern.compile(regexNonPrintables);
	
	String regexEventHandlerPrefix = "^@EventHandler";
	Pattern regexEventHandlerPattern = Pattern.compile(regexEventHandlerPrefix);
	
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
	String variableDeclaration = String.format("^%s?%s%s?[,;]", regexDataType /* (optional) */, regexIdentifier, regexDefinition);
	Pattern variableDeclarationRegex = Pattern.compile(variableDeclaration);
	
	String regexHeapDefinition = "(\\s*=\\s*new\\s*\\(([^;]*\\)))";
	String variableHeapDeclaration = String.format("^%s?%s%s?[,;]", regexDataType, regexIdentifier, regexHeapDefinition);
	Pattern variableHeapDeclarationPattern = Pattern.compile(variableHeapDeclaration);
	
	// matches an enum declaration, for example: enum State { SLEEPING, AWAKE }
	String regexEnum = "\\s*enum\\s+";
	String enumDeclaration = "\\s*enum\\s+(\\w+)\\s*\\{\\s*((\\w+\\s*,?\\s*)*\\s*)\\}"; 
	Pattern enumRegex = Pattern.compile(enumDeclaration, Pattern.MULTILINE);
	//String.format("%s%s\\s*\\{(([\\w\\s,]+)?)*\\}", regexEnum, regexIdentifier);
	
	// Function call in the form of myFunction(myParam);
	// matches (myObject.)myFunction(param1, param2);
	String functionCallDeclaration = String.format("^%s\\s*\\(%s\\);", "([\\w\\.]+)", "([^;]*)");
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
			m_pOptionalVars.clear();
		}
		stateStack.push(State.FILE);
		stateParserStart(fileInput);
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
				fileInput = stateParserClassDeclaration(fileInput);
				break;

			case CLASS:
				// We are in a class. Handle member and function declaration:
				fileInput = stateParserClassDeclaration(fileInput);
				fileInput = stateParserMemberDeclaration(fileInput);
				fileInput = stateParserEventHandlerDeclaration(fileInput);
				fileInput = stateParserFunctionDeclaration(fileInput);
				break;
				
			case SWITCH:
				fileInput = stateParserCaseDefinition(fileInput);
				break;
			
			case CASE:
				fileInput = stateParserConsoleOutput(fileInput);
				fileInput = stateParserMemberDeclaration(fileInput); // Gleiches RegEx fuer lokale Variablen nehmen!!!
				fileInput = stateParserIfStatement(fileInput);
				fileInput = stateParserBreakStatement(fileInput);
				fileInput = stateParserAssignment(fileInput);
				fileInput = stateParserFunctionCall(fileInput);
				break;

			case FUNCTION:
				fileInput = stateParserConsoleOutput(fileInput);
				fileInput = stateParserMemberDeclaration(fileInput); // Gleiches RegEx fuer lokale Variablen nehmen!!!
				fileInput = stateParserIfStatement(fileInput);
				fileInput = stateParserSwitchStatement(fileInput);
				fileInput = stateParserAssignment(fileInput);
				fileInput = stateParserReturnStatement(fileInput);
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

		//JOptionPane.showMessageDialog(null, "------- Next input ------\r\n" + fileInput);
		System.out.println("-------------------------------------------------------");
		System.out.println(m_pSwiftFileContent);
		System.out.println("-------------------------------------------------------");
	}
	
	private String stateParserEventHandlerDeclaration(String fileInput) {
		Matcher eventHandlerTagMatcher = regexEventHandlerPattern.matcher(fileInput);
		if(eventHandlerTagMatcher.find())
		{
			fileInput = fileInput.replaceFirst(regexEventHandlerPrefix, "");
			nextDeclarationIsEventHandler = true;
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
					System.out.println("Case statement: " + currentGroupMatch);
					break;
				case 1: // case name
					System.out.println("Group 1: " + currentGroupMatch);
					returnStatement = currentGroupMatch;
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			addToSwiftFile(String.format("return %s;", returnStatement));
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
					System.out.println("Case statement: " + currentGroupMatch);
					break;
				case 1: // case name
					System.out.println("Group 1: " + currentGroupMatch);
					caseName = currentGroupMatch;
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			addToSwiftFile(String.format("case %s:", caseName));
			fileInput = fileInput.replaceFirst(regexCaseStatement, "");
			stateStack.push(State.CASE);
		}
		return fileInput;
	}

	private String stateParserIfStatement(String fileInput)
	{	
		Matcher ifStatementMatcher = ifStatementPattern.matcher(fileInput);
		//System.out.println(regexMemberDeclaration);
		if(ifStatementMatcher.find())
		{
			int i = 0;
			while(i <= ifStatementMatcher.groupCount())
			{
				String currentGroupMatch = ifStatementMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					System.out.println("If statement: " + currentGroupMatch);
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
				fileInput = fileInput.replaceFirst("{", "");
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
			fileInput = fileInput.replaceFirst(regexNonPrintables, "");
		}
		return fileInput;
	}
	
	private String stateParserBraces(String fileInput)
	{
		if(fileInput.substring(0, 1).matches("}"))
		{
			addToSwiftFile("}");
			fileInput = fileInput.replaceFirst("}", "");
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
			blockComment += "*/"; // Add <end of block comment marker> to file contents;
			addToSwiftFile(blockComment);
			fileInput = fileInput.replaceFirst(regexBlockComment, "");
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
					//System.out.println("Line comment: " + currentGroupMatch);
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
				case 0: // Whole pattern match. Ignore!
					System.out.println(currentGroupMatch);
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
			while(i <= classDeclarationMatcher.groupCount())
			{
				String currentGroupMatch = classDeclarationMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					System.out.println(currentGroupMatch);
					break;
					
				case 1: // Outer access modifiers group
					break;
					 
				case 2: // Access modifiers
					if(currentGroupMatch != null)
					{
						addToSwiftFile(String.format("%s class ", currentGroupMatch));
					}
					else
					{
						addToSwiftFile("class ");
					}
					break;
					
				case 3: // Class name
					if(currentGroupMatch != null)
					{
						addToSwiftFile(currentGroupMatch);
					}
					break;
					
				case 4: // If this group exists, the class extends from a superclass.
					break;
				case 5: // Super class name
					if(currentGroupMatch != null) {
						addToSwiftFile(String.format(": %s", currentGroupMatch));
					}
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			fileInput = fileInput.replaceFirst(classDeclaration, "");

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
				fileInput = fileInput.substring(1, fileInput.length() - 1);
				stateStack.push(State.CLASS);
			}
		}
		return fileInput;
	}
	
	private String stateParserMemberDeclaration(String fileInput)
	{
		Matcher variableDeclarationMatcher = regexMemberDeclarationPattern.matcher(fileInput);
		//System.out.println(regexMemberDeclaration);
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
				String swiftVarDeclaration = String.format("%s %s:%s%s;", accessModifiers, variableName, dataType, definition.equals("") ? "" : " = " + definition);
				addToSwiftFile(swiftVarDeclaration);
				System.out.println(swiftVarDeclaration);
			}
			else
			{
				String swiftVarDeclaration = String.format("%s %s:[%s]%s;", accessModifiers, variableName, dataType, definition.equals("") ? "" : " = " + definition);
				addToSwiftFile(swiftVarDeclaration);
				System.out.println(swiftVarDeclaration);
			}
			
			fileInput = fileInput.replace(variableDeclarationMatcher.group(0), ""); //fileInput.substring(variableDeclarationMatcher.group(0).length(), fileInput.length() - 1);
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
		if(definitionValue.startsWith("new "))
		{
			// Swift benutzt kein new zur Instantiierung
			definitionValue = definitionValue.replaceFirst("new ", "");
		}
		// this durch self ersetzen:
		definitionValue = definitionValue.replace("this", "self");
		
		return definitionValue;
	}

	private String toSwiftDataType(String dataType) {
		dataType = dataType.trim();
		System.out.println("Got data type: " + dataType);
		if(dataType.equals("int") || dataType.equals("Integer"))
			return "Int";
		if(dataType.equals("float"))
			return "Float";
		if(dataType.equals("char"))
			return "Character";
		if(dataType.equals("byte"))
			return "UInt8";
		if(dataType.equals("boolean") || dataType.equals("bool"))
			return "Bool";
		if(dataType.equals("double"))
			return "Double";
		if(dataType.equals("String"))
			return "String";
		
		// Wir nehmen einen benutzerdefinierten Datentyp an.
		return dataType;
	}

	private String stateParserFunctionDeclaration(String fileInput)
	{
		Matcher functionDeclarationMatcher = regexMemberFunctionDeclarationPattern.matcher(fileInput);
		System.out.println(regexMemberFunctionDeclaration);
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
					System.out.println("Function declaration " + currentGroupMatch);
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
					      accessModifiers += String.format("%s ", "class func");
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
						// We treat "void" like no return type set.
						if(!currentGroupMatch.trim().equals("void"))
						{
							returnType += toSwiftDataType(currentGroupMatch.trim());
						}
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
						parameters = toSwiftParameterList(currentGroupMatch);
					}
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			if(!returnType.equals(""))
			{
				if(!nextDeclarationIsEventHandler)
					addToSwiftFile(String.format("%s %s %s(%s) -> %s", accessModifiers, "class func", functionName, parameters, returnType));
				else
					addToSwiftFile(String.format("%s %s(%s) -> %s", "func", functionName, parameters, returnType));
			}
			else
			{
				if(!nextDeclarationIsEventHandler)
					addToSwiftFile(String.format("%s %s %s(%s)", accessModifiers, "class func", functionName, parameters));
				else
					addToSwiftFile(String.format("%s %s(%s)", "func", functionName, parameters));
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
				addToSwiftFile("{");
				fileInput = fileInput.replaceFirst("\\{", "");
				stateStack.push(State.FUNCTION);
			}
		}
		return fileInput;
	}
	
	private String stateParserFunctionCall(String fileInput)
	{
		Matcher functionCallMatcher = functionCallDeclarationRegex.matcher(fileInput);
		System.out.println(functionCallDeclaration);
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
				case 1: // Function name
					//queryFunctionName(currentGroupMatch);
					if(!currentGroupMatch.isEmpty())
					{
					  functionName = currentGroupMatch;
					  for(String optionalVar: m_pOptionalVars)
					  {
						  if(functionName.startsWith(optionalVar + "."))
						  {
							  functionName = functionName.replaceFirst(optionalVar, optionalVar + "!");
							  break;
						  }
					  }
					}
					break;
				case 2: // Function parameter?
					if(!currentGroupMatch.isEmpty())
					{
						parameters = currentGroupMatch;
						//System.out.println("Function parameters: " + currentGroupMatch);
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
			while(i <= consoleOutputMatcher.groupCount())
			{
				String currentGroupMatch = consoleOutputMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					System.out.println("Console output " + currentGroupMatch);
					break;
				case 1:
					System.out.println("Group 1: " + currentGroupMatch);
					break;
				case 2:
					System.out.println("Group 2: " + currentGroupMatch);
					break;
				default:
					System.out.println("Unexpected group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			if(consoleOutputMatcher.group(1) != null)
			{
				if(consoleOutputMatcher.group(1).equals("ln"))
				{
					addToSwiftFile(String.format("println(%s);\r\n", 
							(consoleOutputMatcher.group(2) == null) ? "" : consoleOutputMatcher.group(2)));
				}
			}
			else
			{
				addToSwiftFile(String.format("print(%s);\r\n",
						(consoleOutputMatcher.group(2) == null) ? "" : consoleOutputMatcher.group(2)));
			}
			fileInput = fileInput.replaceFirst(regexConsoleOutput, "");
		}
		return fileInput;
	}
	
	private void handleEnumDeclaration(String fileInput) {
		Matcher enumMatcher = enumRegex.matcher(fileInput);
		if(enumMatcher.find()) {
			int i = 0;
			while(i <= enumMatcher.groupCount())
			{
				String currentGroupMatch = enumMatcher.group(i);
				switch(i)
				{
				case 0: // Whole match. Ignore!
					break;
				case 1: // Enum name
					System.out.print("Declared enum with name " + currentGroupMatch);
					break;
				case 2: // Function parameter?
					if(!currentGroupMatch.isEmpty())
					{
						System.out.print(" and states " + currentGroupMatch);
					}
					else
					{
						System.out.print(" and no enumeration states");
					}
					break;
				}
				i++;
			}
		}
	}

	/**
	 * Returns the publicly accessible API for this element
	 * @param element The element to generate an API list for
	 * @return An ArrayList of method signatures with parameters for this element
	 */
	public ArrayList<String> getPublicAPI(BaseGUIType element) {
		ArrayList<String> public_members = new ArrayList<String>();
		/*Method[] methods = element.getClass().getMethods();
		for(Method method: methods)
		{
			Annotation[] annotations = method.getAnnotations();
			for(Annotation annotation: annotations)
			{
				if(annotation instanceof ExposedMember)
				{
					public_members.add(method.toGenericString());
				}
			}
		}*/
		
		public_members.add("String getLabel()");
		public_members.add("String setLabel(String text)");
		return public_members;
	}
	
	public Boolean publicAPIContains(GUIElement element, String functionName) {
		ArrayList<String> publicAPI = getPublicAPI(element);
		for(String apiCall: publicAPI)
		{
			if(apiCall.indexOf(functionName) != -1)
			{
				return true;
			}
		}
		return false;
	}
}
