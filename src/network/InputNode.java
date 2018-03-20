package network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An InputNode is a Node that is not connected to any previous Node.
 */
public class InputNode extends Node {
//	/**
//	 * Constructs an InputNode not connected to any other node.
//	 */
//	public InputNode() {
//		this(new ArrayList<>());
//	}
//
//	/**
//	 * Construct an InputNode connected to the specified list of nodes.
//	 */
//	public InputNode(List<Connection> outputs) {
//		super(Collections.emptyList(), outputs);
//	}

	public static class InputNodeBuilder extends Node.NodeBuilder {
		@Override
		public NodeBuilder setPrevConnections(List<Connection> prevConnections) {
			throw new UnsupportedOperationException();
		}
	}


	@Override
	protected String buildStringID() {
		return 'I' + Long.toHexString(getID());
	}
}
