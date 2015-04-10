package editorMain.dataTypes;

public abstract class GenericVariable {
	public static enum DataType {INTEGER, STRING, FLOAT, BOOL, CHAR, DOUBLE};
	
	public DataType type;
	
	public Object value;
}
