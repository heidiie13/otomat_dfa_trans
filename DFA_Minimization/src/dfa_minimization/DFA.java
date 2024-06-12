package dfa_minimization;

import java.util.*;

public class DFA {
    private Set<String> states;     // Tập trạng thái
    private Set<String> alphabet; // Bảng chữ cái
    private String initialState;     // Trạng thái ban đầu
    private TransitionFunction delta; // Hàm chuyển trạng thái
    private Set<String> acceptingStates; // Tập trạng thái kết

    public DFA(Set<String> states, Set<String> alphabet, String initialState, TransitionFunction delta, Set<String> acceptingStates) {
        this.states = states;
        this.alphabet = alphabet;
        this.initialState = initialState;
        this.delta = delta;
        this.acceptingStates = acceptingStates;
    }

    public Set<String> getStates() {
        return states;
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public String getInitialState() {
        return initialState;
    }

    public TransitionFunction getDelta() {
        return delta;
    }

    public Set<String> getAcceptingStates() {
        return acceptingStates;
    }

    public void setStates(Set<String> states) {
        this.states = states;
    }

    public void setAlphabet(Set<String> alphabet) {
        this.alphabet = alphabet;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public void setDelta(TransitionFunction delta) {
        this.delta = delta;
    }

    public void setAcceptingStates(Set<String> acceptingStates) {
        this.acceptingStates = acceptingStates;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Tập trạng thái: ").append(states).append("\n");
        result.append("Trạng thái bắt đầu: ").append(initialState).append("\n");
        result.append("Tập trạng thái kết: ").append(acceptingStates).append("\n");
        result.append("Bảng chuyển trạng thái:\n");

        // In header của bảng chữ cái
        result.append("    delta");
        for (String symbol : alphabet) {
            result.append(String.format("%9s", symbol));
        }
        result.append("\n");
        Set<String> newStates = new LinkedHashSet<>();
        newStates.add(initialState);
        for (String state : states) {
            if (!state.equals(initialState)) {
                newStates.add(state);
            }
        }
        // In nội dung của bảng chuyển trạng thái
        for (String state : newStates) {
            result.append(String.format("%9s", state));

            for (String symbol : alphabet) {
                String nextState = delta.transition(state, symbol);
                result.append(String.format("%9s", nextState));
            }
            result.append("\n");
        }

        return result.toString();
    }

    public record TransitionFunction(Map<String, Map<String, String>> transitionTable) {

        public String transition(String currentState, String symbol) {
            return transitionTable.getOrDefault(currentState, new LinkedHashMap<>())
                    .getOrDefault(symbol, null);
        }
    }
}

