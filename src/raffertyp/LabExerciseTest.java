package raffertyp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Test class to make sure everything for the lab exercise is working.
 *
 * @author Patrick Rafferty
 */
public class LabExerciseTest {
    /**
     * Path to the 2000 word dictionary.
     */
    private static final Path DICT_FILE = Path.of("data/2000words.txt");
    
    /**
     * Helper method to load a dictionary.
     *
     * @param collection The collection to use
     * @throws IOException                  if there was a problem reading
     * @throws ReflectiveOperationException if there was an issue with reflection
     */
    private static void doLoadDictionary(final Collection<String> collection)
            throws IOException, ReflectiveOperationException {
        final GameBoard gameBoard = new GameBoard(collection);
        gameBoard.loadDictionary(DICT_FILE);
        final Field dictField = gameBoard.getClass().getDeclaredField("dictionary");
        dictField.setAccessible(true);
        Assertions.assertTrue(Files.readAllLines(DICT_FILE)
                        .containsAll((Collection<String>) dictField.get(gameBoard)),
                "Dictionary missing words.");
        System.out.println("Dictionary Loaded: " + collection);
    }
    
    /**
     * Helper method to load a grid.
     *
     * @param path the path of the grid
     * @return the GameBoard that was loaded
     * @throws IOException if there was a problem reading the grid file
     */
    private static GameBoard doLoadGrid(final Path path) throws IOException {
        final GameBoard gameBoard = new GameBoard(new HashSet<>());
        gameBoard.loadGrid(path);
        final List<String> lines = Files.readAllLines(path);
        final char[][] expected = new char[lines.size()][];
        for (int row = 0; row < lines.size(); row++) {
            final char[] chars = lines.get(row).toLowerCase().toCharArray();
            expected[row] = new char[chars.length];
            System.arraycopy(chars, 0, expected[row], 0, chars.length);
        }
        Assertions.assertEquals(Arrays.deepToString(expected), gameBoard.toString());
        System.out.println("Loaded Grid: " + gameBoard);
        return gameBoard;
    }
    
    /**
     * Tests to make sure dictionary loading is working.
     *
     * @throws IOException                  if there was a problem reading the dictionary file
     * @throws ReflectiveOperationException if there was a problem with reflection
     */
    @Test
    void loadDictionary() throws Exception {
        doLoadDictionary(new HashSet<>());
        doLoadDictionary(new ArrayList<>());
        doLoadDictionary(new LinkedList<>());
        doLoadDictionary(new TreeSet<>());
    }
    
    /**
     * Tests to make sure grid loading is working.
     *
     * @throws IOException if there was a problem reading a grid file
     */
    @Test
    void loadGrid() throws IOException {
        doLoadGrid(Path.of("data/grid2x2.txt"));
        doLoadGrid(Path.of("data/grid2x3.txt"));
        doLoadGrid(Path.of("data/grid3x3.txt"));
        doLoadGrid(Path.of("data/grid4x4.txt"));
        doLoadGrid(Path.of("data/grid6x6.txt"));
    }
    
    /**
     * Makes sure the Cell methods are working as expected.
     *
     * @throws IOException                  if there was a problem reading a file
     * @throws ReflectiveOperationException if there was a problem with reflection
     */
    @Test
    void cellMethods() throws Exception {
        final GameBoard gameBoard = doLoadGrid(Path.of("data/grid6x6.txt"));
        final Field gridField = gameBoard.getClass().getDeclaredField("grid");
        gridField.setAccessible(true);
        final GameBoard.Cell[][] grid = (GameBoard.Cell[][]) gridField.get(gameBoard);
        for (final GameBoard.Cell[] row : grid) {
            for (final GameBoard.Cell cell : row) {
                Assertions.assertNotEquals(System.identityHashCode(cell), cell.hashCode(),
                        "Cell using default hashCode");
            }
        }
        
        final GameBoard dupeBoard = doLoadGrid(Path.of("data/grid6x6.txt"));
        final Field dupeGridField = dupeBoard.getClass().getDeclaredField("grid");
        dupeGridField.setAccessible(true);
        final GameBoard.Cell[][] dupeGrid = (GameBoard.Cell[][]) dupeGridField.get(gameBoard);
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                Assertions.assertEquals(grid[row][col], dupeGrid[row][col], "Equal cells not equal");
            }
        }
    }
}
