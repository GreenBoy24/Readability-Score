package readability;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String textPath = args[0];
        readingTexts(textPath);
    }

    public static void readingTexts(String textPath){
        try (BufferedReader br = new BufferedReader(new FileReader(textPath))) {
            String text = br.readLine();

            System.out.println("The text is:");
            System.out.println(text);
            System.out.println();

            dataText(text);
            System.out.println();

            choiceScore(text);

        } catch (FileNotFoundException e) {
            System.out.println("No file found: " + textPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dataText(String text){
        System.out.println("Words: " + wordsCounter(text));
        System.out.println("Sentences: " + sentencesCounter(text));
        System.out.println("Characters: " + charactersCounter(text));
        System.out.println("Syllables: " + syllablesCounter(text));
        System.out.println("Polysyllables: " + polysyllablesCounter(text));
    }

    public static int wordsCounter(String text) {
        return text.split("[ \\s]").length;
    }

    public static int sentencesCounter(String text) {
        return text.split("[!.?]").length;
    }

    public static int charactersCounter(String text) {
        return text.replaceAll("\\s", "").length();
    }

    public static int singleWordSyllables(String word) {
        int syllables = 0;
        String[] letters = word.split("");

        boolean wasVowel = false;
        for (var letter : letters) {
            if (letter.matches("[aeiouyAEIOUY]")) {
                if (!wasVowel) {
                    syllables++;
                    wasVowel = true;
                }
            } else {
                wasVowel = false;
            }
        }

        if (word.endsWith("e")) {
            syllables--;
        }

        return syllables == 0 ? 1 : syllables;
    }

    public static int syllablesCounter(String text) {
        String[] words = text.split("[,;:\"'!?.]?[ \\s]");
        int count = 0;

        for (var word : words) {
            count += singleWordSyllables(word);
        }

        return count;
    }

    public static int polysyllablesCounter(String text) {
        String[] words = text.split("[,;:\"'!?.]?[ \\s]");
        int count = 0;

        for (var word : words) {
            count += singleWordSyllables(word) > 2 ? 1 : 0;
        }

        return count;
    }

    public static void choiceScore(String text) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String choice = scanner.next();

        switch (choice) {
            case "ARI":
                System.out.format("\n\nAutomated Readability Index: %.2f (about %d year olds).\n\n",
                        ari(text), scoreConclusion(ari(text)));
                break;
            case "FK":
                System.out.format("\n\nFlesch–Kincaid readability tests: %.2f (about %d year olds).\n\n",
                        fk(text), scoreConclusion(fk(text)));
                break;
            case "SMOG":
                System.out.format("\n\nSimple Measure of Gobbledygook: %.2f (about %d year olds).\n\n",
                        smog(text), scoreConclusion(smog(text)));
                break;
            case "CL":
                System.out.format("\n\nColeman–Liau index: %.2f (about %d year olds).\n\n",
                        cl(text), scoreConclusion(cl(text)));
                break;
            case "all":
                int ageARI = scoreConclusion(ari(text));
                int ageFK = scoreConclusion(fk(text));
                int ageSMOG = scoreConclusion(smog(text));
                int ageCL = scoreConclusion(cl(text));

                System.out.format("\n\nAutomated Readability Index: %.2f (about %d year olds).\n",
                        ari(text), ageARI);
                System.out.format("Flesch–Kincaid readability tests: %.2f (about %d year olds).\n",
                        fk(text), ageFK);
                System.out.format("Simple Measure of Gobbledygook: %.2f (about %d year olds).\n",
                        smog(text), ageSMOG);
                System.out.format("Coleman–Liau index: %.2f (about %d year olds).\n\n",
                        cl(text), ageCL);

                double averageAge = (ageARI + ageFK + ageSMOG + ageCL) / 4.0;

                System.out.format("This text should be understood in average by %.2f year olds.", averageAge);
                break;
            default:
                System.out.println("Wrong choice");
        }

        System.out.println("\n");
    }

    public static int scoreConclusion(double score) {
        int age = 0;
        int roundScore = (int) Math.round(score);

        switch (roundScore) {
            case 1:
                age = 6;
                break;
            case 2:
                age = 7;
                break;
            case 3:
                age = 9;
                break;
            case 4:
                age = 10;
                break;
            case 5:
                age = 11;
                break;
            case 6:
                age = 12;
                break;
            case 7:
                age = 13;
                break;
            case 8:
                age = 14;
                break;
            case 9:
                age = 15;
                break;
            case 10:
                age = 16;
                break;
            case 11:
                age = 17;
                break;
            case 12:
                age = 18;
                break;
            case 13:
                age = 24;
                break;
            case 14:
                age = 100;
                break;
            default:
                System.out.println("Text size is zero");

        }
        return age;
    }

    public static double ari(String text) {
        int characters = charactersCounter(text);
        int words = wordsCounter(text);
        int sentences = sentencesCounter(text);

        return 4.71 * characters / words +
                0.5 * words / sentences - 21.43;
    }

    public static double fk(String text) {
        int words = wordsCounter(text);
        int sentences = sentencesCounter(text);
        int syllables = syllablesCounter(text);

        return 0.39 * words / sentences + 11.8 * syllables / words - 15.59;
    }

    public static double smog(String text) {
        int polysyllables = polysyllablesCounter(text);
        int sentences = sentencesCounter(text);

        return 1.043 * Math.sqrt(polysyllables * 30 / sentences) + 3.1291;
    }

    public static double cl(String text) {
        int characters = charactersCounter(text);
        int words = wordsCounter(text);
        int sentences = sentencesCounter(text);

        return  0.0588 * (characters / words * 100) - 0.296 * (sentences / words * 100) - 15.8;
    }
}
