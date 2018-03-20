package network;

import java.util.Collections;
import java.util.SortedSet;

public class OutputNode extends Node {
	public OutputNode(SortedSet<Connection> inputs) {
		super(inputs, Collections.emptySortedSet());
	}

	@Override
	protected String buildStringID() {
		return 'O' + Long.toHexString(getID());
	}
}
