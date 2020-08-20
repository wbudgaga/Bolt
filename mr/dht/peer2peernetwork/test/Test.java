package mr.dht.peer2peernetwork.test;

import java.util.Arrays;

public class Test<T> {
	private T t;

	public T getT() {
		return t;
	}

	public void setT(T t) {
		this.t		 	= t;
	}
	
	public static void main(String s[]){
		String ext 		= "";
		byte[] a 		= new byte[0];
		int idx			= 498;
		idx 			= (idx +1) % 500;
		idx 			= (idx +1) % 500;
		System.out.println(idx);
		int[] lst 		= {2,4,6,8,0};
		int[] l1 		= Arrays.copyOfRange(lst,1,3);
		System.out.println(l1.length);
		System.out.println("StartMqapTask".contains("Map") + "    mm=" + Integer.MAX_VALUE/1000000 + "    " + ("ahbsvext.xt".endsWith(ext)));
		System.out.println("abc. Thm>q \'".replaceAll("[^a-zA-Z]", " "));
		Test<Long> t 		= new Test<>();
		t.setT(50l);
		System.out.println(t.getT());
		
		int l			= -11;
		System.out.println(Integer.MAX_VALUE + "  ###  " + (l&Integer.MAX_VALUE));
	}
}
