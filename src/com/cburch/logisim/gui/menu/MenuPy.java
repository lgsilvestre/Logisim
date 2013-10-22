/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Locale;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.python.core.PyException;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

class MenuPy extends JMenu implements ActionListener {
	private LogisimMenuBar menubar;
	private JMenuItem script1 = new JMenuItem();
	private JMenuItem script2 = new JMenuItem();
	PythonInterpreter interp = new PythonInterpreter();
	
	public MenuPy(LogisimMenuBar menubar) {
		this.menubar = menubar;

		script1.addActionListener(this);
		script2.addActionListener(this);
		
		add(script1);
		addSeparator();
		add(script2);
		
	}

	public void localeChanged() {
		this.setText("Scripts");
		
		script1.setText("Script 1");
		script2.setText("Script 2");
		
	}

	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == script1) {
			interp.execfile("script1.py");
		} else if (src == script2) {
			interp.execfile("script2.py");
		}
	}
	
	
}
