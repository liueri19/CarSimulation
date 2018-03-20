package network;

import java.util.Collections;
import java.util.Set;

public class InputNode extends Node {
	public InputNode(Set<Connection> outputs) {
		super(Collections.emptySortedSet(), outputs);
	}

	@Override
	protected String buildStringID() {
		return 'I' + Long.toHexString(getID());
	}
}
