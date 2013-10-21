package logisim.scripts.test;

import logisim.scripts.ScriptHandler;

import org.junit.*;

import static org.junit.Assert.*;



public class ScriptHandlerTest {
	private ScriptHandler script1;
	private ScriptHandler script2;
	@Before
	public void setUp(){
		script1=new ScriptHandler("x=1\ny=4\nz=x+y");
		script2=new ScriptHandler("print z");
	}
	@Test
	public void testContainsError() {
		assertFalse(script1.containsError());
		assertTrue(script2.containsError());
		
	}

}
