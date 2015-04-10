package editorMain.dataTypes;

public class Variable<Type> implements IGenericVariable<Type> {
	public String strValue;
	public Type value;
	
	public DataType type;
	
	public void setValue(Type value)
	{
		this.value = value;
		this.strValue = value.toString();
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
}
