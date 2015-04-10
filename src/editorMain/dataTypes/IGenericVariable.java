package editorMain.dataTypes;

public interface IGenericVariable<Type> {
	public static enum DataType {INTEGER, STRING, FLOAT, BOOL, CHAR, DOUBLE};
	public void setValue(Type type);
	public Type getValue();
	public void setType(DataType type);
	public DataType getType();
}
