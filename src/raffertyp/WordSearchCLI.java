package raffertyp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
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
    public static void main(final String[] args) {
        try {
            final GameBoard gameBoard = new GameBoard(switch (args[3]) {
                case "ArrayList" -> new ArrayList<>(Files.readAllLines(Path.of(args[2])).size());
                case "LinkedList" -> new LinkedList<>();
                case "HashSet" -> HashSet.newHashSet(Files.readAllLines(Path.of(args[2])).size());
                case "TreeSet" -> new TreeSet<>();
                default -> throw new IllegalArgumentException(UNEXPECTED_VALUE + args[3]);
            });
            gameBoard.loadDictionary(Path.of(args[2]));
            gameBoard.loadGrid(Path.of(args[1]));
            System.out.println("Starting Search");
            final Instant start = Instant.now();
            final Set<String> words = gameBoard.findWords(switch (args[0]) {
                case "4way" -> true;
                case "8way" -> false;
                default -> throw new IllegalArgumentException(UNEXPECTED_VALUE + args[0]);
            });
            final Duration runTime = Duration.between(start, Instant.now());
            System.out.println("Words Found:");
            for (final String word : words) {
                System.out.println(word);
            }
            System.out.println("Total Words found: " + words.size());
            System.out.println(
                    "Run Time: " + runTime.toSeconds() + '.' + runTime.toNanosPart() + " seconds");
        } catch (final IOException e) {
            System.err.println("Error reading file: " + e);
        } catch (final IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}
