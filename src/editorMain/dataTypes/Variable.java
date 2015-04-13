package editorMain.dataTypes;

public class Variable<Type> implements IGenericVariable<Type> {
	private Type value;
	
	private DataType type;
	private String name;
	
	@Override
	public void setValue(Type value)
	{
		this.value = value;
	}

	@Override
	public Type getValue()
	{
		return value;
	}

	@Override
	public DataType getType()
	{
		return type;
	}

	@Override
	public void setType(DataType type)
	{
		this.type = type;
	}
	
	@Override
	public String toString()
	{
		if(value == null)
			return "<not set>";
		return value.toString();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
