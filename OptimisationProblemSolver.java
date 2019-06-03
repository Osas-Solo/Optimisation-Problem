import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class OptimisationProblemSolver extends Application {

    Stage window;
    Scene scene;
    BorderPane windowContent;
    ScrollPane scroller;

    HBox northContent;
    VBox[] northComponents;
    Label[] prompts;
    TextField constraintNumberInput;
    TextField variableNumberInput;
    ChoiceBox<String> optimisationSelector;

    VBox centreContent;
    Button proceedButton;
    GridPane centreComponents;
    Label[] variableTitles;
    Label objectiveFunctionTitle;
    TextField[] objectiveFunctionInputs;
    Label[] constraintTitles;
    TextField[][] constraintInputs;
    ChoiceBox<String>[] symbolSelectors;
    Button optimiseButton;
    Button resetButton;

    TextArea resultDisplay;

    Alert error;

    int constraintNumber;
    int variableNumber;
    int optimisationType;
    Simplex simplex;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //  initialise window
        window = primaryStage;
        windowContent = new BorderPane();
        scroller = new ScrollPane(windowContent);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scroller.setFitToHeight(true);
        scroller.setFitToWidth(true);

        //  north content
        northContent = new HBox(20);
        northComponents = new VBox[3];
        prompts = new Label[3];
        for (int i = 0; i < 3; i++) {
            northComponents[i] = new VBox(5);

            if (i == 0) {
                prompts[i] = new Label("Enter number of constraints:");
            }
            else if (i == 1) {
                prompts[i] = new Label("Enter number of variables:");
            }
            else if (i == 2) {
                prompts[i] = new Label("Select type of optimisation:");
            }

            northComponents[i].getChildren().add(prompts[i]);
            northComponents[i].setAlignment(Pos.CENTER);
        }  //  end of for
        constraintNumberInput = new TextField();
        northComponents[0].getChildren().add(constraintNumberInput);
        variableNumberInput = new TextField();
        northComponents[1].getChildren().add(variableNumberInput);
        String[] options = {"Maximisation", "Minimisation"};
        optimisationSelector = new ChoiceBox<>();
        optimisationSelector.getItems().addAll(options);
        optimisationSelector.setValue("Maximisation");
        northComponents[2].getChildren().add(optimisationSelector);
        northContent.getChildren().addAll(northComponents);
        northContent.setAlignment(Pos.CENTER);
        windowContent.setTop(northContent);

        //  centre content
        centreContent = new VBox(5);
        proceedButton = new Button("Proceed");
        centreComponents = new GridPane();
        centreComponents.setAlignment(Pos.CENTER);
        centreComponents.setPadding(new Insets(5, 5, 5, 5));
        centreComponents.setGridLinesVisible(true);
        optimiseButton = new Button("Optimise");
        optimiseButton.setVisible(false);
        resetButton = new Button("Reset");
        resetButton.setVisible(false);
        centreContent.getChildren().addAll(proceedButton, centreComponents, optimiseButton, resetButton);
        centreContent.setAlignment(Pos.CENTER);
        windowContent.setCenter(centreContent);

        //  bottom content
        resultDisplay = new TextArea();
        resultDisplay.setEditable(false);
        windowContent.setBottom(resultDisplay);

        //  set scene
        scene = new Scene(scroller);
        window.setScene(scene);
        window.setTitle("Optimisation Problem Solver");
        window.setMaximized(true);
        window.show();

        //  error
        error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Input Error");
        error.setHeaderText("");

        //  set style
        scene.getStylesheets().add("Style.css");

        //  set actions
        proceedButton.setOnAction(e -> {
            //  create simplex object
            try {
                constraintNumber = Integer.parseInt(constraintNumberInput.getText());
                variableNumber = Integer.parseInt(variableNumberInput.getText());
                if (optimisationSelector.getValue().equals("Maximisation")) {
                    optimisationType = 1;
                }
                else if (optimisationSelector.getValue().equals("Minimisation")) {
                    optimisationType = 2;
                }


                //  check if constraint and variable numbers are valid
                if (constraintNumber > 0 && variableNumber > 0) {

                    simplex = new Simplex(constraintNumber, variableNumber, optimisationType);

                    //  prepare variable inputs
                    variableTitles = new Label[variableNumber + 1];
                    objectiveFunctionTitle = new Label("Enter objective function:");
                    GridPane.setConstraints(objectiveFunctionTitle, 0, 1);
                    centreComponents.getChildren().add(objectiveFunctionTitle);
                    objectiveFunctionInputs = new TextField[variableNumber];
                    constraintTitles = new Label[constraintNumber];
                    constraintInputs = new TextField[constraintNumber][variableNumber + 1];
                    symbolSelectors = new ChoiceBox[constraintNumber];

                    for (int i = 0; i < variableTitles.length; i++) {
                        if (i == variableTitles.length - 1) {
                            variableTitles[i] = new Label("Solution:");
                            GridPane.setConstraints(variableTitles[i], variableNumber + 2, 0);
                            centreComponents.getChildren().add(variableTitles[i]);
                        }
                        else {
                            variableTitles[i] = new Label("x" + (i + 1) + ":");
                            GridPane.setConstraints(variableTitles[i], i + 1, 0);
                            centreComponents.getChildren().add(variableTitles[i]);
                        }
                    }  //  end of variableTitles for loop

                    for (int i = 0; i < objectiveFunctionInputs.length; i++) {
                        objectiveFunctionInputs[i] = new TextField();
                        GridPane.setConstraints(objectiveFunctionInputs[i], i + 1, 1);
                        centreComponents.getChildren().add(objectiveFunctionInputs[i]);
                    }  //  end of objectiveFunctionInputs for loop

                    for (int i = 0; i < constraintNumber; i++) {
                        constraintTitles[i] = new Label("Constraint " + (i + 1) + ":");
                        GridPane.setConstraints(constraintTitles[i], 0, i + 2);
                        centreComponents.getChildren().add(constraintTitles[i]);
                        String[] symbols = {"<", ">", "="};
                        symbolSelectors[i] = new ChoiceBox<>();
                        symbolSelectors[i].getItems().addAll(symbols);
                        symbolSelectors[i].setValue("<");
                        GridPane.setConstraints(symbolSelectors[i], variableNumber + 1, i + 2);
                        centreComponents.getChildren().add(symbolSelectors[i]);

                        for (int j = 0; j < constraintInputs[i].length; j++) {
                            constraintInputs[i][j] = new TextField();
                            if (j == constraintInputs[i].length - 1) {
                                GridPane.setConstraints(constraintInputs[i][j], variableNumber + 2, i + 2);
                                centreComponents.getChildren().add(constraintInputs[i][j]);
                            }
                            else {
                                GridPane.setConstraints(constraintInputs[i][j], j + 1, i + 2);
                                centreComponents.getChildren().add(constraintInputs[i][j]);
                            }
                        }  //  end of inner for
                    }  //  end of constraintInputs for loop


                    centreComponents.setVisible(true);

                    // display other buttons
                    optimiseButton.setVisible(true);
                    resetButton.setVisible(true);
                    constraintNumberInput.setDisable(true);
                    variableNumberInput.setDisable(true);
                    optimisationSelector.setDisable(true);
                    proceedButton.setDisable(true);

                }  //  end of if to check constraint and variable number

                else {
                    error.setContentText("Please enter valid integers (> 0)");
                    error.showAndWait();
                }

            } catch (NumberFormatException e1) {
                error.setContentText("Please enter valid integers (> 0)");
                error.showAndWait();
            }
        });

        optimiseButton.setOnAction(e -> {
            resultDisplay.setText("");
            try {
                getCoefficients();
                simplex.prepareSimpex();
                simplex.optimise(resultDisplay);
            } catch (NumberFormatException e1) {
                error.setContentText("Please enter valid numbers in every textfield");
                error.showAndWait();
            }
        });

        resetButton.setOnAction(e -> {
            constraintNumberInput.setText("");
            variableNumberInput.setText("");
            optimisationSelector.setValue("Maximisation");
            constraintNumberInput.setDisable(false);
            variableNumberInput.setDisable(false);
            optimisationSelector.setDisable(false);
            proceedButton.setDisable(false);
            for (int i = 0; i < variableTitles.length; i++) {
                if (i == variableTitles.length - 1) {
                    centreComponents.getChildren().remove(variableTitles[i]);
                }
                else {
                    centreComponents.getChildren().remove(variableTitles[i]);
                }
            }  //  end of variableTitles for loop

            for (int i = 0; i < objectiveFunctionInputs.length; i++) {
                centreComponents.getChildren().remove(objectiveFunctionInputs[i]);
            }  //  end of objectiveFunctionInputs for loop

            for (int i = 0; i < constraintNumber; i++) {
                centreComponents.getChildren().remove(constraintTitles[i]);
                centreComponents.getChildren().remove(symbolSelectors[i]);

                for (int j = 0; j < constraintInputs[i].length; j++) {
                    if (j == constraintInputs[i].length - 1) {
                        centreComponents.getChildren().remove(constraintInputs[i][j]);
                    }
                    else {
                        centreComponents.getChildren().remove(constraintInputs[i][j]);
                    }
                }  //  end of inner for
            }  //  end of constraintInputs for loop
            centreComponents.setVisible(false);
            optimiseButton.setVisible(false);
            resetButton.setVisible(false);
            resultDisplay.setText("");
        });



    }  //  end of start()

    private void getCoefficients() {
        //  get coefficients
        for (int i = 0; i < simplex.coefficients.length; i++) {

            for (int j = 0; j < simplex.coefficients[i].length; j++) {
                if (j != simplex.coefficients[i].length - 1 && j < variableNumber) {
                    //  get coeffiecients of objective function variables
                    if (i == 0) {
                        simplex.coefficients[i][j] = Double.parseDouble(objectiveFunctionInputs[j].getText());
                    }
                    //  get coefficient of variable
                    else {
                        simplex.coefficients[i][j] = Double.parseDouble(constraintInputs[i - 1][j].getText());
                    }
                }
                else if (j == simplex.coefficients[i].length - 1 && i != 0) {
                    //  input </>/=
                    simplex.symbols[i - 1] = symbolSelectors[i - 1].getValue();
                    //  input solution
                    simplex.coefficients[i][j] = Double.parseDouble(constraintInputs[i - 1][variableNumber].getText());
                }  //  end of else if
            }  //  end of inner for
        }  //  end of outer for

    }  //  end of getCoefficients()

}  //  end of class
