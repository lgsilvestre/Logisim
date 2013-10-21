/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.circuit;

import java.awt.Graphics;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.cburch.logisim.circuit.appear.CircuitAppearance;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentEvent;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentListener;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.std.wiring.Clock;
import com.cburch.logisim.util.CollectionUtil;
import com.cburch.logisim.util.EventSourceWeakSupport;

public abstract class Circuit {
	protected static final PrintStream DEBUG_STREAM = null;
	
	// wires is package-protected for CircuitState and Analyze only.
	CircuitWires wires = new CircuitWires();
	private EventSourceWeakSupport<CircuitListener> listeners = new EventSourceWeakSupport<CircuitListener>();
	protected WeakHashMap<Component, Circuit> circuitsUsingThis;
	
	protected Circuit() {
		circuitsUsingThis = new WeakHashMap<Component, Circuit>();
	}
	
	abstract CircuitLocker getLocker();
	
	public Collection<Circuit> getCircuitsUsingThis() {
		return circuitsUsingThis.values();
	}
	
	public void mutatorClear() {
	}

	public abstract AttributeSet getStaticAttributes();

	//
	// Listener methods
	//
	public void addCircuitListener(CircuitListener what) {
		listeners.add(what);
	}

	public void removeCircuitListener(CircuitListener what) {
		listeners.remove(what);
	}

	void fireEvent(int action, Object data) {
		fireEvent(new CircuitEvent(action, this, data));
	}

	private void fireEvent(CircuitEvent event) {
		for (CircuitListener l : listeners) {
			l.circuitChanged(event);
		}
	}

	//
	// access methods
	//
	public abstract String getName();
	public abstract CircuitAppearance getAppearance();
	public abstract SubcircuitFactory getSubcircuitFactory();
	public abstract Set<WidthIncompatibilityData> getWidthIncompatibilityData();
	public abstract BitWidth getWidth(Location p);
	public abstract Location getWidthDeterminant(Location p);
	public abstract boolean hasConflict(Component comp);
	public abstract Component getExclusive(Location loc);
	
	public abstract boolean contains(Component c);
	public abstract Set<Wire> getWires();
	public abstract Set<Component> getNonWires();
	public abstract Collection<? extends Component> getComponents(Location loc);	
	public abstract Collection<? extends Component> getSplitCauses(Location loc);	
	public abstract Collection<Wire> getWires(Location loc);	
	public abstract Collection<? extends Component> getNonWires(Location loc);	
	public abstract boolean isConnected(Location loc, Component ignore);	
	public abstract Set<Location> getSplitLocations();
	public abstract Collection<Component> getAllContaining(Location pt);
	public abstract Collection<Component> getAllContaining(Location pt, Graphics g);
	public abstract Collection<Component> getAllWithin(Bounds bds);
	public abstract Collection<Component> getAllWithin(Bounds bds, Graphics g);
	
	public abstract WireSet getWireSet(Wire start);
	public abstract Bounds getBounds();
	public abstract Bounds getBounds(Graphics g);
	abstract List<Component> getClocks();

	//
	// action methods
	//
	public abstract void setName(String name);	
	void mutatorAdd(Component c) {
	}

	void mutatorRemove(Component c) {
	}

	//
	// Graphics methods
	//
	public void draw(ComponentDrawContext context, Collection<Component> hidden) {
	}

	//
	// helper methods for other classes in package
	//
	public static boolean isInput(Component comp) {
		return comp.getEnd(0).getType() != EndData.INPUT_ONLY;
	}
}
