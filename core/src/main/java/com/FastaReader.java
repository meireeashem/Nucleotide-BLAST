package com;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FastaReader {

    public static HttpClient client = HttpClient.newHttpClient();

    public FastaReader() {
    }

    /**
     * Reads FASTA format nucleotide string, submits to NCBI BLAST
     * and returns type of sequence(i.e: animal,homo sapiens etc).
     */
    public static String identifyNucleotide(String fastaSequence) throws Exception {

        if (fastaSequence == null || fastaSequence.isBlank()) {
            throw new IllegalArgumentException("Empty/null FASTA sequence entered.");
        }

        // Step 1: submit sequence
        String postData = "CMD=Put&PROGRAM=blastn&DATABASE=nt&QUERY=" + URLEncoder.encode(fastaSequence, "UTF-8");

        HttpRequest submitRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://blast.ncbi.nlm.nih.gov/Blast.cgi"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(postData))
                .build();

        HttpResponse<String> submitResponse = client.send(submitRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("Submit response body:\n" + submitResponse.body());  // Debug print

        // Extract request ID from the response received from BLAST
        Pattern ridPattern = Pattern.compile("RID\\s*=\\s*(\\S+)");
        Matcher ridMatcher = ridPattern.matcher(submitResponse.body());
        if (!ridMatcher.find()) {
            return "Error: Could not find the Request ID in BLAST submission response.";
        }

        String rid = ridMatcher.group(1);
        System.out.println("RID: " + rid);

        // Step 2: Poll for results with retry limit
        boolean ready = false;
        String results = "";
        int maxRetries = 10;
        int retries = 0;

        while (!ready && retries < maxRetries) {
            retries++;
            TimeUnit.SECONDS.sleep(8); // wait 8 seconds before checking

            String getUrl = "https://blast.ncbi.nlm.nih.gov/Blast.cgi?CMD=Get&RID=" + rid + "&FORMAT_OBJECT=Alignment&FORMAT_TYPE=Text";

            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(getUrl))
                    .GET()
                    .build();

            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            String body = getResponse.body();
            System.out.println(body);

            if (body.contains("Status=WAITING")) {
                System.out.println("BLAST search still running... Attempt " + retries);
            } else if (body.contains("Status=FAILED")) {
                return "Error: BLAST search failed. Response:\n" + body;
            } else if (body.contains("Status=UNKNOWN")) {
                return "Error: BLAST search status unknown. Response:\n" + body;
            } else {
                // Results ready
                results = body;
                ready = true;
            }
        }

        if (!ready) {
            return "Error: BLAST polling timed out after " + maxRetries + " attempts.";
        }

        // Step 3: Parse top hit and return it
        return parseTopHit(results);
    }

    /**
     * Method that takes the BLAST output produced and returns the description
     * of the top (first) sequence hit. Otherwise, it returns message saying
     * nothing was found
     */
    private static String parseTopHit(String blastText) {

    String marker = "Sequences producing significant alignments:";

    // finding the position of the marker in the blastText
    int index = blastText.indexOf(marker);

    if (index == -1) {
        return "No significant BLAST hits found.";
    }

    String hitsSection = blastText.substring(index + marker.length()).trim();
    String[] lines = hitsSection.split("\n");

    for (String line : lines) {
        line = line.trim();

        // Skip empty lines and header lines (common header keywords)
        if (line.isEmpty()) continue;

        // Skip lines that look like headers or column titles
        String lower = line.toLowerCase();
        if (lower.startsWith("(bits)") || lower.contains("value") || lower.contains("ident") || lower.contains("score")) {
            continue;
        }

        // First valid hit line found, return it
        return "Top BLAST hit(bits) Value Ident:<br>" + line;
    }
    // No valid hit lines found
    return "No significant BLAST hits found.";
}
}
