package raffertyp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.random.RandomGenerator;

/**
 * The GUIController class is responsible for controlling the graphical user interface of the word
 * search application. It handles the user's interactions with the GUI elements and performs the
 * necessary actions to generate a word search grid, load a grid file, search for words in the grid,
 * and display the results.
 *
 * @author Patrick Rafferty
 */
public class GUIController {
    /**
     * A constant variable for a file chooser extension filter for text files.
     *
     * <p>
     * The filter allows only files with the ".txt" extension to be selected in a file chooser
     * dialog.
     * </p>
     */
    private static final FileChooser.ExtensionFilter TXT_FILTER =
            new FileChooser.ExtensionFilter("Grid file", "*.txt");
    /**
     * The random variable is an instance of the RandomGenerator class used for generating random
     * numbers.
     */
    private final RandomGenerator random = new Random();
    /**
     * Displays a visual representation of the GameBoard.
     */
    @FXML
    private GridPane gridPane;
    /**
     * Displays the list of found words.
     */
    @FXML
    private VBox wordList;
    /**
     * Input for how many columns a generated board should have.
     */
    @FXML
    private TextField widthInput;
    /**
     * Input for how many rows a generated board should have.
     */
    @FXML
    private TextField heightInput;
    /**
     * Used for the two different rulesets.
     */
    @FXML
    private ToggleGroup rules;
    /**
     * Represents a radio button for four-way ruleset.
     */
    @FXML
    private RadioButton fourway;
    /**
     * File of the current board being used.
     */
    private File file;
    /**
     * Used for selecting a board to load.
     */
    private FileChooser fileChooser;
    /**
     * The main stage for this application.
     */
    @FXML
    private Stage stage;
    /**
     * The GameBoard runs the logic for loading and searching.
     */
    private GameBoard gameBoard;
    /**
     * Used to show the search time.
     */
    @FXML
    private Label time;
    /**
     * Used to show the word count.
     */
    @FXML
    private Label wordCount;
    
    /**
     * Initializes the game by setting up the file chooser, loading the dictionary file, and
     * creating a new game board.
     *
     * @throws IOException if there was a problem reading the dictionary.
     */
    public void initialize() throws IOException {
        fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(TXT_FILTER);
        fileChooser.setTitle("Load Grid File");
        Path dictionaryFilePath = Path.of("data/words.txt");
        gameBoard = new GameBoard(new ArrayList<>(Files.readAllLines(dictionaryFilePath).size()));
        gameBoard.loadDictionary(dictionaryFilePath);
    }
    
    /**
     * Generates a new grid for the game based on the specified width and height. The grid is saved
     * to a temporary file and then loaded into the game.
     *
     * @throws NumberFormatException if the width or height is not a positive integer.
     */
    @FXML
    private void generate() {
        try {
            int width = Integer.parseInt(widthInput.getText());
            int height = Integer.parseInt(heightInput.getText());
            if (width <= 0 || height <= 0) {
                throw new NumberFormatException("Input was negative");
            }
            file = Files.createTempFile("grid", ".txt").toFile();
            file.deleteOnExit();
            try (PrintWriter out = new PrintWriter(file, StandardCharsets.UTF_8)) {
                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        out.print((char) random.nextInt('A', 'Z' + 1));
                    }
                    out.println();
                }
            }
            loadFile();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Width and height must be positive integers").show();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error making temporary file").show();
        }
    }
    
    /**
     * Loads a previously generated grid from a file into the game board. The grid is displayed on
     * the UI as a grid of labels.
     */
    private void loadFile() {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            gridPane.getChildren().clear();
            int rows = lines.size();
            for (int row = 0; row < rows; row++) {
                gridPane.addRow(row);
            }
            int cols = lines.get(0).length();
            for (int col = 0; col < cols; col++) {
                gridPane.addColumn(col);
            }
            try (Scanner in = new Scanner(file, StandardCharsets.UTF_8)) {
                for (int row = 0; row < rows; row++) {
                    char[] line = in.nextLine().toCharArray();
                    for (int col = 0; col < cols; col++) {
                        gridPane.add(new Label(String.valueOf(line[col])), col, row);
                    }
                }
            }
            gameBoard.loadGrid(file.toPath());
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Problem reading file.").show();
        }
    }
    
    /**
     * Performs a search for words on the game board based on the selected search rules. Results are
     * displayed on the UI in a list of labels.
     */
    @FXML
    private void search() {
        wordList.getChildren().clear();
        Instant start = Instant.now();
        Set<String> words = gameBoard.findWords(rules.getSelectedToggle().equals(fourway));
        Duration runTime = Duration.between(start, Instant.now());
        time.setText("Time: " + runTime.toSeconds() + '.' + runTime.toNanosPart() + " seconds");
        wordCount.setText("Words: " + words.size());
        for (String word : words) {
            wordList.getChildren().add(new Label(word));
        }
    }
    
    /**
     * Opens a file chooser dialog for the user to select a file to load. If a file is selected, the
     * method calls the loadFile() method to load the selected file.
     */
    @FXML
    private void pickFile() {
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            loadFile();
        }
    }
}
