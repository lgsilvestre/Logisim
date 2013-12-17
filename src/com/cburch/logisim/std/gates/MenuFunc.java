package com.cburch.logisim.std.gates;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class MenuFunc extends JDialog implements ActionListener {

	String code;
	JButton butt;
	JTextField tf;
	public MenuFunc(){
		super(new JFrame(), "HOLA", true);
		resize(200,300);
		JLabel lbl = new JLabel("Inserte Codigo");
		tf = new JTextField("         \n"
				+ "         \n"
				+ "         \n"
				+ "         \n");
		butt = new JButton("Setear codigo");
		butt.addActionListener(this);
		JPanel p = new JPanel();
		p.add(lbl);
		p.add(tf);
		p.add(butt);
		add(p);
		tf.reshape(0, 0, 50, 50);
		setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getSource().equals(butt)){
			code = tf.getText(); 
			System.out.println(code);
		}
	}
	
	
}
