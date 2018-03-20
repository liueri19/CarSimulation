package network;

import java.util.List;

/**
 * An InputNode is a Node that is not connected to any previous Node.
 */
public class InputNode extends Node {
	/**
	 * Construct an InputNode connected to the specified list of nodes.
	 */
	public InputNode(long id, List<Connection> outputs) {
		super(id, null, outputs);
	}


	@Override
	protected String buildStringID() {
		return 'I' + Long.toHexString(getID());
	}
}
