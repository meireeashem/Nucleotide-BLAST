package com.analyzer;

import com.FastaReader;
import com.analyzer.Complement;
import com.analyzer.InputHandler;
import javax.swing.*;
import java.awt.*;

public class GUI {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Sequence Analyzer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        // Background gif as a JLabel with icon
        ImageIcon gif = new ImageIcon(GUI.class.getResource("/dnagif.gif"));
        JLabel backgroundLabel = new JLabel(gif);

        // Use a JPanel with transparent background for UI components
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Text field
        JTextField textBox = new JTextField(20); // 30 columns wide
        gbc.gridy = 0;
        textBox.setPreferredSize(new Dimension(300, 50)); 
        panel.add(textBox, gbc);

        // Find Complement Button
        JButton complementButton = new JButton("Find Complement");
        gbc.gridy = 1;
        complementButton.setPreferredSize(new Dimension(80, 50));
        panel.add(complementButton, gbc);

        // Analyze FASTA Button
        JButton fastaButton = new JButton("Analyze FASTA");
        fastaButton.setPreferredSize(new Dimension(80, 50));
        gbc.gridy = 2;

        panel.add(fastaButton, gbc);

        // Result Label
        JLabel resultLabel = new JLabel("Result: ", SwingConstants.CENTER);
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridy = 3;
        panel.add(resultLabel, gbc);

        // Use a layered pane to put the background behind the panel
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 800));
        backgroundLabel.setBounds(0, 0, 800, 800);
        panel.setBounds(0, 0, 800, 800);

        layeredPane.add(backgroundLabel, Integer.valueOf(0));
        layeredPane.add(panel, Integer.valueOf(1));

        frame.setContentPane(layeredPane);
        frame.pack();
        frame.setLocationRelativeTo(null); // center on screen
        frame.setVisible(true);
        frame.setResizable(false);

        // Action listeners
        complementButton.addActionListener(e -> {
            String input = textBox.getText();
            if (input.isBlank()) {
                resultLabel.setText("Result: Invalid input");
                return;
            }
            String complement = Complement.getComplement(input);
            resultLabel.setText("Result: " + complement);
        });

        fastaButton.addActionListener(e -> {
            String input = textBox.getText();
            if (input.isBlank()) {
                resultLabel.setText("Result: Invalid input");
                return;
            }
            try {
                String fastaResult = FastaReader.identifyNucleotide(input);
                String htmlResult = "<html>" + fastaResult.replaceAll("\n", "<br>") + "</html>";
                resultLabel.setText(htmlResult);
            } catch (Exception ex) {
                ex.printStackTrace();
                resultLabel.setText("Result: Error!");
            }
        });
    }
}
