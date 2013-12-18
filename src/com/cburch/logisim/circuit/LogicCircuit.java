package com.cburch.logisim.circuit;

import java.awt.Graphics;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

public class LogicCircuit extends Circuit {
	private class EndChangedTransaction extends CircuitTransaction {
		private Component comp;
		private Map<Location,EndData> toRemove;
		private Map<Location,EndData> toAdd;
		
		EndChangedTransaction(Component comp, Map<Location,EndData> toRemove,
				Map<Location,EndData> toAdd) {
			this.comp = comp;
			this.toRemove = toRemove;
			this.toAdd = toAdd;
		}
		
		@Override
		protected Map<Circuit,Integer> getAccessedCircuits() {
			return Collections.singletonMap((Circuit)LogicCircuit.this, READ_WRITE);
		}

		@Override
		protected void run(CircuitMutator mutator) {
			for (Location loc : toRemove.keySet()) {
				EndData removed = toRemove.get(loc);
				EndData replaced = toAdd.remove(loc);
				if (replaced == null) {
					wires.remove(comp, removed);
				} else if (!replaced.equals(removed)) {
					wires.replace(comp, removed, replaced);
				}
			}
			for (EndData end : toAdd.values()) {
				wires.add(comp, end);
			}
			((CircuitMutatorImpl) mutator).markModified(LogicCircuit.this);
		}
	}

	private class MyComponentListener implements ComponentListener {
		public void endChanged(ComponentEvent e) {
			locker.checkForWritePermission("ends changed");
			Component comp = e.getSource();
			HashMap<Location,EndData> toRemove = toMap(e.getOldData());
			HashMap<Location,EndData> toAdd = toMap(e.getData());
			EndChangedTransaction xn = new EndChangedTransaction(comp, toRemove, toAdd);
			locker.execute(xn);
			fireEvent(CircuitEvent.ACTION_INVALIDATE, comp);
		}

		private HashMap<Location,EndData> toMap(Object val) {
			HashMap<Location,EndData> map = new HashMap<Location,EndData>();
			if (val instanceof List) {
				@SuppressWarnings("unchecked")
				List<EndData> valList = (List<EndData>) val;
				int i = -1;
				for (EndData end : valList) {
					i++;
					if (end != null) {
						map.put(end.getLocation(), end);
					}
				}
			} else if (val instanceof EndData) {
				EndData end = (EndData) val;
				map.put(end.getLocation(), end);
			}
			return map;
		}
		
		public void componentInvalidated(ComponentEvent e) {
			fireEvent(CircuitEvent.ACTION_INVALIDATE, e.getSource());
		}
	}

	private MyComponentListener myComponentListener = new MyComponentListener();
	private CircuitAppearance appearance;
	private AttributeSet staticAttrs;
	private SubcircuitFactory subcircuitFactory;
	private HashSet<Component> comps = new HashSet<Component>(); // doesn't include wires
	private ArrayList<Component> clocks = new ArrayList<Component>();
	private CircuitLocker locker;
	

	public LogicCircuit(String name) {
		appearance = new CircuitAppearance(this);
		staticAttrs = LogicCircuitAttributes.createBaseAttrs(this, name);
		subcircuitFactory = new SubcircuitFactory(this);
		locker = new CircuitLocker();
	}
	
	@Override
	CircuitLocker getLocker() {
		return locker;
	}
	
	@Override
	public void mutatorClear() {
		locker.checkForWritePermission("clear");

		Set<Component> oldComps = comps;
		comps = new HashSet<Component>();
		wires = new CircuitWires();
		clocks.clear();
		for (Component comp : oldComps) {
			if (comp.getFactory() instanceof SubcircuitFactory) {
				SubcircuitFactory sub = (SubcircuitFactory) comp.getFactory();
				sub.getSubcircuit().circuitsUsingThis.remove(comp);
			}
		}
		fireEvent(CircuitEvent.ACTION_CLEAR, oldComps);
	}
	
	@Override
	public AttributeSet getStaticAttributes() {
		return staticAttrs;
	}
	
	@Override
	public  String getName() {
		return staticAttrs.getValue(LogicCircuitAttributes.NAME_ATTR);
	}

	@Override
	public CircuitAppearance getAppearance() {
		return appearance;
	}
	
