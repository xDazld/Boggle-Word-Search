/*
 * Course: CS2852
 * Spring 2022-2023
 * Lab 9 - Word Search
 * Name: Patrick Rafferty
 * Created: May 2023
 */
package raffertyp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Program to search a Boggle board for possible words and benchmark a Collection.
 *
 * @author Patrick Rafferty
 */
public class WordSearchCLI {
    /**
     * Printed if an unexpected value was found.
     */
    private static final String UNEXPECTED_VALUE = "Unexpected value: ";
    
    /**
     * Entry point for program.
     *
     * @param args Ruleset, grid file, word list, Collection type
     */
    public static void main(String[] args) {
        try {
            GameBoard gameBoard = new GameBoard(switch (args[3]) {
                case "ArrayList" -> new ArrayList<>(Files.readAllLines(Path.of(args[2])).size());
                case "LinkedList" -> new LinkedList<>();
                case "HashSet" -> HashSet.newHashSet(Files.readAllLines(Path.of(args[2])).size());
                case "TreeSet" -> new TreeSet<>();
                case "LinkedHashSet" ->
                        LinkedHashSet.newLinkedHashSet(Files.readAllLines(Path.of(args[2])).size());
                default -> throw new IllegalArgumentException(UNEXPECTED_VALUE + args[3]);
            });
            gameBoard.loadDictionary(Path.of(args[2]));
            gameBoard.loadGrid(Path.of(args[1]));
            boolean isFourWay = switch (args[0]) {
                case "4way" -> true;
                case "8way" -> false;
                default -> throw new IllegalArgumentException(UNEXPECTED_VALUE + args[0]);
            };
            Instant start = Instant.now();
            Set<String> words = gameBoard.findWords(isFourWay);
            Duration runTime = Duration.between(start, Instant.now());
            if (!(args.length >= 5 && args[4].equals("q"))) {
                System.out.println("Words Found:");
                for (String word : words) {
                    System.out.println(word);
                }
                System.out.println("Total Words found: " + words.size());
            }
            System.out.println(
                    "Run Time: " + runTime.toSeconds() + '.' + runTime.toNanosPart() + " seconds");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}
