package mathematica;

/**
 * һ��ȫ�ֱ���������
 * @author Gizing
 */


public class Macro {
	
	/** �Ƿ�����debug���� */
	public static boolean debug = false;
	
	/** ����������ƽ��еĵ������� */
	public static int iter = 30;
	
	/** ����������жԽ�ľ��ȵ�Ҫ�� */
	public static float precision = 0.001f;
	
	//�Ⱥ�ע�붨���������index���������
	public static int randomTimes = 5;
	
	//�����ֵ�κ󲻵Ⱥ�Լ���ľ���Ҫ��
	public static float nonEquPrecision = 0.1f;
	
	//���㾫�Ȳ�����ʱ��������
	public static int calculateTimes = 5;
	
	//�����������ɻ��ǰ������ɣ�Ϊtrue��������
	public static boolean row = true;
	
	//�Ƿ����쳣�˳���true��һ���쳣���˳�
	public static boolean exceptionExit = true;
	
	//�Ƿ�ʹ����֤���ɷ��������ڲ��ԣ����ǵ�ֵ�κ����x���ǽ����ŵ�ֵ�εģ�
	public static boolean verifyGeneration = false;
	
	//�Ƿ�ʹ�ö��߳�����
	public static boolean multiThreadGeneration = true;
	
	//�Ƿ�����Ⱥ�Լ���ĵ�������
	public static boolean ouputEquaAdjParameter = true;
	
	//����ʱ���ܺ�
	public static double calculateTime = 0;
}
