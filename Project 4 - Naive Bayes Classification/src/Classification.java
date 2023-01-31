import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class Classification {

    public static void main(String[] args) {
        // Trains to see how to classify an email as spam or ham and generates the full vocab
        Train trainingSet = new Train();
        trainingSet.trainSpam();
        trainingSet.trainHam();
        trainingSet.fullVocab();

        // Keeps track of how many emails were classified correctly
        int classifiedRight = 0;

        // Creates a map of the spam email inputs and then runs through each email
        HashMap<Integer, ArrayList<String>> spamEmails = splitEmails("test-spam.txt");
        for (int i : spamEmails.keySet()) {
            System.out.print("TEST " + i + " ");

            // Checks the number of features that occur in the email against the vocab
            ArrayList<String> currEmail = spamEmails.get(i);
            Set<String> fullVocab = trainingSet.getAllVocab();
            int featureTrue = featuresTrue(fullVocab, currEmail);
            System.out.print(featureTrue + "/" + trainingSet.getAllVocab().size() + " features true ");

            // Calculates the log prob of the current email being spam or ham
            double currEmailSpamProb = getSpamProb(trainingSet, spamEmails.get(i));
            System.out.printf("%.3f ", currEmailSpamProb);
            double currEmailHamProb = getHamProb(trainingSet, spamEmails.get(i));
            System.out.printf("%.3f ", currEmailHamProb);

            // Determines if the current email is classified correctly
            if (currEmailSpamProb > currEmailHamProb) {
                System.out.println("spam right");
                classifiedRight += 1;
            }
            else {
                System.out.println("ham wrong");
            }
        }

        // Creates a map of the ham email inputs and then runs through each email
        HashMap<Integer, ArrayList<String>> hamEmails = splitEmails("test-ham.txt");
        for (int j : hamEmails.keySet()) {
            System.out.print("Test " + j + " ");

            // Checks the number of features that occur in email against the vocab
            ArrayList<String> currEmail = hamEmails.get(j);
            Set<String> fullVocab = trainingSet.getAllVocab();
            int featureTrue = featuresTrue(fullVocab, currEmail);
            System.out.print(featureTrue + "/" + trainingSet.getAllVocab().size() + " features true ");

            // Calculates the log prob of the current email being spam or ham
            double currEmailSpamProb = getSpamProb(trainingSet, hamEmails.get(j));
            System.out.printf("%.3f ", currEmailSpamProb);
            double currEmailHamProb = getHamProb(trainingSet, hamEmails.get(j));
            System.out.printf("%.3f ", currEmailHamProb);

            // Determines if the current email is classified correctly
            if (currEmailHamProb > currEmailSpamProb) {
                System.out.println("ham right");
                classifiedRight += 1;
            }
            else {
                System.out.println("spam wrong");
            }
        }

        // Prints the number of emails that were classified correctly
        System.out.println("Total: " + classifiedRight + "/" + (spamEmails.size() + hamEmails.size()) + " emails classified correctly.");
    }

    /** Determines how many features occur in the email against the vocabulary */
    public static int featuresTrue(Set<String> fullVocab, ArrayList<String> email) {
        int count = 0;
        for (String s : email) {
            if (fullVocab.contains(s)) {
                count += 1;
            }
        }
        return count;
    }

    /** Determines the probability of an email being spam */
    public static double getSpamProb(Train trainingSet, ArrayList<String> email) {
        // Calculates the probability that the email is spam given the total email count
        double spamEmailCount = trainingSet.getSpamEmailCount();
        double totalEmailCount = trainingSet.getTotalEmailCount();
        double probSpam = spamEmailCount / totalEmailCount;

        // Gets the spam and full vocab
        HashMap<String, Integer> trainSpamVocab = trainingSet.getSpamVocab();
        Set<String> fullVocab = trainingSet.getAllVocab();

        // Stores the log prob for the email
        double logProb = 0.0;
        logProb += Math.log(probSpam);

        // Runs through each word in the vocabulary
        for (String s : fullVocab) {
            // If the word is in the email but not in the spam vocab, then the prob of it being spam is 0
            if ((email.contains(s)) && (!trainSpamVocab.containsKey(s))) {
                double numer = 1;
                double denom = spamEmailCount + 2;
                logProb += Math.log(numer / denom);
            }
            // If the word is not in the email and not in the spam vocab, then the prob of it being spam is 0
            else if (!(email.contains(s)) && (!trainSpamVocab.containsKey(s))) {
                double numer = 1;
                double denom = spamEmailCount + 2;
                logProb += Math.log(1.0 - (numer / denom));
            }
            // If the word is in the email, then get how many emails the word appears in and divide by the num of spam emails
            else if (email.contains(s)) {
                double numer = trainSpamVocab.get(s) + 1;
                double denom = spamEmailCount + 2;
                logProb += Math.log(numer / denom);
            }
            // If the word is not in the email, then take the prob of 1 minus the prob of the word being in the email
            else {
                double numer = trainSpamVocab.get(s) + 1;
                double denom = spamEmailCount + 2;
                logProb += Math.log(1.0 - (numer / denom));
            }
        }
        return logProb;
    }

    /** Determines the probability of an email being ham */
    public static double getHamProb(Train trainingSet, ArrayList<String> email) {
        // Calculates the probability that the email is ham given the total email count
        double hamEmailCount = trainingSet.getHamEmailCount();
        double totalEmailCount = trainingSet.getTotalEmailCount();
        double probHam = hamEmailCount / totalEmailCount;

        // Gets the ham and full vocab
        HashMap<String, Integer> trainHamVocab = trainingSet.getHamVocab();
        Set<String> fullVocab = trainingSet.getAllVocab();

        // Stores the log prob for the email
        double logProb = 0.0;
        logProb += Math.log(probHam);

        // Runs through each word in the vocabulary
        for (String s : fullVocab) {
            // If the word is in the email but not in the ham vocab, then the prob of it being ham is 0
            if ((email.contains(s)) && (!trainHamVocab.containsKey(s))) {
                double numer = 1;
                double denom = hamEmailCount + 2;
                logProb += Math.log(numer / denom);
            }
            // If the word is not in the email and not in the ham vocab, then the prob of it being ham is 0
            else if (!(email.contains(s)) && (!trainHamVocab.containsKey(s))) {
                double numer = 1;
                double denom = hamEmailCount + 2;
                logProb += Math.log(1.0 - (numer / denom));
            }
            // If the word is in the email, then get how many emails the word appears in and divide by the num of ham emails
            else if (email.contains(s)) {
                double numer = trainHamVocab.get(s) + 1;
                double denom = hamEmailCount + 2;
                logProb += Math.log(numer / denom);
            }
            // If the word is not in the email, then take the prob of 1 minus the prob of the word being in the email
            else {
                double numer = trainHamVocab.get(s) + 1;
                double denom = hamEmailCount + 2;
                logProb += Math.log(1.0 - (numer / denom));
            }
        }
        return logProb;
    }

    /** Splits the text file into a hashmap of emails */
    public static HashMap<Integer, ArrayList<String>> splitEmails(String textFile) {
        InputStream is = Classification.class.getResourceAsStream(textFile);
        Scanner scan = new Scanner(is);

        // Create an arrayList that holds the list of words in a current email
        HashMap<Integer, ArrayList<String>> emails = new HashMap<>();
        ArrayList<String> emailWords = null;
        int count = 0;

        // Runs through the text file line by line
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] words = line.split(" ");

            for (String word : words) {
                // Checks if the current word is "SUBJECT", then add 1 to email count and create a new list for the words in the email
                if (word.equals("<SUBJECT>")) {
                    count += 1;
                    emailWords = new ArrayList<>();
                }
                // If we have reached the end of the email, add to the map for emails
                else if (word.equals("</BODY>")) {
                    emails.put(count, emailWords);
                }
                // Skips over "subject" and "body" and "new lines" in the current email
                else if (word.equals("</SUBJECT>") || word.equals("<BODY>") || word.equals("")) {
                    continue;
                }
                // Converts the word to lower case letters
                String lowerCase = word.toLowerCase();
                // Checks if the list of words in the email already has the word and if not, adds it
                if (!(emailWords.contains(lowerCase))) {
                    emailWords.add(lowerCase);
                }
            }
        }
        return emails;
    }
}