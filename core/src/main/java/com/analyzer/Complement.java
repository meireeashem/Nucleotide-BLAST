package com.analyzer;

import com.analyzer.InputHandler.SequenceType;

public class Complement {

    private static final SequenceType SequenceType = nullß;

    public static String getComplement(String sequence) {
        StringBuilder result = new StringBuilder();

        for (char base : sequence.toUpperCase().toCharArray()) {

           
            switch (base) {
                case 'A':
                    result.append(SequenceType == SequenceType.DNA ? 'T' : 'U');
                    break;
                case 'T':
                    if (SequenceType == SequenceType.DNA)
                        result.append('A');
                    else
                        result.append('-'); // invalid for RNA
                    break;
                case 'U':
                    if (SequenceType == SequenceType.RNA)
                        result.append('A');
                    else
                        result.append('-'); // invalid for DNA
                    break;
                case 'G':
                    result.append('C');
                    break;
                case 'C':
                    result.append('G');
                    break;
                default:
                    result.append('-'); // unknown character
                    break;
            }
        }

        return result.toString();
    }
}
