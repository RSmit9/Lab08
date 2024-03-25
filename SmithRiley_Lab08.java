import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TagExtractorGUI extends JFrame implements ActionListener
    {
        private JTextArea outputTextArea;
        private JButton selectFileButton;
        private JButton selectStopWordsButton;
        private JButton extractTagsButton;
        private JButton saveTagsButton;
        private File selectedFile;
        private File stopWordsFile;
        private Map<String, Integer> tagFrequencyMap;
        private Set<String> stopWords;
        public TagExtractorGUI() {
            setTitle("Tag/Keyword Extractor");
            setSize(800, 800);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            initComponents();
    }
private void initComponents()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        outputTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        selectFileButton = new JButton("Select A Text File Please");
        selectFileButton.addActionListener(this);

        selectStopWordsButton = new JButton("Please Select Stop Words File");
        selectStopWordsButton.addActionListener(this);

        extractTagsButton = new JButton("Select The Extract Tags");
        extractTagsButton.addActionListener(this);

        saveTagsButton = new JButton("Save Tags");
        saveTagsButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectFileButton);
        buttonPanel.add(selectStopWordsButton);
        buttonPanel.add(extractTagsButton);
        buttonPanel.add(saveTagsButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }
@Override
public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == selectFileButton)
                {
                    selectTextFile();
                }
            else if (e.getSource() == selectStopWordsButton)
                {
                    selectStopWordsFile();
                }
            else if (e.getSource() == extractTagsButton)
                {
                    extractTags();
                }
            else if (e.getSource() == saveTagsButton)
                {
                    saveTags();
                }
        }
private void selectTextFile()
    {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION)
            {
                selectedFile = fileChooser.getSelectedFile();
                outputTextArea.append("Selected Text File: " + selectedFile.getName() + "\n");
            }
    }
private void selectStopWordsFile()
    {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION)
        {
            stopWordsFile = fileChooser.getSelectedFile();
            loadStopWords();
            outputTextArea.append("Selected Stop Words File: " + stopWordsFile.getName() + "\n");
        }
    }
private void loadStopWords()
    {
        stopWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile)))
    {
            String line;
            while ((line = reader.readLine()) != null)
                {
                    stopWords.add(line.trim().toLowerCase());
                }
        } catch (IOException e)
            {
                e.printStackTrace();
            }
    }

private void extractTags()
    {
        if (selectedFile == null || stopWordsFile == null)
            {
                JOptionPane.showMessageDialog(this, "Please select a text file AND a stop word file.");
                return;
            }
        tagFrequencyMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile)))
            {
                String line;
                while ((line = reader.readLine()) != null)
            {
                line = line.replaceAll("[^a-zA-Z ]", "").toLowerCase();
                String[] words = line.split("\\s+");
                for (String word : words)
                    {
                        if (!stopWords.contains(word))
                            {
                                tagFrequencyMap.put(word, tagFrequencyMap.getOrDefault(word, 0) + 1);
                            }
                    }
            }
            displayTags();
        }
            catch (IOException e)
                {
                    e.printStackTrace();
                }
    }

private void displayTags()
    {
        outputTextArea.setText("Tags and Frequencies:\n");
        for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet())
            {
                outputTextArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
            }
    }
private void saveTags()
    {
        if (tagFrequencyMap == null || tagFrequencyMap.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "No tags to save.");
                return;
            }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Tags to File");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION)
        {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileToSave))) 
            {
                for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet())
                    {
                        writer.println(entry.getKey() + ": " + entry.getValue());
                    }
                JOptionPane.showMessageDialog(this, "Tags saved successfully.");
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error occurred while saving tags.");
                }
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
            {
                TagExtractorGUI gui = new TagExtractorGUI();
                gui.setVisible(true);
            });
    }
}
