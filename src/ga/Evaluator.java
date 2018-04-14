package ga;

import network.Network;

@FunctionalInterface
public interface Evaluator {
	double evaluate(Network network);
}
