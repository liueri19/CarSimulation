package network;

import java.util.List;

public class OutputNode extends Node {
	public OutputNode(long id, List<Connection> inputs) {
		super(id, inputs, null);
	}

	@Override
	protected String buildStringID() {
		return 'O' + Long.toHexString(getID());
	}
}
