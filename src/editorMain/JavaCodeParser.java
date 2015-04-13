package editorMain;

//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.*;

import editorMain.guitypes.BaseGUIType;
//import editorMain.guitypes.ExposedMember;
import editorMain.guitypes.GUIActivity;
import editorMain.guitypes.GUIElement;
import editorMain.dataTypes.Variable;
import editorMain.dataTypes.IGenericVariable.DataType;

public class JavaCodeParser {
	
	
	private GUIActivity m_pParentActivity;

	/**
	 * Test whether the passed character is
	 * a character allowed in designations
	 * @param c
	 * @return
	 */
	public String abfruehstuecken(String str, Character c) {
		/*if(str.length() == 0 || str.length() == 1)
		{
			return "";
		}*/

		if(firstCharMatches(str, c)) {
			return str.substring(1, str.length() - 1);
		}
		return str;
	}
	
	public String abfruehstuecken(String str, String matching)
	{
		if(str.startsWith(matching))
		{
			str = str.substring(matching.length());
		}
		return str;
	}
	
	/**
	 * Returns true when the firstChar matches the given one
	 * @param str String to check
	 * @param c Character to check against
	 * @return true if it matches, false if it doesn't
	 */
	public Boolean firstCharMatches(String str, Character c) {
		if(str.charAt(0) == c)
			return true;

		return false;
	}
	
	String regexAnySpace = "\\s*";
	String regexDataType = "\\s*(int|float|String|boolean|char|double)\\s+"; // matches primitive data type
	String regexAccessModifier = "\\s*(public|private)\\s+"; // matches <space(s) public | private <space(s)>
	String regexOtherModifier = "\\s+(static|override)\\s+"; // matches <space(s)> static | override <space(s)>
	String regexClass = "\\s*class\\s+"; // matches <space(s)s>class<space(s)>
	String regexClassExtends = "(\\s+extends\\s+(\\w+))"; // matches "extends ClassName"
	
	String regexLineComment = "\\s*//\\s*?(.*+)"; // matches a comment like this one
	String regexBlockComment = "\\s*\\/\\*(.*?)\\*\\/"; // /* matches a block comment like this */
	Pattern lineCommentRegex = Pattern.compile(regexLineComment);
	Pattern blockCommentRegex = Pattern.compile(regexBlockComment, Pattern.MULTILINE | Pattern.DOTALL);
	
	String regexIdentifier = "(\\w+)"; // variable / class identifier
	String regexDefinition = "(\\s*=\\s*([^,;]*))?"; // = <some value>

	// matches a variable definition, for example: int i = 0;
	String variableDeclaration = String.format("%s?%s%s[,;]", regexDataType /* (optional) */, regexIdentifier, regexDefinition);
	Pattern variableDeclarationRegex = Pattern.compile(variableDeclaration);
	
	// matches an enum declaration, for example: enum State { SLEEPING, AWAKE }
	String regexEnum = "\\s*enum\\s+";
	String enumDeclaration = "\\s*enum\\s+(\\w+)\\s*\\{\\s*((\\w+\\s*,?\\s*)*\\s*)\\}"; 
	Pattern enumRegex = Pattern.compile(enumDeclaration, Pattern.MULTILINE);
	//String.format("%s%s\\s*\\{(([\\w\\s,]+)?)*\\}", regexEnum, regexIdentifier);
	
	// Function call in the form of myFunction(myParam);
	// matches (myObject.)myFunction(param1, param2);
	String functionCallDeclaration = String.format("%s\\s*\\(%s\\);?", "([\\w\\.]+)", "([^;]*)");
	Pattern functionCallDeclarationRegex = Pattern.compile(functionCallDeclaration);
	
	String regexCharacter = "'\\w'";
	
	// matches "public class Help (extends BaseClass)
	String classDeclaration = String.format("(%s)?%s%s%s?", regexAccessModifier, regexClass, regexIdentifier, regexClassExtends);
	Pattern classDeclarationRegex = Pattern.compile(classDeclaration);
	
	ArrayList<Variable> variables = new ArrayList<Variable>();
	//ArrayList<String> previousVariables = new ArrayList<String>();
	
