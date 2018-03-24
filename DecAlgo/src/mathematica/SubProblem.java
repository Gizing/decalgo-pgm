package mathematica;

public class SubProblem {
	
	/** Mathematica计算器 */
	Mathematica mathematica = null;

	/** 构造函数，初始化 Mathematica计算器 */
	public SubProblem() {
		super();
		mathematica = new Mathematica();
	}
	
	/** 清除相关计算资源 */
	public void clear() {
		if(mathematica != null)	mathematica.close();
	}
}
