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
import java.util.NavigableSet;
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
     * Length of the longest word in the dictionary.
     */
    private int longestWordLength;
    /**
     * How many total cells are in the grid.
     */
    private int gridArea;
    
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
        dictionary.parallelStream().mapToInt(String::length).max()
                .ifPresent(max -> longestWordLength = max);
        if (dictionary instanceof List<String>) {
            Collections.sort((List<String>) dictionary);
        }
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
        gridArea = grid.length * grid[0].length;
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
        if (partialWord.length() > longestWordLength || row >= grid.length || row < 0 ||
                col >= grid[row].length || col < 0) {
            return Collections.emptySet();
        }
        final Cell currentCell = grid[row][col];
        if (visited.contains(currentCell)) {
            return Collections.emptySet();
        }
        final Set<String> words = LinkedHashSet.newLinkedHashSet(dictionary.size());
        partialWord += currentCell.letter;
        if (partialWord.length() >= 3) {
            if (dictionary instanceof List<String>) {
                final int index = Collections.binarySearch((List<String>) dictionary, partialWord);
                if (index > -1) {
                    words.add(partialWord);
                } else {
                    if (-(index + 1) == dictionary.size() || index == -1 ||
                            (!((List<String>) dictionary).get(-(index + 1))
                                    .startsWith(partialWord) &&
                                    !((List<String>) dictionary).get(-(index + 1) - 1)
                                            .startsWith(partialWord))) {
                        return Collections.emptySet();
                    }
                }
            } else {
                if (dictionary.contains(partialWord)) {
                    words.add(partialWord);
                } else if (dictionary instanceof NavigableSet<String>) {
                    final String lower = ((NavigableSet<String>) dictionary).lower(partialWord);
                    final String higher = ((NavigableSet<String>) dictionary).higher(partialWord);
                    if (lower == null || higher == null ||
                            (!lower.startsWith(partialWord) && !higher.startsWith(partialWord))) {
                        return Collections.emptySet();
                    }
                } else {
                    final String finalPartialWord = partialWord;
                    if (dictionary.stream().noneMatch(word -> word.startsWith(finalPartialWord))) {
                        return Collections.emptySet();
                    }
                }
            }
        }
        final Set<Cell> tempSet = HashSet.newHashSet(gridArea);
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
        return words;
    }
    
    /**
     * Searches the boggle board for all valid words.
     *
     * @param isFourWay if only cardinal directions should be checked
     * @return A set of all the found words
     */
    public Set<String> findWords(final boolean isFourWay) {
        final Set<String> words =
                Collections.synchronizedSet(LinkedHashSet.newLinkedHashSet(dictionary.size()));
        Arrays.stream(grid).parallel().forEach(startRow -> Arrays.stream(startRow).parallel()
                .forEach(startCell -> words.addAll(recursiveSearch(startCell.row, startCell.col, "",
                        HashSet.newHashSet(gridArea), isFourWay))));
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