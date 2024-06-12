package dfa_minimization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ImportData {
    static String fileName = "src/dfa_minimization/input.txt"; // Lưu ý nhập đúng đường dẫn trước khi chạy chương trình.

    public static DFA getDFA() {
        Set<String> states = new LinkedHashSet<>();
        Set<String> alphabet = new LinkedHashSet<>();
        String initialState = "";
        Map<String, Map<String, String>> transitionTable = new LinkedHashMap<>();
        Set<String> acceptingStates = new LinkedHashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                }
                List<String> partList = Arrays.stream(parts).filter(s -> !s.isEmpty()).toList();

                if (lineNumber == 1 && partList.size() < 2) {
                    throw new RuntimeException("Thiếu thông tin.");
                }
                if (lineNumber == 1) {
                    states.addAll(parseStates(partList));
                } else if (lineNumber == 2) {
                    alphabet.addAll(parseAlphabet(partList));
                } else if (lineNumber == 3) {
                    initialState = parseInitialState(partList, states);
                } else if (lineNumber > 3 && reader.ready()) {
                    updateTransitionTable(partList, states, transitionTable);
                } else if (!reader.ready()) {
                    acceptingStates.addAll(parseAcceptingStates(partList, states));
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return new DFA(states, alphabet, initialState, new DFA.TransitionFunction(transitionTable), acceptingStates);
    }

    private static Set<String> parseStates(List<String> parts) {
        Set<String> states = new LinkedHashSet<>();
        for (String state : parts) {
            if (!states.add(state)) {
                throw new IllegalArgumentException("Trạng thái không được trùng lặp.");
            }
        }
        return states;
    }

    private static Set<String> parseAlphabet(List<String> parts) {
        Set<String> alphabet = new LinkedHashSet<>();
        for (String symbol : parts) {
            if (!alphabet.add(symbol)) {
                throw new IllegalArgumentException("Chữ cái không được trùng lặp.");
            }
        }
        return alphabet;
    }

    private static String parseInitialState(List<String> parts, Set<String> states) {
        if (parts.size() != 1 || !states.contains(parts.get(0))) {
            throw new RuntimeException("Trạng thái ban đầu không hợp lệ.");
        }

        return parts.get(0);
    }

    private static Set<String> parseAcceptingStates(List<String> parts, Set<String> states) {
        Set<String> acceptingStates = new LinkedHashSet<>(parseStates(parts));

        if (!states.containsAll(acceptingStates)) {
            throw new RuntimeException("Lỗi: Tập trạng thái chấp nhận không hợp lệ.");
        }

        return acceptingStates;
    }

    private static void updateTransitionTable(List<String> parts, Set<String> states, Map<String, Map<String, String>> transitionTable) {
        if (parts.size() != 3) {
            throw new RuntimeException("Lỗi ở dòng: " + String.join(",", parts) + ": Số lượng phần tử không hợp lệ.");
        }

        String currentState = parts.get(0);
        String symbol = parts.get(1);
        String nextState = parts.get(2);

        if (!states.contains(currentState)) {
            throw new IllegalArgumentException("Trạng thái '" + currentState + "' không có trong tập trạng thái.");
        }
        if (!states.contains(nextState)) {
            throw new IllegalArgumentException("Trạng thái '" + nextState + "' không có trong tập trạng thái.");
        }

        Map<String, String> transitions = transitionTable.getOrDefault(currentState, new LinkedHashMap<>());
        if (transitions.containsKey(symbol)) {
            throw new RuntimeException("dfa_minimization.DFA không đơn định: trạng thái " + currentState + " có nhiều hơn một chuyển trạng thái cho chữ cái " + symbol);
        }
        transitions.put(symbol, nextState);
        transitionTable.put(currentState, transitions);
    }
}
