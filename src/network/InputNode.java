package network;

import java.util.Collections;
import java.util.List;

public class InputNode extends Node {
	public InputNode(List<Connection> outputs) {
		super(Collections.emptyList(), outputs);
	}
}
