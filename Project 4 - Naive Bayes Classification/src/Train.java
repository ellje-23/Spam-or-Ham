import java.io.InputStream;
import java.util.*;

public class Train {
    /** A map of the words in spam emails and how many emails it occurred in */
    private HashMap<String, Integer> spamVocab;
    /** The number of spam emails */
    private int spamEmailCount;
    /** A map of the words in ham emails and how many emails it occurred in */
    private HashMap<String, Integer> hamVocab;
    /** The number of ham emails */
    private int hamEmailCount;
    /** A map of all the words seen in spam and ham emails */
    private Set<String> allVocab;
    /** The total number of emails */
    private int totalEmailCount;
    /** A list of words to skip when reading an email */
    private ArrayList<String> skipWords;

    public Train() {
        this.spamVocab = new HashMap<>();
        this.spamEmailCount = 0;

        this.hamVocab = new HashMap<>();
        this.hamEmailCount = 0;

        this.allVocab = new HashSet<>();
        this.totalEmailCount = 0;

        this.skipWords = new ArrayList<>();
        skipWords.add("<SUBJECT>");
        skipWords.add("</SUBJECT>");
        skipWords.add("<BODY>");
        skipWords.add("</BODY>");
        skipWords.add("");
    }

    /** Trains to see the contents of a spam email */
    public void trainSpam() {
        InputStream is = Classification.class.getResourceAsStream("train-spam.txt");
        Scanner scan = new Scanner(is);

        // Creates a list of words in a current email
        ArrayList<String> emailWord = new ArrayList<>();

        // Reads the file line by line
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] words = line.split(" ");

            for (String word : words) {
                // If the current word is "<SUBJECT>", then add 1 to email count
                if (word.equals("<SUBJECT>")) {
                    spamEmailCount += 1;
                }
                // If the current word is "</BODY>", then clear the list of words in an email since the end is reached
                if (word.equals("</BODY>")) {
                    emailWord.clear();
                }
                // Skips over the skip words
                if (skipWords.contains(word)) {
                    continue;
                }
                // Converts the word to lowercase
                String currword = word.toLowerCase();

                // If the word is already in the map but not already seen in the email, increment it's count by 1
                if ((spamVocab.containsKey(currword)) && (!emailWord.contains(currword))) {
                    int count = spamVocab.get(currword);
                    count += 1;
                    spamVocab.put(currword, count);
                }
                // If the word is already in the map and already seen in the email, then skip it
                else if ((spamVocab.containsKey(currword)) && (emailWord.contains(currword))) {
                    continue;
                }
                // Otherwise, add the word to the spam vocab
                else {
                    spamVocab.put(currword, 1);
                }
                // If the word wasn't in the email word list, then add it
                if (!(emailWord.contains(currword))) {
                    emailWord.add(currword);
                }
            }
        }
    }

    /** Trains to see the contents of a ham email */
    public void trainHam() {
        InputStream is = Classification.class.getResourceAsStream("train-ham.txt");
        Scanner scan = new Scanner(is);

        // Creates a list of words in a current email
        ArrayList<String> emailWord = new ArrayList<>();

        // Reads the file line by line
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] words = line.split(" ");

            for (String word : words) {
                // If the current word is "<SUBJECT>", then add 1 to email count
                if (word.equals("<SUBJECT>")) {
                    hamEmailCount += 1;
                }
                // If the current word is "</BODY>", then clear the list of words in an email since the end is reached
                if (word.equals("</BODY>")) {
                    emailWord.clear();
                }
                // Skips over the skip words
                if (skipWords.contains(word)) {
                    continue;
                }
                // Converts the word to lowercase
                String currword = word.toLowerCase();

                // If the word is already in the map but not already seen in the email, increment it's count by 1
                if ((hamVocab.containsKey(currword)) && (!emailWord.contains(currword))) {
                    int count = hamVocab.get(currword);
                    count += 1;
                    hamVocab.put(currword, count);
                }
                // If the word is already in the map and already seen in the email, then skip it
                else if ((hamVocab.containsKey(currword)) && (emailWord.contains(currword))) {
                    continue;
                }
                // Otherwise, add the word to the spam vocab
                else {
                    hamVocab.put(currword, 1);
                }
                // If the word wasn't in the email word list, then add it
                if (!(emailWord.contains(currword))) {
                    emailWord.add(currword);
                }
            }
        }

        // Runs through the spam vocab and add makes the count 0 in ham for each word not in ham vocab
        for (String s : spamVocab.keySet()) {
            if (!hamVocab.containsKey(s)) {
                hamVocab.put(s, 0);
            }
        }
    }

    /** Creates the full vocab set by running through spam and ham vocab */
    public void fullVocab() {
        for (String n : spamVocab.keySet()) {
           allVocab.add(n);
        }
        for (String m : hamVocab.keySet()) {
            allVocab.add(m);
        }
        // Calculates the total email count by adding spam and ham email counts together
        totalEmailCount = spamEmailCount + hamEmailCount;
    }

    /** Returns the entire vocab set */
    public Set<String> getAllVocab() { return allVocab; }

    /** Returns the entire ham vocab set */
    public HashMap<String, Integer> getHamVocab() {
        return hamVocab;
    }

    /** Returns the entire spam vocab set */
    public HashMap<String, Integer> getSpamVocab() {
        return spamVocab;
    }

    /** Returns the number of ham emails */
    public int getHamEmailCount() {
        return hamEmailCount;
    }

    /** Returns the number of spam emails */
    public int getSpamEmailCount() {
        return spamEmailCount;
    }

    /** Returns the number of total emails */
    public int getTotalEmailCount() {
        return totalEmailCount;
    }
}