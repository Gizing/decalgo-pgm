package multipleAttributes;

//∂•µ„¿‡
public class VertexNode
{
	private int value = 0;
	private String attributeName = null;
	private String attributeType = null;
	
	public String getAttributeType()
	{
		return attributeType;
	}

	public void setAttributeType(String attributeType)
	{
		this.attributeType = attributeType;
	}

	public String getAttributeName()
	{
		return attributeName;
	}

	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}
	
	public VertexNode()
	{
		
	}
	
	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	

}
