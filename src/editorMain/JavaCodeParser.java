package editorMain;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.*;

import editorMain.guitypes.BaseGUIType;
import editorMain.guitypes.ExposedMember;

public class JavaCodeParser {
	
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
	
	String regexDataType = "(int|float|String|Boolean)\\s+";
	String regexDeclaration = "(\\w+)"; // variable identifier
	String regexDefinition = "(\\s?=\\s?([^,;]*))?";
	String variableDeclaration = String.format("%s?%s%s[,;]", regexDataType /* (optional) */, regexDeclaration, regexDefinition);
	Pattern variableDeclarationRegex = Pattern.compile(variableDeclaration);
	
	enum DataType {INTEGER, STRING, FLOAT, BOOL};
	DataType currentDataType;
	
	public String handleVariableDeclaration(String str)
	{
		String localCopyStr = str;
		Matcher variableDeclarationMatcher = variableDeclarationRegex.matcher(localCopyStr);
		while(variableDeclarationMatcher.find())
		{
			int i = 0;
			while(i <= variableDeclarationMatcher.groupCount())
			{
				String currentGroupMatch = variableDeclarationMatcher.group(i);
				switch(i)
				{
				case 0: // Whole pattern match. Ignore!
					break;
				case 1: // Data type
					System.out.println("Declared new " + currentGroupMatch + " variable.");
					if(currentGroupMatch.equals("int"))
						currentDataType = DataType.INTEGER;
					else if(currentGroupMatch.equals("Boolean"))
						currentDataType = DataType.BOOL;
					else if(currentGroupMatch.equals("float"))
						currentDataType = DataType.FLOAT;
					else if(currentGroupMatch.equals("String"))
						currentDataType = DataType.STRING;
					else
						System.out.printf("Unknown data type %s detected!", currentDataType);
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
					switch(currentDataType)
					{
					case BOOL:
						if(!checkBoolValue(currentGroupMatch))
						{
							System.out.println("Detected unknown boolean value " + currentGroupMatch);
						}
						break;
					case INTEGER:
						if(!checkIntegerValue(currentGroupMatch))
						{
							System.out.println("Detected unknown integer value " + currentGroupMatch);
						}
						break;
					case STRING:
						if(!checkStringValue(currentGroupMatch))
						{
							System.out.println("Detected unknown String value " + currentGroupMatch);
						}
						break;
					case FLOAT:
						if(!checkFloatValue(currentGroupMatch))
						{
							System.out.println("Detected unknown float value " + currentGroupMatch);
						}
						break;
					default:
						break;
					}
					System.out.println("Variable value is " + currentGroupMatch);
				}
				i++;
			}
		}
		return str;
	}
	
	/**
	 * Checks if this string parameter can be considered a boolean value
	 * @param str The string to check
	 * @return true if this is a bool value, false if it isn't
	 */
	public Boolean checkBoolValue(String str)
	{
		str = str.trim();
		return(str.equals("true") || str.equals("false"));
	}
	
	/**
	 * Checks if this string parameter can be considered a string value
	 * @param str The string to check
	 * @return true if this is a String value, false if it isn't
	 */
	public Boolean checkStringValue(String str)
	{
		str = str.trim();
		return ((str.startsWith("'") && str.endsWith("'")) || 
				(str.startsWith("\"") && str.endsWith("\"")));
	}

	/**
	 * Checks if this string parameter can be considered an int value
	 * @param str The string to check
	 * @return true if this is an int value, false if it isn't
	 */
	public Boolean checkIntegerValue(String str)
	{
		str = str.trim();
		return str.matches("\\d+");
	}
	
	public Boolean checkFloatValue(String str)
	{
		str = str.trim();
		return str.matches("\\d+(\\.\\d+)?");
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
	

	public void parse(String fileInput) {
		handleVariableDeclaration("String str = 'Hallo!';\r\nint x = 3;\r\nBoolean someBoolValue;");
	}
	
	/**
	 * Returns the publicly accessible API for this element
	 * @param element The element to generate an API list for
	 * @return An ArrayList of method signatures with parameters for this element
	 */
	public ArrayList<String> getPublicAPI(BaseGUIType element) {
		System.out.println("== Getting API for " + element.toString() + " ==");
		ArrayList<String> public_members = new ArrayList<String>();
		Method[] methods = element.getClass().getMethods();
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
		}
		return public_members;
	}
}
