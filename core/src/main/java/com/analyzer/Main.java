package com.analyzer;

public class Main {
    public static void main(String[] args) {
       
        String sequence = "AUCGG";

        String comp = Complement.getComplement(sequence);

        System.out.println("Complement Sequence: " + comp);
    }


}