package network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OutputNode extends Node {
	public OutputNode() {
		this(new ArrayList<>());
	}

	public OutputNode(List<Connection> inputs) {
		super(inputs, Collections.emptyList());
	}

	@Override
	protected String buildStringID() {
		return 'O' + Long.toHexString(getID());
	}
}