	@Override
	public SubcircuitFactory getSubcircuitFactory() {
		return subcircuitFactory;
	}
	
	@Override
	public Set<WidthIncompatibilityData> getWidthIncompatibilityData() {
		return wires.getWidthIncompatibilityData();
	}
	
	@Override
	public BitWidth getWidth(Location p) {
		return wires.getWidth(p);
	}

	@Override
	public Location getWidthDeterminant(Location p) {
		return wires.getWidthDeterminant(p);
	}
	
	@Override
	public boolean hasConflict(Component comp) {
		return wires.points.hasConflict(comp);
	}
	
	@Override
	public Component getExclusive(Location loc) {
		return wires.points.getExclusive(loc);
	}

	private Set<Component> getComponents() {
		return CollectionUtil.createUnmodifiableSetUnion(comps, wires.getWires());
	}
	
	@Override
	public boolean contains(Component c) {
		return comps.contains(c) || wires.getWires().contains(c);
	}

	@Override
	public Set<Wire> getWires() {
		return wires.getWires();
	}

	@Override
	public Set<Component> getNonWires() {
		return comps;
	}

	@Override
	public Collection<? extends Component> getComponents(Location loc) {
		return wires.points.getComponents(loc);
	}
	
	@Override
	public Collection<? extends Component> getSplitCauses(Location loc) {
		return wires.points.getSplitCauses(loc);
	}
	
	@Override
	public Collection<Wire> getWires(Location loc) {
		return wires.points.getWires(loc);
	}
	
	@Override
	public Collection<? extends Component> getNonWires(Location loc) {
		return wires.points.getNonWires(loc);
	}
	
	@Override
	public boolean isConnected(Location loc, Component ignore) {
		for (Component o : wires.points.getComponents(loc)) {
			if (o != ignore) return true;
		}
		return false;
	}
	
	@Override
	public Set<Location> getSplitLocations() {
		return wires.points.getSplitLocations();
	}

	@Override
	public Collection<Component> getAllContaining(Location pt) {
		HashSet<Component> ret = new HashSet<Component>();
		for (Component comp : getComponents()) {
			if (comp.contains(pt)) ret.add(comp);
		}
		return ret;
	}

	@Override
	public Collection<Component> getAllContaining(Location pt, Graphics g) {
		HashSet<Component> ret = new HashSet<Component>();
		for (Component comp : getComponents()) {
			if (comp.contains(pt, g)) ret.add(comp);
		}
		return ret;
	}

	@Override
	public Collection<Component> getAllWithin(Bounds bds) {
		HashSet<Component> ret = new HashSet<Component>();
		for (Component comp : getComponents()) {
			if (bds.contains(comp.getBounds())) ret.add(comp);
		}
		return ret;
	}

	@Override
	public Collection<Component> getAllWithin(Bounds bds, Graphics g) {
		HashSet<Component> ret = new HashSet<Component>();
		for (Component comp : getComponents()) {
			if (bds.contains(comp.getBounds(g))) ret.add(comp);
		}
		return ret;
	}
	
	@Override	
	public WireSet getWireSet(Wire start) {
		return wires.getWireSet(start);
	}

	@Override
	public Bounds getBounds() {
		Bounds wireBounds = wires.getWireBounds();
		Iterator<Component> it = comps.iterator();
		if (!it.hasNext()) return wireBounds;
		Component first = it.next();
		Bounds firstBounds = first.getBounds();
		int xMin = firstBounds.getX();
		int yMin = firstBounds.getY();
		int xMax = xMin + firstBounds.getWidth();
		int yMax = yMin + firstBounds.getHeight();
		while (it.hasNext()) {
			Component c = it.next();
			Bounds bds = c.getBounds();
			int x0 = bds.getX(); int x1 = x0 + bds.getWidth();
			int y0 = bds.getY(); int y1 = y0 + bds.getHeight();
			if (x0 < xMin) xMin = x0;
			if (x1 > xMax) xMax = x1;
			if (y0 < yMin) yMin = y0;
			if (y1 > yMax) yMax = y1;
		}
		Bounds compBounds = Bounds.create(xMin, yMin, xMax - xMin, yMax - yMin);
		if (wireBounds.getWidth() == 0 || wireBounds.getHeight() == 0) {
			return compBounds;
		} else {
			return compBounds.add(wireBounds);
		}
	}

