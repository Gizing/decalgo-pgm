package mathematica;

/**
 * 一切全局变量的设置
 * @author Gizing
 */


public class Macro {
	
	/** 是否启动debug功能 */
	public static boolean debug = false;
	
	/** 子问题迭代逼近中的迭代次数 */
	public static int iter = 30;
	
	/** 子问题求解中对解的精度的要求 */
	public static float precision = 0.001f;
	
	//等号注入定义域中随机index点的最多次数
	public static int randomTimes = 5;
	
	//插入等值段后不等号约束的精度要求
	public static float nonEquPrecision = 0.1f;
	
	//计算精度不满足时的最多次数
	public static int calculateTimes = 5;
	
	//决定按行生成还是按列生成，为true则按行生成
	public static boolean row = true;
	
	//是否开启异常退出，true则一有异常就退出
	public static boolean exceptionExit = true;
	
	//是否使用验证生成方法（用于测试，就是等值段后面的x不是紧接着等值段的）
	public static boolean verifyGeneration = false;
	
	//是否使用多线程生成
	public static boolean multiThreadGeneration = true;
	
	//是否输出等号约束的调整参数
	public static boolean ouputEquaAdjParameter = true;
	
	//计算时间总和
	public static double calculateTime = 0;
}
