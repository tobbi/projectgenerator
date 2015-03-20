package editorMain;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

import editorMain.guitypes.BaseGUIType;
import editorMain.guitypes.ExposedMember;

public class JavaCodeParser {
	
	/**
	 * Test whether the passed character is
	 * a character allowed in designations
	 * @param c
	 * @return
	 */
	public String abfruehstuecken(String str) {
		/*if(str.length() == 0 || str.length() == 1)
		{
			return "";
		}*/
		return str.substring(1, str.length() - 1);
	}
	
	public Boolean isNextChar(String str, Character c) {
		if(str.charAt(0) == c)
			return true;

		return false;
	}
	
	public static void parse(String fileInput) {
	}
	
	/**
	 * Returns the publicly accessible API for this element
	 * @param element The element to generate an API list for
	 * @return An ArrayList of method signatures with parameters for this element
	 */
	public static ArrayList<String> getPublicAPI(BaseGUIType element) {
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
