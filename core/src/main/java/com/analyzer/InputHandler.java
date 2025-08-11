package com.analyzer;

public class InputHandler {
    public enum SequenceType { DNA, RNA, UNKNOWN }

    public static SequenceType determineSequenceType(String sequence) {
        sequence = sequence.toUpperCase();

        boolean hasT = sequence.contains("T");
        boolean hasU = sequence.contains("U");

        if (hasT && !hasU){
            System.out.println("DNA detected");
            return SequenceType.DNA;
        }

        if (hasU && !hasT){
            System.out.println("RNA detected");
            return SequenceType.RNA;
        }

        if (hasU && hasT){
            return SequenceType.UNKNOWN;
        }

        if (!hasT && !hasU){
            return SequenceType.UNKNOWN;
        }

        return SequenceType.UNKNOWN;
    }
}
