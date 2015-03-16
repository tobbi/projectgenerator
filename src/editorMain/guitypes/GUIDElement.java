package editorMain.guitypes;

import java.util.Random;

public class GUIDElement extends DOMWriteable {

	public GUIDElement()
	{
		this.setGeneratedID(this.generateID());
	}
	
	/**
	 * Generated IOS ID
	 */
	private String m_pGeneratedID;

	/**
	 * The Generated iOS ID of this UI element
	 * @return generated ID as a String
	 */
	public String getGeneratedID() {
		return m_pGeneratedID;
	}
	
	public void setGeneratedID(String id)
	{
		this.m_pGeneratedID = id;
	}

	/**
	 * Chars that are allowed for an ID
	 */
	private String m_pAllowedIdChars = "abcdefghijklmnopqrstuvwxyzABCDEFGRHIJKLMNOPQRSTUVWXYZ0123456789";
	
	/**
	 * Generate a new User Interface Element ID (iOS)
	 * in the form ###-##-### (numbers and characters)
	 */
	public String generateID() {
		String id = "";

		for(int i = 0; i < 3; i++)
			id += getRandomCharacter();
		id += "-";
		for(int i = 0; i < 2; i++)
			id += getRandomCharacter();
		id += "-";
		for(int i = 0; i < 3; i++)
			id += getRandomCharacter();

		return id;
	}
	
	/**
	 * Returns a random character from the list of
	 * allowed characters
	 * @return random character
	 */
	private Character getRandomCharacter()
	{
		final Random rand = new Random();
		int current_index = rand.nextInt(m_pAllowedIdChars.length());
		Character current = m_pAllowedIdChars.charAt(current_index);
		return current;
	}
}
