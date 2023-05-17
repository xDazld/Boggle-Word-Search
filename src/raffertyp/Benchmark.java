/*
 * Course: CS2852
 * Spring 2022-2023
 * Lab 9 - Word Search
 * Name: Patrick Rafferty
 * Created: May 2023
 */
package raffertyp;

import java.time.Duration;
import java.time.Instant;

/**
 * Utility to run all the benchmarks in a sequence.
 *
 * @author Patrick Rafferty
 */
public class Benchmark {
    /**
     * An array of the possible rule types.
     */
    private static final String[] RULES = {"4way", "8way"};
    /**
     * An array of the grid files.
     */
    private static final String[] GRIDS =
            {"data/grid2x2.txt", "data/grid2x3.txt", "data/grid3x3.txt", "data/grid4x4.txt",
                    "data/grid6x6.txt"};
    /**
     * An array of the dictionary files.
     */
    private static final String[] DICTIONARIES =
            {"data/2000words.txt", "data/words.txt", "data/scowl.txt"};
    /**
     * An array of the Collection types.
     */
    private static final String[] COLLECTION_TYPES =
            {"HashSet", "TreeSet", "ArrayList", "LinkedList", "LinkedHashSet"};
    
    /**
     * Runs the benchmark.
     *
     * @param args unused
     */
    public static void main(final String[] args) {
        final Instant start = Instant.now();
        for (final String rules : RULES) {
            for (final String grid : GRIDS) {
                for (final String dictionary : DICTIONARIES) {
                    for (final String collection : COLLECTION_TYPES) {
                        System.out.printf("%1s %2s %3s %4s\n", rules, grid, dictionary, collection);
                        WordSearchCLI.main(new String[] {rules, grid, dictionary, collection, "q"});
                        System.out.println();
                    }
                }
            }
        }
        final Duration runTime = Duration.between(start, Instant.now());
        System.out.println(
                "TOTAL RUN TIME: " + runTime.toMinutesPart() + ':' + runTime.toSecondsPart() + '.' +
                        runTime.toNanosPart());
    }
}
