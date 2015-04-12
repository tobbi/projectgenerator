package editorMain.dataTypes;

public class Variable<Type> implements IGenericVariable<Type> {
	private Type value;
	
	private DataType type;
	
	public void setValue(Type value)
	{
		this.value = value;
	}

	public Type getValue()
	{
		return value;
	}
	
	public DataType getType()
	{
		return type;
	}
	
	public void setType(DataType type)
	{
		this.type = type;
	}
	
	public String toString()
	{
		return value.toString();
	}
}
