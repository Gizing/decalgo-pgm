package multipleAttributes;

/**
 * ��������Ե����봦����
 * @author Gizing
 *
 */
public class InputProcessor
{
	protected StringBuilder schema = null;
	protected VertexNode[] vertexNode = null;
	protected StringBuilder constraintString = null;
	protected Constraint[] constraints = null;
	protected int tableSize;
	protected String tableName = null;
	
	public InputProcessor()
	{
		
	}
	
	public InputProcessor(String schemaPath,String constraintsPath)
	{
		MyFileReader mfr = new MyFileReader();
		
		//��schema������
		mfr.setFilePath(schemaPath);
		mfr.read();
		schema = mfr.getReadString();
		//System.out.println(schema);
		processSchema();
		
		//��constraint������
		mfr.setFilePath(constraintsPath);
		mfr.read();
		constraintString = mfr.getReadString();
		processConstraints();
	}
	
	/**
	 * �����constraints.txt��ȡ��
	 */
	protected void processConstraints()
	{
		String[] constraintStrArr = constraintString.toString().split(";;");
		constraints = new Constraint[constraintStrArr.length];
		for(int i=0;i<constraints.length;i++)
		{
			constraints[i] = new Constraint(constraintStrArr[i]);
		}
		
	}
	
	/**
	 * �����schema.txt��ȡ��
	 */
	protected void processSchema()
	{
		schema.delete(schema.length()-2, schema.length());
		String[] temp = schema.toString().split(",");
		tableName = temp[0].substring(2);
		tableSize = Integer.parseInt(temp[1]);
		vertexNode = new VertexNode[temp.length-2];
		VertexNode curVertexNode = null;
		for(int i=2;i<temp.length;i++)
		{
			curVertexNode = new VertexNode();
			curVertexNode.setAttributeName(temp[i].split(" ")[0]);
			vertexNode[i-2] = curVertexNode;
		}
	}
	
	public VertexNode[] getVertexNode()
	{
		return vertexNode;
	}
	
	public Constraint[] getConstraints()
	{
		return constraints;
	}
	public int getTableSize()
	{
		return tableSize;
	}
	public String getTableName()
	{
		return tableName;
	}
}
