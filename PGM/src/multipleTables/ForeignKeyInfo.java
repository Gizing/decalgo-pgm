package multipleTables;

/**
 * �洢ÿ�������Ӧ�ı������
 * @author Gizing
 *
 */
public class ForeignKeyInfo
{
	private String attribute = null;
	private String referenceTable = null;
	private String referenceAttribute = null;
	
	public ForeignKeyInfo()
	{
		
	}
	
	public String getAttribute()
	{
		return attribute;
	}

	public void setAttribute(String attribute)
	{
		this.attribute = attribute;
	}

	public String getReferenceTable()
	{
		return referenceTable;
	}

	public void setReferenceTable(String referenceTable)
	{
		this.referenceTable = referenceTable;
	}

	public String getReferenceAttribute()
	{
		return referenceAttribute;
	}

	public void setReferenceAttribute(String referenceAttribute)
	{
		this.referenceAttribute = referenceAttribute;
	}
}