	public String handleVariableDeclaration(String str)
	{
		String localCopyStr = str;
		Matcher variableDeclarationMatcher = variableDeclarationRegex.matcher(localCopyStr);
		while(variableDeclarationMatcher.find())
		{
			int i = 0;
			Variable var = null;
			while(i <= variableDeclarationMatcher.groupCount())
			{
				String currentGroupMatch = variableDeclarationMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					break;
				case 1: // Data type
					System.out.print("Declared new " + currentGroupMatch + " variable");
					if(currentGroupMatch.equals("int")) {
						var = new Variable<Integer>();
						var.setType(DataType.INTEGER);
					}
					else if(currentGroupMatch.equals("boolean")) {
						var = new Variable<Boolean>();
						var.setType(DataType.BOOL);
					}
					else if(currentGroupMatch.equals("char")) {
						var = new Variable<Character>();
						var.setType(DataType.CHAR);
					}
					else if(currentGroupMatch.equals("double")) {
						var = new Variable<Double>();
						var.setType(DataType.DOUBLE);
					}
					else if(currentGroupMatch.equals("float")) {
						var = new Variable<Float>();
						var.setType(DataType.FLOAT);
					}
					else if(currentGroupMatch.equals("String")) {
						var = new Variable<String>();
						var.setType(DataType.STRING);
					}
					else {
						System.out.printf("Unknown data type %s detected!", currentGroupMatch);
						continue;
					}

					break;
				case 2: // Variable name
					System.out.print(" with the name " + currentGroupMatch);
					break;
				case 3: // Is a declaration
					if(currentGroupMatch == null) {
						i++;
						continue;
					}
					System.out.print(" and a value of ");
					break;
				case 4: // Variable value
					if(currentGroupMatch == null) {
						i++;
						continue;
					}
					
					System.out.print(currentGroupMatch);
					
					if(currentGroupMatch.matches(functionCallDeclaration))
					{
						handleFunctionCall(currentGroupMatch);
					}

					if(var.getType() == DataType.BOOL)
					{
						var.setValue(Boolean.parseBoolean(currentGroupMatch));
					}
					if(var.getType() == DataType.CHAR)
					{
						if(!currentGroupMatch.matches(regexCharacter))
						{
							System.out.println(currentGroupMatch + " is not a character definition!");
							i++;
						}
						var.setValue(currentGroupMatch.charAt(1));
					}
					if(var.getType() == DataType.INTEGER)
					{
						try {
							var.setValue(Integer.parseInt(currentGroupMatch));
						}
						catch(NumberFormatException e)
						{
							var.setValue(0);
							System.out.print(" (Could not parse Java int (typecasts are currently not supported). Setting value to 0). ");
						}
					}
					if(var.getType() == DataType.STRING)
					{
						var.setValue(currentGroupMatch);
					}
					if(var.getType() == DataType.FLOAT)
					{
						try {
							var.setValue(Float.parseFloat(currentGroupMatch));
						}
						catch(NumberFormatException e)
						{
							var.setValue(0.0f);
							System.out.print(" (Could not parse Java float (typecasts are currently not supported). Setting value to 0.0). ");
						}
					}
					if(var.getType() == DataType.DOUBLE)
					{
						try {
							var.setValue(Double.parseDouble(currentGroupMatch));
						}
						catch(NumberFormatException e)
						{
							var.setValue(0.0);
							System.out.print(" (Could not parse Java double (typecasts are currently not supported). Setting value to 0.0). ");
						}
					}
				}
				i++;
			}
			System.out.println();
			variables.add(var);
		}
		
