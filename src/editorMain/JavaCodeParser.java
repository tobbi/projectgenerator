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
	
	String regexDataType = "\\s*(int|float|String|Boolean)\\s+";
	String regexDeclaration = "(\\w+)"; // variable identifier
	String regexDefinition = "(\\s*=\\s*([^,;]*))?"; // = <some value>
	String variableDeclaration = String.format("%s?%s%s[,;]", regexDataType /* (optional) */, regexDeclaration, regexDefinition);
	Pattern variableDeclarationRegex = Pattern.compile(variableDeclaration);
	
	// Function call in the form of myFunction(myParam);
	String functionCallDeclaration = String.format("%s\\s*\\(%s\\);?", "([\\w\\.]+)", "([^;]*)");
	Pattern functionCallDeclarationRegex = Pattern.compile(functionCallDeclaration);

	ArrayList<Variable> variables = new ArrayList<Variable>();
	
	public String handleVariableDeclaration(String str)
	{
		String localCopyStr = str;
		Matcher variableDeclarationMatcher = variableDeclarationRegex.matcher(localCopyStr);
		while(variableDeclarationMatcher.find())
		{
			int i = 0;
			Variable var = new Variable();
			while(i <= variableDeclarationMatcher.groupCount())
			{
				String currentGroupMatch = variableDeclarationMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					break;
				case 1: // Data type
					System.out.println("Declared new " + currentGroupMatch + " variable.");
					if(currentGroupMatch.equals("int")) {
						var.type = DataType.INTEGER;
					}
					else if(currentGroupMatch.equals("Boolean")) {
						var.type = DataType.BOOL;
					}
					else if(currentGroupMatch.equals("float")) {
						var.type = DataType.FLOAT;
					}
					else if(currentGroupMatch.equals("String")) {
						var.type = DataType.STRING;
					}
					else {
						System.out.printf("Unknown data type %s detected!", var.type);
						continue;
					}

					break;
				case 2: // Variable name
					System.out.println("New variable has the name " + currentGroupMatch);
					break;	
				case 3: // Is a declaration
					if(currentGroupMatch == null) {
						i++;
						continue;
					}
					System.out.println("Variable is being declared!");
					break;
				case 4: // Variable value
					if(currentGroupMatch == null) {
						i++;
						continue;
					}
					
					if(currentGroupMatch.matches(functionCallDeclaration))
					{
						handleFunctionCall(currentGroupMatch);
					}

					switch(var.type)
					{
					case BOOL:
						var.strValue = String.valueOf(Boolean.parseBoolean(currentGroupMatch));
						break;
					case INTEGER:
						var.strValue = String.valueOf(Integer.parseInt(currentGroupMatch));
						break;
					case STRING:
						var.strValue = String.valueOf(currentGroupMatch);
						break;
					case FLOAT:
						var.strValue = String.valueOf(Float.parseFloat(currentGroupMatch));
						break;
					default:
						break;
					}
				}
				i++;
			}
			variables.add(var);
		}
		
		for(Variable var: variables)
		{
			System.out.println("Variable " + var.type + " " + var.strValue);
		}
		return str;
	}
	
	public void handleFunctionCall(String str) {
		Matcher functionCallMatcher = functionCallDeclarationRegex.matcher(str);
		System.out.println(functionCallDeclaration);
		while(functionCallMatcher.find()) {
			int i = 0;
			System.out.println("Function declaration has group count " + functionCallMatcher.groupCount());
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
		handleVariableDeclaration(fileInput);
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
