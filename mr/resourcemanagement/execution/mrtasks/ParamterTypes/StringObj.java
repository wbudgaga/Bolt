package mr.resourcemanagement.execution.mrtasks.ParamterTypes;

public abstract class StringObj {
	public abstract boolean map(long id, String in);
	
	public static void compute(Object l){
		Long l1 			= (Long) l;
	}
	
	public static void compute(Long l){
		Long l1 			=  l;
	}

	public static void main(String s[]){
		Long l1 = 20l;
		Object l2 = l1;
		
		//1st exp with casting
		long l = System.currentTimeMillis();
		for (int i=0;i<100000000; ++i)
			compute(l2);
		System.out.println(" with casting:  "+(System.currentTimeMillis()- l));
		
		l = System.currentTimeMillis();
		for (int i=0;i<100000000; ++i)
			compute(l1);
		System.out.println(" without casting:  "+(System.currentTimeMillis()- l));

	}
}