		for(Variable var: variables)
		{
			System.out.println("Variable " + var.getType() + " " + var.toString());
		}
		return str;
	}
	
	public String handleClassDeclaration(String str) {
		String localCopyStr = str;
		Matcher classDeclarationMatcher = classDeclarationRegex.matcher(localCopyStr);
		while(classDeclarationMatcher.find())
		{
			int i = 0;
			while(i <= classDeclarationMatcher.groupCount())
			{
				String currentGroupMatch = classDeclarationMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					break;
				case 1: // access modifiers
					if(currentGroupMatch != null)
					{
						System.out.print("Declared new " + currentGroupMatch.trim() + " class ");
					}
					else
					{
						System.out.print("Declared new class without access modifiers (implied private) ");
					}
					break;
				case 2: // bigger access modifiers group
					break;
				case 3:
					//System.out.println("Class has name: " + currentGroupMatch);
					System.out.print("with name " + currentGroupMatch);
					break;
				case 4: // bigger extends group -> Class extends from superclass
					break;
				case 5: // Superclass name
					if(currentGroupMatch != null) 
					{
						System.out.print(" extending from superclass " + currentGroupMatch);
					}
					break;
				default:
					System.out.println("Out of the line group #" + i + " with value " + currentGroupMatch);
					break;
				}
				i++;
			}
			System.out.println();
		}
		return str;
	}
	
	public void handleFunctionCall(String str) {
		Matcher functionCallMatcher = functionCallDeclarationRegex.matcher(str);
		System.out.println(functionCallDeclaration);
		while(functionCallMatcher.find()) {
			int i = 0;
			while(i <= functionCallMatcher.groupCount())
			{
				String currentGroupMatch = functionCallMatcher.group(i);
				switch(i)
				{
				case 0: // Whole match. Ignore!
					break;
				case 1: // Function name
					queryFunctionName(currentGroupMatch);
					break;
				case 2: // Function parameter?
					if(!currentGroupMatch.isEmpty())
					{
						System.out.print("Function parameters: " + currentGroupMatch);
					}
					else
					{
						System.out.print("No function parameters");
					}
					break;
				}
				i++;
			}
		}
	}
	
	public void handleComments(String str)
	{
		Matcher commentMatcher = lineCommentRegex.matcher(str);
		while(commentMatcher.find())
		{
			int i = 0;
			while(i <= commentMatcher.groupCount())
			{
				String currentGroupMatch = commentMatcher.group(i);
				switch(i)
				{
				case 0: // Whole match. Ignore!
					break;
				case 1:
					System.out.println("Comment: " + currentGroupMatch);
					break;
				}
				i++;
			}
		}
		
		Matcher blockCommentMatcher = blockCommentRegex.matcher(str);
		while(blockCommentMatcher.find())
		{
			int i = 0;
			while (i <= blockCommentMatcher.groupCount())
			{
				String currentGroupMatch = blockCommentMatcher.group(i);
				switch(i)
				{
				case 0: // Whole match Ignore!
					break;
				case 1:
					System.out.println("BlockComment:\r\n" + currentGroupMatch);
				}
				i++;
			}
		}
	}
	
	/**
	 * Queries for a function name
	 * @param currentGroupMatch Function name to query for
	 */
	private void queryFunctionName(String currentGroupMatch) {
		String object = null, functionName = null;
		String[] functionCallParts = currentGroupMatch.split("\\.");
		
		if(functionCallParts.length > 1)
		{
			object = functionCallParts[0].trim();
			System.out.println("Object name " + object);
			
			functionName = functionCallParts[1].trim();
			//TODO: Query whether object API contains function or local function
			System.out.println("Function name: "+ functionName);
			
			// Let's try to find the object
			GUIElement referencedElement = m_pParentActivity.getElementById(object);
			if(referencedElement == null)
			{
				if(variables.contains(object))
				{
					System.out.println("Found object amongst previously declared variables");
				}
				else
				{
					System.out.println("Object or element " + object + " could not be found! Trying variables");
				}
			}
			else
			{
				if(!publicAPIContains(referencedElement, functionName))
				{
					System.out.println("ERROR: Public API for " + referencedElement + " does not contain definition for " + functionName);
				}
			}
		}
		else if(currentGroupMatch.split(".").length == 1) {
			functionName = functionCallParts[0].trim();
			System.out.println("Function name: " + functionName);
		}
	}

	public String getJavaIdentifier(String str) {
		Character firstChar = str.charAt(0);
		String varName = "";
		while(Character.isJavaIdentifierPart(firstChar))
		{
			varName += firstChar;
			abfruehstuecken(str, firstChar.toString());
			firstChar = str.charAt(0);
		}
		
		return varName;
	}
	

	public void parse(GUIActivity activity, String fileInput) {
		m_pParentActivity = activity;
		handleEnumDeclaration(fileInput);
		handleClassDeclaration(fileInput);
		handleVariableDeclaration(fileInput);
		handleComments(fileInput);
		handleFunctionCall(fileInput);
	}
	
	private void handleEnumDeclaration(String fileInput) {
		Matcher enumMatcher = enumRegex.matcher(fileInput);
		while(enumMatcher.find()) {
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
		
		public_members.add("java.lang.String editorMain.guitypes.GUIElement.getLabel()");
		public_members.add("java.lang.String editorMain.guitypes.GUIElement.setLabel(java.lang.String text)");
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