	@Override
	public Bounds getBounds(Graphics g) {
		Bounds ret = wires.getWireBounds();
		int xMin = ret.getX();
		int yMin = ret.getY();
		int xMax = xMin + ret.getWidth();
		int yMax = yMin + ret.getHeight();
		if (ret == Bounds.EMPTY_BOUNDS) {
			xMin = Integer.MAX_VALUE;
			yMin = Integer.MAX_VALUE;
			xMax = Integer.MIN_VALUE;
			yMax = Integer.MIN_VALUE;
		}
		for (Component c : comps) {
			Bounds bds = c.getBounds(g);
			if (bds != null && bds != Bounds.EMPTY_BOUNDS) {
				int x0 = bds.getX(); int x1 = x0 + bds.getWidth();
				int y0 = bds.getY(); int y1 = y0 + bds.getHeight();
				if (x0 < xMin) xMin = x0;
				if (x1 > xMax) xMax = x1;
				if (y0 < yMin) yMin = y0;
				if (y1 > yMax) yMax = y1;
			}
		}
		if (xMin > xMax || yMin > yMax) return Bounds.EMPTY_BOUNDS;
		return Bounds.create(xMin, yMin, xMax - xMin, yMax - yMin);
	}

	@Override
	List<Component> getClocks() {
		return clocks;
	}
	
	//
	// action methods
	//
	@Override
	public void setName(String name) {
		staticAttrs.setValue(LogicCircuitAttributes.NAME_ATTR, name);
	}
	
	private void showDebug(String message, Object parm) {
		PrintStream dest = DEBUG_STREAM;
		if (dest != null) {
			dest.println("mutatorAdd"); //OK
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace(dest); //OK
			}
		}
	}

	@Override
	void mutatorAdd(Component c) {
		showDebug("mutatorAdd", c);
		locker.checkForWritePermission("add");

		if (c instanceof Wire) {
			Wire w = (Wire) c;
			if (w.getEnd0().equals(w.getEnd1())) return;
			boolean added = wires.add(w);
			if (!added) return;
		} else {
			// add it into the circuit
			boolean added = comps.add(c);
			if (!added) return;

			wires.add(c);
			ComponentFactory factory = c.getFactory();
			if (factory instanceof Clock) {
				clocks.add(c);
			} else if (factory instanceof SubcircuitFactory) {
				SubcircuitFactory subcirc = (SubcircuitFactory) factory;
				subcirc.getSubcircuit().circuitsUsingThis.put(c, this);
			}
			c.addComponentListener(myComponentListener);
		}
		fireEvent(CircuitEvent.ACTION_ADD, c);
	}

	@Override
	void mutatorRemove(Component c) {
		showDebug("mutatorRemove", c);
		locker.checkForWritePermission("remove");

		if (c instanceof Wire) {
			wires.remove(c);
		} else {
			wires.remove(c);
			comps.remove(c);
			ComponentFactory factory = c.getFactory();
			if (factory instanceof Clock) {
				clocks.remove(c);
			} else if (factory instanceof SubcircuitFactory) {
				SubcircuitFactory subcirc = (SubcircuitFactory) factory;
				subcirc.getSubcircuit().circuitsUsingThis.remove(c);
			}
			c.removeComponentListener(myComponentListener);
		}
		fireEvent(CircuitEvent.ACTION_REMOVE, c);
	}

	//
	// Graphics methods
	//
	@Override
	public void draw(ComponentDrawContext context, Collection<Component> hidden) {
		Graphics g = context.getGraphics();
		Graphics g_copy = g.create();
		context.setGraphics(g_copy);
		wires.draw(context, hidden);

		if (hidden == null || hidden.size() == 0) {
			for (Component c : comps) {
				Graphics g_new = g.create();
				context.setGraphics(g_new);
				g_copy.dispose();
				g_copy = g_new;

				c.draw(context);
			}
		} else {
			for (Component c : comps) {
				if (!hidden.contains(c)) {
					Graphics g_new = g.create();
					context.setGraphics(g_new);
					g_copy.dispose();
					g_copy = g_new;

					try {
						c.draw(context);
					} catch (RuntimeException e) {
						// this is a JAR developer error - display it and move on
						e.printStackTrace();
					}
				}
			}
		}
		context.setGraphics(g);
		g_copy.dispose();
	}
}
