package editorMain;

//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.*;

import editorMain.guitypes.BaseGUIType;
//import editorMain.guitypes.ExposedMember;
import editorMain.guitypes.GUIActivity;
import editorMain.guitypes.GUIElement;
import editorMain.dataTypes.GenericVariable;
import editorMain.dataTypes.Variable;
import editorMain.dataTypes.GenericVariable.DataType;

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
	
	String regexDataType = "\\s*(int|float|String|boolean|char|double)\\s+";
	String regexAccessModifier = "\\s*(public|private)\\s+";
	String regexOtherModifier = "\\s+(static|override)\\s+";
	String regexClass = "\\s*class\\s+";
	String regexClassExtends = "(\\s+extends\\s+(\\w+))";
	
	String regexLineComment = "\\s*//\\s*?(.*+)";
	String regexBlockComment = "\\s*\\/\\*(.*?)\\*\\/";
	Pattern lineCommentRegex = Pattern.compile(regexLineComment);
	Pattern blockCommentRegex = Pattern.compile(regexBlockComment, Pattern.MULTILINE | Pattern.DOTALL);
	
	String regexDeclaration = "(\\w+)"; // variable / class identifier
	String regexDefinition = "(\\s*=\\s*([^,;]*))?"; // = <some value>
	String variableDeclaration = String.format("%s?%s%s[,;]", regexDataType /* (optional) */, regexDeclaration, regexDefinition);
	Pattern variableDeclarationRegex = Pattern.compile(variableDeclaration);
	
	// Function call in the form of myFunction(myParam);
	String functionCallDeclaration = String.format("%s\\s*\\(%s\\);?", "([\\w\\.]+)", "([^;]*)");
	Pattern functionCallDeclarationRegex = Pattern.compile(functionCallDeclaration);
	
	String regexCharacter = "'\\w'";
	
	String classDeclaration = String.format("(%s)?%s%s%s?", regexAccessModifier, regexClass, regexDeclaration, regexClassExtends);
	Pattern classDeclarationRegex = Pattern.compile(classDeclaration);
	
	ArrayList<GenericVariable> variables = new ArrayList<GenericVariable>();
	
	public String handleVariableDeclaration(String str)
	{
		String localCopyStr = str;
		Matcher variableDeclarationMatcher = variableDeclarationRegex.matcher(localCopyStr);
		while(variableDeclarationMatcher.find())
		{
			int i = 0;
			GenericVariable var = null;
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
						var.type = DataType.INTEGER;
					}
					else if(currentGroupMatch.equals("boolean")) {
						var = new Variable<Boolean>();
						var.type = DataType.BOOL;
					}
					else if(currentGroupMatch.equals("char")) {
						var = new Variable<Character>();
						var.type = DataType.CHAR;
					}
					else if(currentGroupMatch.equals("double")) {
						var = new Variable<Double>();
						var.type = DataType.DOUBLE;
					}
					else if(currentGroupMatch.equals("float")) {
						var = new Variable<Float>();
						var.type = DataType.FLOAT;
					}
					else if(currentGroupMatch.equals("String")) {
						var = new Variable<String>();
						var.type = DataType.STRING;
					}
					else {
						System.out.printf("Unknown data type %s detected!", var.type);
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

					if(var.value instanceof Boolean)
					{
						var.value = Boolean.parseBoolean(currentGroupMatch);
					}
					if(var.value instanceof Character)
					{
						if(!currentGroupMatch.matches(regexCharacter))
						{
							System.out.println(currentGroupMatch + " is not a character definition!");
							i++;
						}
						var.value = currentGroupMatch.charAt(1);
					}
					if(var.value instanceof Integer)
					{
						try {
							var.value = Integer.parseInt(currentGroupMatch);
						}
						catch(NumberFormatException e)
						{
							System.out.print(" (Could not parse Java int. Typecasts and not local function calls are currently not supported!) ");
							i++;
						}
					}
					if(var.value instanceof String)
					{
						var.value = currentGroupMatch;
					}
					if(var.value instanceof Float)
					{
						try {
							var.value = Float.parseFloat(currentGroupMatch);
						}
						catch(NumberFormatException e)
						{
							System.out.print(" (Could not parse Java float. Typecasts and not local function calls are currently not supported!) ");
							i++;
						}
					}
					if(var.value instanceof Double)
					{
						try {
							var.value = Double.parseDouble(currentGroupMatch);
						}
						catch(NumberFormatException e)
						{
							System.out.print(" (Could not parse Java double. Typecasts and not local function calls are currently not supported!) ");
							i++;
						}
					}
				}
				i++;
			}
			System.out.println();
			variables.add(var);
		}
		
		//for(Variable var: variables)
		//{
		//	System.out.println("Variable " + var.type + " " + var.strValue);
		//}
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
					System.out.println("Function parameters: " + currentGroupMatch);
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
				System.out.println("Object or element " + object + " could not be found!");
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
		handleClassDeclaration(fileInput);
		handleVariableDeclaration(fileInput);
		handleComments(fileInput);
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
