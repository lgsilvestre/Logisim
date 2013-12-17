package com.cburch.logisim.std.gates;

import java.awt.Color;

import bsh.EvalError;
import bsh.Interpreter;

import java.awt.Graphics;

import javax.swing.JOptionPane;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.std.gates.MenuFunc;

class NewAndGate extends AbstractGate {
	public static NewAndGate FACTORY = new NewAndGate();
	Interpreter interp;
	boolean firstTime;
	String func_;
	MenuFunc mf;

	private NewAndGate() {	
		super("script Gate", Strings.getter("scriptGateComponent"),false,true);
		interp = new Interpreter();
		firstTime = true;
		setRectangularLabel("&");
		String func_;
		setIconNames("scriptGate.gif", "scriptGate.gif", "scriptGate.gif");
	}

	@Override
	protected void paintIconShaped(InstancePainter painter) {
		Graphics g = painter.getGraphics();
		int[] xp = new int[] { 10, 2, 2, 10 };
		int[] yp = new int[] { 2, 2, 18, 18 };
		g.setColor(Color.YELLOW);
		g.drawPolyline(xp, yp, 4);
		GraphicsUtil.drawCenteredArc(g, 10, 10, 8, -90, 180);
	}

	@Override
	protected void paintShape(InstancePainter painter, int width, int height) {
		if (firstTime){
			mf = new MenuFunc();
			firstTime = false;
		}
		PainterShaped.paintNewAnd(painter, width, height);
	}

	@Override
	protected void paintDinShape(InstancePainter painter, int width, int height, int inputs) {
		PainterDin.paintAnd(painter, width, height, false);
	}

	@Override
	protected Value computeOutput(Value[] inputs, int numInputs,
			InstanceState state) {
		//System.out.println(super.
		String func = ""
				+ "if (numInputs < 4){"
		+ "		for (int i = 1; i < numInputs; i++) {"
		+ "			ret = ret.or(inputs[i]);		} }"
		+ "else {"
		+ "for (int i = 1; i < numInputs; i++) {"
		+ "			ret = ret.and(inputs[i]);		} }";
		
		String func2 = "if (numImputs < 3) {"
		+ "		for (int i = 1; i < numInputs; i++) {"
		+ "			ret = ret.or(inputs[i]);		}"
		+ "}"
		+ "else {"
		+ "for (int i = 1; i < 2; i++)"
		+ "ret = ret.or(inputs[i]);"
		+ "for (int i = 2; i < numInputs; i++)"
		+ "ret = ret.and(inputs[i]);"
		+ "}";

		func_ = mf.code; 
		Value ret = inputs[0];
		try {			
			interp.set("ret", ret);
			interp.set("numInputs", numInputs);
			interp.set("inputs", inputs);
			interp.eval(func_);
			ret = (Value)interp.get("ret");
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//JOptionPane.showMessageDialog(frame, "Error en ejecución del código");
		}
		
		return  ret;
		
		//return GateFunctions.computeNewAnd(inputs, numInputs);
	}

	@Override
	protected Expression computeExpression(Expression[] inputs, int numInputs) {
		Expression ret = inputs[0];
		for (int i = 1; i < numInputs; i++) {	
			ret = Expressions.or(ret, inputs[i]);
		}
		return ret;
	}

	@Override
	protected Value getIdentity() { return Value.TRUE; }
	
	
	/*
	public static void main (String[] args){
		
		String perro = "perro ladra";
		Interpreter interp = new Interpreter();
		NewAndGate NAG = new NewAndGate();
		
		try {
			interp.eval("import com.cburch.logisim.std.gates.NewAndGate");
			interp.set("NAG", NAG);
			
			interp.set("perro", perro);
			interp.set("exp", perro);
			interp.eval("System.out.println(perro)");
			NAG.ladrar();
			interp.eval("System.out.println(NAG)");
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
		
	}*/
	
}


