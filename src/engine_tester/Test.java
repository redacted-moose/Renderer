package engine_tester;

class TestClass {
	public TestClass() {
		// TODO Auto-generated constructor stub
	}

	public void test(Object o) {
		System.out.println("TestClass object");
	}
}

class TestClass2 extends TestClass {
	public TestClass2() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void test(Object o) {
		System.out.println("Object method called");
	}
	
	public void test(Float o) {
		System.out.println("Float method called");
	}
	
	public void test(Float o1, Float o2) {
		System.out.println("Two args");
	}
}

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TestClass test = new TestClass2();
		
		test.test(new Float(0.0));
		
	}

}
