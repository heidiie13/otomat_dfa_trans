package dfa_minimization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        DFA dfaOrigin = ImportData.getDFA();
        String original_dfa = dfaOrigin.toString();
        String minimal_dfa = DFA_Minimization.minimize(dfaOrigin).toString();
        String output = "Original DFA:\n" +
                original_dfa + "\n" +
                "Minimal DFA of Original DFA:\n" +
                minimal_dfa;

        String file_name = "src/dfa_minimization/output.txt";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file_name));
            writer.write(output);
            writer.close();

            System.out.println("Tối tiểu hóa thành công, kết quả được ghi trong file: " + file_name);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
