package multipleAttributes;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 文件读取类
 * @author Gizing
 *
 */
public class MyFileReader
{
	private String filePath = null;
	private StringBuilder readString = null;

	public MyFileReader()
	{
		
	}
	
	public void read()
	{
		try
		{
			 FileReader fr = new FileReader(filePath);
			 int i=0;
			 readString = new StringBuilder();
			 while((i = fr.read())!= -1)
			 {
				 //s = s +(char)i;
				 readString.append((char)i);
			 }
			 //System.out.println(readString);
			 fr.close();

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	
	public StringBuilder getReadString()
	{
		return readString;
	}
}