package dfa_minimization;

import java.util.*;

public class DFA_Minimization {
    public static DFA minimize(DFA dfa) {
        Set<String> alphabet = dfa.getAlphabet();
        DFA.TransitionFunction delta = dfa.getDelta();
        String initialState = dfa.getInitialState();
        Set<String> acceptingStates = dfa.getAcceptingStates();

        Set<String> states = findReachableStates(dfa);

        // Khởi tạo bảng
        Map<Pair<String, String>, Boolean> marked = new LinkedHashMap<>();
        for (String state1 : states) {
            for (String state2 : states) {
                if (state1.equals(state2)) continue;
                Pair<String, String> pair = new Pair<>(state1, state2);
                if (acceptingStates.contains(state1) ^ acceptingStates.contains(state2)) {
                    marked.put(pair, true);
                } else {
                    marked.put(pair, false);
                }
            }
        }

        // Đánh dấu các cặp
        boolean changed;
        do {
            changed = false;
            for (String state1 : states) {
                for (String state2 : states) {
                    if (state1.equals(state2)) continue;
                    Pair<String, String> pair = new Pair<>(state1, state2);
                    if (marked.containsKey(pair) && marked.get(pair)) continue;
                    for (String symbol : alphabet) {
                        String nextState1 = delta.transition(state1, symbol);
                        String nextState2 = delta.transition(state2, symbol);
                        Pair<String, String> nextPair = new Pair<>(nextState1, nextState2);
                        if (marked.containsKey(nextPair) && marked.get(nextPair)) {
                            marked.put(pair, true);
                            changed = true;
                            break;
                        }
                    }
                }
            }
        } while (changed);

        // Cập nhật các lớp tương đương
        Map<String, Set<String>> equivalenceClasses = new LinkedHashMap<>();
        for (String state : states) {
            equivalenceClasses.put(state, new LinkedHashSet<>());
        }
        for (Pair<String, String> pair : marked.keySet()) {
            if (!marked.get(pair)) {
                equivalenceClasses.get(pair.first()).add(pair.second());
                equivalenceClasses.get(pair.second()).add(pair.first());
            }
        }

        // Xây dựng DFA_Minimization
        Set<String> newStates = new LinkedHashSet<>();
        Map<String, Map<String, String>> newTransitionTable = new LinkedHashMap<>();
        Set<String> newAcceptingStates = new LinkedHashSet<>();

        String newInitialState = initialState;

        // Cập nhật trạng thái
        Set<Set<String>> processedStates = new LinkedHashSet<>();
        for (Map.Entry<String, Set<String>> equivalenceClass : equivalenceClasses.entrySet()) {
            String newState;
            if (!equivalenceClass.getValue().isEmpty()) {
                List<String> sortedStates = new ArrayList<>(equivalenceClass.getValue());
                Collections.sort(sortedStates);
                newState = equivalenceClass.getKey() + "-" + String.join("-", sortedStates);
            } else {
                newState = equivalenceClass.getKey();
            }

            Set<String> ElementsStatesName = new LinkedHashSet<>(Arrays.asList(newState.split("-")));
            if (!processedStates.contains(ElementsStatesName)) {
                processedStates.add(ElementsStatesName);
                newStates.add(newState);
                if (newState.contains(initialState)) {
                    newInitialState = newState; // cập nhật trạng thái bắt đầu
                }
            }

            // cập nhật trạng thái kết
            for (String accState : acceptingStates) {
                for (Set<String> state : processedStates) {
                    if (state.contains(accState)) {
                        newAcceptingStates.add(String.join("-", state));
                        break;
                    }
                }
            }
        }

        // cập nhật bảng chuyển trạng thái
        for (String state : newStates) {
            Map<String, String> transitions = new LinkedHashMap<>();
            for (String symbol : alphabet) {
                for (String element : state.split("-")) {
                    String nextState = delta.transition(element, symbol);
                    if (nextState != null) {
                        for (String s : newStates) {
                            if (s.contains(nextState)) {
                                transitions.put(symbol, s);
                            }
                        }
                    }
                }
            }
            newTransitionTable.put(state, transitions);
        }

        DFA.TransitionFunction newDelta = new DFA.TransitionFunction(newTransitionTable);
        return new DFA(newStates, alphabet, newInitialState, newDelta, newAcceptingStates);
    }

    private static Set<String> findReachableStates(DFA dfa) {
        Set<String> reachableStates = new LinkedHashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(dfa.getInitialState());

        while (!queue.isEmpty()) {
            String currentState = queue.poll();
            if (!reachableStates.contains(currentState)) {
                reachableStates.add(currentState);
                for (String symbol : dfa.getAlphabet()) {
                    String nextState = dfa.getDelta().transition(currentState, symbol);
                    if (nextState != null) {
                        queue.add(nextState);
                    }
                }
            }
        }

        return reachableStates;
    }

    record Pair<T, U>(T first, U second) {

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) obj;
            return first.equals(pair.first) && second.equals(pair.second);
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }
}
