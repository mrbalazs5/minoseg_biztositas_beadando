/*
 * ComponentConnection
 *
 * v1.0
 *
 * 2021.04.05
 *
 */

package main;

import java.util.ArrayList;
import java.util.List;

public class ComponentConnection implements Cloneable {
    public int id1;
    public int id2;
    public List<Integer> replacedIndices;

    public ComponentConnection(int id1, int id2) {
	this.id1 = id1;
	this.id2 = id2;
	this.replacedIndices = new ArrayList<Integer>();
    }

    @Override
    protected ComponentConnection clone() throws CloneNotSupportedException {
	ComponentConnection clone = (ComponentConnection) super.clone();
	List<Integer> copy = new ArrayList<Integer>();
	
	for (Integer i : replacedIndices) {
	    copy.add(Integer.valueOf(i));
	}
	
	clone.replacedIndices = new ArrayList<Integer>(replacedIndices);

	return clone;
    }

    @Override
    public String toString() {
	return id1 + "-" + id2;
    }

}
