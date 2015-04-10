package editorMain.dataTypes;

public class Variable<Type> extends GenericVariable {
	public String strValue;
	public Type value;
	
	public void setValue(Type value)
	{
		this.value = value;
		this.strValue = value.toString();
	}
}
