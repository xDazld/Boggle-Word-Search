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

public class LabExercise {
    private static final Path dictFile = Path.of("data/2000words.txt");
    
    private static GameBoard doLoadDictionary(final Collection<String> collection)
            throws IOException, NoSuchFieldException, IllegalAccessException {
        final GameBoard gameBoard = new GameBoard(collection);
        gameBoard.loadDictionary(dictFile);
        final Field dictField = gameBoard.getClass().getDeclaredField("dictionary");
        dictField.setAccessible(true);
        Assertions.assertTrue(Files.readAllLines(dictFile)
                        .containsAll((Collection<String>) dictField.get(gameBoard)),
                "Dictionary missing words.");
        System.out.println("Dictionary Loaded: " + collection);
        return gameBoard;
    }
    
    private static GameBoard doLoadGrid(final Path path) throws IOException {
        final GameBoard gameBoard = new GameBoard(new HashSet<>());
        gameBoard.loadGrid(path);
        final List<String> lines = Files.readAllLines(path);
        final char[][] expected = new char[lines.size()][];
        for (int row = 0; row < lines.size(); row++) {
            final char[] chars = lines.get(row).toCharArray();
            expected[row] = new char[chars.length];
            System.arraycopy(chars, 0, expected[row], 0, chars.length);
        }
        Assertions.assertEquals(Arrays.deepToString(expected), gameBoard.toString());
        System.out.println("Loaded Grid: " + gameBoard);
        return gameBoard;
    }
    
    @Test
    void loadDictionary() throws Exception {
        doLoadDictionary(new HashSet<>());
        doLoadDictionary(new ArrayList<>());
        doLoadDictionary(new LinkedList<>());
        doLoadDictionary(new TreeSet<>());
    }
    
    @Test
    void loadGrid() throws Exception {
        doLoadGrid(Path.of("data/grid2x2.txt"));
        doLoadGrid(Path.of("data/grid2x3.txt"));
        doLoadGrid(Path.of("data/grid3x3.txt"));
        doLoadGrid(Path.of("data/grid4x4.txt"));
        doLoadGrid(Path.of("data/grid6x6.txt"));
    }
    
    @Test
    void checkCellMethods() throws Exception {
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
                Assertions.assertEquals(grid[row][col], dupeGrid[row][col],
                        "Equal cells not equal");
            }
        }
    }
}
