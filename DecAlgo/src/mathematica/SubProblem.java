package mathematica;

public class SubProblem {
	
	/** Mathematica������ */
	Mathematica mathematica = null;

	/** ���캯������ʼ�� Mathematica������ */
	public SubProblem() {
		super();
		mathematica = new Mathematica();
	}
	
	/** �����ؼ�����Դ */
	public void clear() {
		if(mathematica != null)	mathematica.close();
	}
}
