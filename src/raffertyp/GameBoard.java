package raffertyp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A game of Boggle.
 *
 * @author Patrick Rafferty
 */
public class GameBoard {
    /**
     * Dictionary holding this board's valid words.
     */
    private final Collection<String> dictionary;
    /**
     * The grid of cells on this board.
     */
    private Cell[][] grid;
    
    /**
     * Creates a new GameBoard using a provided collection as the dictionary.
     *
     * @param emptyDictionary the collection to use as the dictionary
     */
    public GameBoard(final Collection<String> emptyDictionary) {
        dictionary = emptyDictionary;
    }
    
    /**
     * Loads a list of words into the dictionary.
     *
     * @param path the path of the file with words
     * @throws IOException of there was a problem reading the dictionary
     */
    public void loadDictionary(final Path path) throws IOException {
        dictionary.addAll(Files.readAllLines(path));
    }
    
    /**
     * Loads a generated grid of Boggle from a file.
     *
     * @param path the path to load the grid from
     * @throws IOException if there was a problem reading the file
     */
    public void loadGrid(final Path path) throws IOException {
        final List<String> lines = Files.readAllLines(path);
        grid = new Cell[lines.size()][];
        for (int row = 0; row < lines.size(); row++) {
            final char[] chars = lines.get(row).toCharArray();
            grid[row] = new Cell[chars.length];
            for (int col = 0; col < chars.length; col++) {
                grid[row][col] = new Cell(row, col, Character.toLowerCase(chars[col]));
            }
        }
    }
    
    /**
     * Helper to recursively search the board for valid words.
     *
     * @param row         the current row
     * @param col         the current column
     * @param partialWord the partially constructed word
     * @param visited     a Set of Cells already visited
     * @param isFourWay   if we are ignoring diagonals or not
     * @return a set of words found in the grid
     */
    private Set<String> recursiveSearch(final int row, final int col, String partialWord,
                                        Set<Cell> visited, final boolean isFourWay) {
        //Check if oob or already seen, return empty set if we are
        if (row >= grid.length || row < 0 || col >= grid[row].length || col < 0) {
            return Collections.emptySet();
        }
        final Cell currentCell = grid[row][col];
        if (visited.contains(currentCell)) {
            return Collections.emptySet();
        }
        //Visit other directions, add sets from those
        final Set<String> words = LinkedHashSet.newLinkedHashSet(dictionary.size());
        partialWord += currentCell.letter;
        final Set<Cell> tempSet = HashSet.newHashSet(grid.length * grid[row].length);
        tempSet.addAll(visited);
        visited = tempSet;
        visited.add(currentCell);
        if (isFourWay) {
            for (int rrow = -1; rrow < 2; rrow += 2) {
                words.addAll(recursiveSearch(rrow + row, col, partialWord, visited, true));
            }
            for (int rcol = -1; rcol < 2; rcol += 2) {
                words.addAll(recursiveSearch(row, rcol + col, partialWord, visited, true));
            }
        } else {
            for (int rrow = -1; rrow < 2; rrow++) {
                for (int rcol = -1; rcol < 2; rcol++) {
                    words.addAll(
                            recursiveSearch(rrow + row, rcol + col, partialWord, visited, false));
                }
            }
        }
        if (partialWord.length() >= 3 && dictionary.contains(partialWord)) {
            words.add(partialWord);
        }
        return words;
    }
    
    /**
     * Searches the boggle board for all valid words.
     *
     * @param isFourWay if only cardinal directions should be checked
     * @return A set of all the found words
     */
    public Set<String> findWords(final boolean isFourWay) {
        final Set<String> words = LinkedHashSet.newLinkedHashSet(dictionary.size());
        final int rowLength = grid[0].length;
        final int gridArea = grid.length * rowLength;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                final String status =
                        String.valueOf(((double) ((row * rowLength) + col) / gridArea) * 100) + '%';
                System.out.print(status);
                words.addAll(
                        recursiveSearch(row, col, "", HashSet.newHashSet(gridArea), isFourWay));
                System.out.print("\b".repeat(status.length()));
            }
        }
        return words;
    }
    
    @Override
    public String toString() {
        return Arrays.deepToString(grid);
    }
    
    /**
     * A cell to go inside the Boggle grid.
     *
     * @param row    the row this cell is in
     * @param col    the column this cell is in
     * @param letter the letter this cell holds
     */
    record Cell(int row, int col, char letter) {
        // Record implements hashCode and equals
        // Record also deals with making getters
        
        @Override
        public String toString() {
            return String.valueOf(letter);
        }
    }
}