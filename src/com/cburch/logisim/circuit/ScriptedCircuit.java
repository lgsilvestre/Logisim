package com.cburch.logisim.circuit;

import java.awt.Graphics;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.cburch.logisim.circuit.appear.CircuitAppearance;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;

public class ScriptedCircuit extends Circuit {
	private CircuitAppearance appearance;
	private AttributeSet staticAttrs;
	private SubcircuitFactory subcircuitFactory;
	private CircuitLocker locker;
	
	public ScriptedCircuit (String name) {
		appearance = new CircuitAppearance(this);
		staticAttrs = ScriptedCircuitAttributes.createBaseAttrs(this, name);
		subcircuitFactory = new SubcircuitFactory(this);
		locker = new CircuitLocker();
	}
	
	@Override
	public String toString() {
		return staticAttrs.getValue(ScriptedCircuitAttributes.NAME_ATTR);
	}

	@Override
	CircuitLocker getLocker() {
		return locker;
	}

	@Override
	public AttributeSet getStaticAttributes() {
		return staticAttrs;
	}

	@Override
	public String getName() {
		return staticAttrs.getValue(ScriptedCircuitAttributes.NAME_ATTR);
	}
	
	public int getNumInputs() {
		return (Integer)staticAttrs.getValue(ScriptedCircuitAttributes.CIRCUIT_INPUTS_ATTR);
	}

	public int getNumOutputs() {
		return (Integer)staticAttrs.getValue(ScriptedCircuitAttributes.CIRCUIT_OUTPUTS_ATTR);
	}

	@Override
	public CircuitAppearance getAppearance() {
		// TODO Auto-generated method stub
		return appearance;
	}

	@Override
	public SubcircuitFactory getSubcircuitFactory() {
		return subcircuitFactory;
	}

	@Override
	public Set<WidthIncompatibilityData> getWidthIncompatibilityData() {
		// TODO Auto-generated method stub
		return Collections.emptySet();
	}

	@Override
	public BitWidth getWidth(Location p) {
		// TODO Auto-generated method stub
		return BitWidth.ONE;
	}

	@Override
	public Location getWidthDeterminant(Location p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasConflict(Component comp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Component getExclusive(Location loc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(Component c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Wire> getWires() {
		return Collections.emptySet();
	}

	@Override
	public Set<Component> getNonWires() {
		return Collections.emptySet();
	}

	@Override
	public Collection<? extends Component> getComponents(Location loc) {
		return Collections.emptyList();
	}

	@Override
	public Collection<? extends Component> getSplitCauses(Location loc) {
		return Collections.emptyList();
	}

	@Override
	public Collection<Wire> getWires(Location loc) {
		return Collections.emptyList();
	}

	@Override
	public Collection<? extends Component> getNonWires(Location loc) {
		return Collections.emptyList();
	}

	@Override
	public boolean isConnected(Location loc, Component ignore) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Location> getSplitLocations() {
		return Collections.emptySet();
	}

	@Override
	public Collection<Component> getAllContaining(Location pt) {
		return Collections.emptyList();
	}

	@Override
	public Collection<Component> getAllContaining(Location pt, Graphics g) {
		return Collections.emptyList();
	}

	@Override
	public Collection<Component> getAllWithin(Bounds bds) {
		return Collections.emptyList();
	}

	@Override
	public Collection<Component> getAllWithin(Bounds bds, Graphics g) {
		return Collections.emptyList();
	}

	@Override
	public WireSet getWireSet(Wire start) {
		return WireSet.EMPTY;
	}

	@Override
	public Bounds getBounds() {
		return Bounds.EMPTY_BOUNDS;
	}

	@Override
	public Bounds getBounds(Graphics g) {
		return Bounds.EMPTY_BOUNDS;
	}

	@Override
	List<Component> getClocks() {
		return Collections.emptyList();
	}

	@Override
	public void setName(String name) {
		staticAttrs.setValue(ScriptedCircuitAttributes.NAME_ATTR, name);
	}
	
	
	public void setNumInputs(int numInputs) {
		staticAttrs.setValue(ScriptedCircuitAttributes.CIRCUIT_INPUTS_ATTR, numInputs);
	}

	public void setNumOutputs(int numOutputs) {
		staticAttrs.setValue(ScriptedCircuitAttributes.CIRCUIT_OUTPUTS_ATTR, numOutputs);
	}

	//
	// Graphics methods
	//
	@Override
	public void draw(ComponentDrawContext context, Collection<Component> hidden) {
	}

}
