package network;

public enum NodeType {
	INPUT("I"), OUTPUT("O"), HIDDEN("H");

	private final String prefix;

	NodeType(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public String toString() {
		return prefix;
	}

	/**
	 * Returns the NodeType represented by the specified String.
	 * @throws IllegalArgumentException	if the specified String does not match any
	 * NodeType.
	 */
	public static NodeType of(String type)
			throws IllegalArgumentException {

		for (NodeType nodeType : NodeType.values()) {
			if (type.equals(nodeType.toString()))
				return nodeType;
		}

		throw new IllegalArgumentException("No matching NodeType: " + type);
	}
}
