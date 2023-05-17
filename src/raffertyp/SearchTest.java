/*
 * Course: CS2852
 * Spring 2022-2023
 * Lab 9 - Word Search
 * Name: Patrick Rafferty
 * Created: May 2023
 */
package raffertyp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Tests the search to make sure it is working.
 *
 * @author Patrick Rafferty
 */
public class SearchTest {
    /**
     * Path to the 2000 word dictionary.
     */
    private static final Path TWO_K_DICTIONARY = Path.of("data/2000words.txt");
    /**
     * Path to the word dictionary.
     */
    private static final Path WORDS_DICTIONARY = Path.of("data/words.txt");
    /**
     * Array of Collection types to test.
     */
    private static final String[] COLLECTION_TYPES =
            {"TreeSet", "ArrayList", "LinkedList", "LinkedHashSet"};
    
    /**
     * Helper method to set up a GameBoard and find its words.
     *
     * @param isFourWay      if the four way ruleset should be used
     * @param gridPath       the path to the grid file
     * @param collectionType the type of Collection to use
     * @param dictionaryPath the path to the dictionary
     * @return the Set of words found on the board.
     * @throws IOException if there was a problem reading a file
     */
    private static Set<String> doFindWords(final boolean isFourWay, final String gridPath,
                                           final String collectionType, final Path dictionaryPath)
            throws IOException {
        final GameBoard gameBoard = new GameBoard(switch (collectionType) {
            case "ArrayList" -> new ArrayList<>(Files.readAllLines(dictionaryPath).size());
            case "LinkedList" -> new LinkedList<>();
            case "HashSet" -> HashSet.newHashSet(Files.readAllLines(dictionaryPath).size());
            case "TreeSet" -> new TreeSet<>();
            case "LinkedHashSet" ->
                    LinkedHashSet.newLinkedHashSet(Files.readAllLines(dictionaryPath).size());
            default -> throw new IllegalArgumentException(collectionType);
        });
        gameBoard.loadGrid(Path.of(gridPath));
        gameBoard.loadDictionary(dictionaryPath);
        return gameBoard.findWords(isFourWay);
    }
    
    /**
     * Tests findWords to make sure it is working correctly.
     *
     * @throws IOException if there was a problem reading a file
     */
    @Test
    void findWords() throws IOException {
        for (final String collectionType : COLLECTION_TYPES) {
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/2000words4way2x2.txt"))),
                    doFindWords(true, "data/grid2x2.txt", collectionType, TWO_K_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/2000words4way2x3.txt"))),
                    doFindWords(true, "data/grid2x3.txt", collectionType, TWO_K_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/2000words4way3x3.txt"))),
                    doFindWords(true, "data/grid3x3.txt", collectionType, TWO_K_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/2000words4way4x4.txt"))),
                    doFindWords(true, "data/grid4x4.txt", collectionType, TWO_K_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/2000words4way6x6.txt"))),
                    doFindWords(true, "data/grid6x6.txt", collectionType, TWO_K_DICTIONARY),
                    "Failed searching " + collectionType);
            
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/2000words8way2x2.txt"))),
                    doFindWords(false, "data/grid2x2.txt", collectionType, TWO_K_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/2000words8way2x3.txt"))),
                    doFindWords(false, "data/grid2x3.txt", collectionType, TWO_K_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/2000words8way3x3.txt"))),
                    doFindWords(false, "data/grid3x3.txt", collectionType, TWO_K_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/2000words8way4x4.txt"))),
                    doFindWords(false, "data/grid4x4.txt", collectionType, TWO_K_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/2000words8way6x6.txt"))),
                    doFindWords(false, "data/grid6x6.txt", collectionType, TWO_K_DICTIONARY),
                    "Failed searching " + collectionType);
            
            
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/words4way2x2.txt"))),
                    doFindWords(true, "data/grid2x2.txt", collectionType, WORDS_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/words4way2x3.txt"))),
                    doFindWords(true, "data/grid2x3.txt", collectionType, WORDS_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/words4way3x3.txt"))),
                    doFindWords(true, "data/grid3x3.txt", collectionType, WORDS_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/words4way4x4.txt"))),
                    doFindWords(true, "data/grid4x4.txt", collectionType, WORDS_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/words4way6x6.txt"))),
                    doFindWords(true, "data/grid6x6.txt", collectionType, WORDS_DICTIONARY),
                    "Failed searching " + collectionType);
            
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/words8way2x2.txt"))),
                    doFindWords(false, "data/grid2x2.txt", collectionType, WORDS_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/words8way2x3.txt"))),
                    doFindWords(false, "data/grid2x3.txt", collectionType, WORDS_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/words8way3x3.txt"))),
                    doFindWords(false, "data/grid3x3.txt", collectionType, WORDS_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/words8way4x4.txt"))),
                    doFindWords(false, "data/grid4x4.txt", collectionType, WORDS_DICTIONARY),
                    "Failed searching " + collectionType);
            Assertions.assertEquals(
                    Set.copyOf(Files.readAllLines(Path.of("data/testOutput/words8way6x6.txt"))),
                    doFindWords(false, "data/grid6x6.txt", collectionType, WORDS_DICTIONARY),
                    "Failed searching " + collectionType);
        }
    }
}
