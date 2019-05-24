/**
 *  The class {@code Simplex} solves optimisation problems
 *  using simplex method.
 *
 *  @author Osaremhen Ukpebor
 */

public class Simplex {

    /**
     *  The {@code int} value can only be above 0
     */
    int constraintNumber;

    /**
     *  The {@code int} value can only be above 0
     */
    int variableNumber;

    int rowSize;
    int colSize;

    double[][] coefficients;

    /**
     *  The {@code string} can only have <i><</i>, <i>></i> or <i>=</i> as its value
     */
    String[] symbols;

    String[] columnTitles;
    String[] rowTitles;

    /**
     *  The {@code int} can only have <i>1</i> or <i>2</i>
     *  indicating <b>Maximisation</b> or <b>Minimisation</b>
     *  problems respectively.
     *  Any other value would cause an incorrect result
     */
    int optimisationType;

    /**
     * Creates a {@code Simplex} object with a specified number
     * of constraints, variables and type of optimisation to be carried out with
     * <b>1</b> indicating a maximisation problem and <b>2</b> indicating a mimisation
     * problem.
     *
     * @param constraintNumber should greater than 0
     * @param variableNumber should be greater than 0
     * @param optimisationType can only be 1 or 2
     */

    public Simplex (int constraintNumber, int variableNumber, int optimisationType) {
        this.constraintNumber = constraintNumber;
        this.variableNumber = variableNumber;
        this.rowSize = constraintNumber + 1;
        this.colSize = variableNumber + (constraintNumber * 2) + 1;
        this.coefficients = new double[rowSize][colSize];
        this.symbols = new String[constraintNumber];
        this.columnTitles = new String[variableNumber + (constraintNumber * 2)];
        this.rowTitles = new String[constraintNumber];
        this.optimisationType = optimisationType;
    }  //  end of constructor

    /**
     *  Sets slack, surplus and artificial variables.
     *  Also sets variable titles and negates coefficients
     *  of variable in the objective function
     */
    public void prepareSimpex() {
        //  set slack, surplus and artificial variables
        for (int i = 0; i < this.coefficients.length; i++) {
            if (i != 0) {
                if (this.symbols[i - 1].equals("<")) {  //  set slack variable
                    this.coefficients[i][this.variableNumber + i - 1] = 1;
                }
                else if (this.symbols[i - 1].equals(">")) {  //  set surplus and artificial variable
                    this.coefficients[i][this.variableNumber + i - 1] = -1;
                    this.coefficients[i][this.variableNumber + this.constraintNumber + i - 1] = 1;
                }
                else if (this.symbols[i - 1].equals("=")) {  //  artificial variable
                    this.coefficients[i][this.variableNumber + this.constraintNumber + i - 1] = 1;
                }
            }
        }  //  end of for

        //  negate objective function coefficients for maximisation problem
        if (this.optimisationType == 1) {
            for (int i = 0; i < this.coefficients[0].length; i++) {
                if (this.coefficients[0][i] != 0) {
                    this.coefficients[0][i] *= -1;
                }
            }
        }

        //  set variable titles
        this.columnTitles = new String[variableNumber + (constraintNumber * 2)];
        this.rowTitles = new String[constraintNumber];
        for (int i = 0; i < this.columnTitles.length; i++) {  //  set column titles
            if (i < variableNumber) {
                this.columnTitles[i] = "x" + (i + 1);
            }
            else {
                this.columnTitles[i] = "s" + (i - variableNumber + 1);
            }
        }  //  end of columnTitles for loop
        for (int i = 0; i < this.rowTitles.length; i++) {  //  set row titles
            if (this.symbols[i].contains("<")) {
                this.rowTitles[i] = "s" + (i + 1);
            }
            else {
                this.rowTitles[i] = "s" + (constraintNumber + i + 1);
            }
        }  //  end of rowTitles for loop

    }  //  end of prepareSimplex()

    /**
     *  Outputs tableu
     */
    public void printSimplex () {
        //  print column titles
        System.out.printf("%12s", "");
        for (int i = 0; i < columnTitles.length; i++) {
            System.out.printf("%10s", columnTitles[i]);
        }
        System.out.printf("%13s\n", "Solution");

        //  print P titles and coefficients
        System.out.printf("%12s:", "P");
        for (int i = 0; i < coefficients[0].length; i++) {
            System.out.printf("%10.2f", coefficients[0][i]);
        }
        System.out.println();

        // print other rows titles and coefficients
        for (int i = 0; i < rowTitles.length; i++) {
            System.out.printf("%12s:", rowTitles[i]);
            for (int j = 0; j < coefficients[i].length; j++) {
                System.out.printf("%10.2f", coefficients[i + 1][j]);
            }
            System.out.println();
        }

    }  //  end of printSimplex()

    /**
     *  Eliminates a negative coefficient in the objective function
     */
    public void solveSimplex () {
        double key = 0;
        int keyColumn = 0, keyRow = 0;
        double pivot = 1;

        //  find key column
        for (int i = 0; i < variableNumber + constraintNumber; i++) {
            if (coefficients[0][i] < key) {
                key = coefficients[0][i];
                keyColumn = i;
            }
        }

        //  find key row
        double ratio = Double.MAX_VALUE, ratioTest;
        for (int i = 1; i < constraintNumber + 1; i++) {
            ratioTest = coefficients[i][colSize - 1] / coefficients[i][keyColumn];
            if (ratioTest < ratio && ratioTest > 0) {
                ratio = ratioTest;
                pivot = coefficients[i][keyColumn];
                keyRow = i;
            }
        }
        rowTitles[keyRow - 1] = columnTitles[keyColumn]; //  change row title

        //  perform Gaussian reduction
        for (int i = 0; i < colSize; i++) {
            coefficients[keyRow][i] /= pivot;
        }
        for (int i = 0; i < constraintNumber + 1; i++) {
            final double rowPivot = coefficients[i][keyColumn];
            for (int j = 0; j < colSize; j++) {
                if (i != keyRow) {
                    coefficients[i][j] -= (rowPivot * coefficients[keyRow][j]);
                }
            }
        }
    }  //  end of solveSimplex()

    /**
     *  Tries to eliminate artificial variables
     *  that were not eliminated by the <code>solveSimplex</code> method
     */
    public void removeArtificialVariable() {
        int keyColumn = 0, keyRow = 0;
        double pivot = 1;

        //  find key column
        for (int i = 0; i < variableNumber + constraintNumber; i++) {
            if (coefficients[0][i] != 0) {
                keyColumn = i;
                break;
            }
        }

        //  find key row
        for (int i = 0; i < rowTitles.length; i++) {
            boolean keyRowFound = false;
            for (int j = constraintNumber + variableNumber; j < columnTitles.length; j++) {
                if (rowTitles[i].contains(columnTitles[j])) {
                    pivot = coefficients[i + 1][keyColumn];
                    keyRow = i + 1;
                    keyRowFound = true;
                    break;
                }
            }
            if (keyRowFound) {
                break;
            }
        }  //  end of for loop to check artificial variable


        rowTitles[keyRow - 1] = columnTitles[keyColumn]; //  change row title

        //  perform Gaussian reduction
        for (int i = 0; i < colSize; i++) {
            coefficients[keyRow][i] /= pivot;
        }
        for (int i = 0; i < constraintNumber + 1; i++) {
            final double rowPivot = coefficients[i][keyColumn];
            for (int j = 0; j < colSize; j++) {
                if (i != keyRow) {
                    coefficients[i][j] -= (rowPivot * coefficients[keyRow][j]);
                }
            }
        }
    }  //  end of removeArtificialVariable()

    /**
     *  Tries to get the optimum solution
     */
    public void optimise() {
        printSimplex();

        boolean foundOptimum = false;
        int iteration = 0;
        boolean foundArtificialVariable = false;

        while (!foundOptimum) {
            iteration++;
            if (!foundArtificialVariable) {
                solveSimplex();
            }
            else {
                removeArtificialVariable();
            }
            System.out.println();
            printSimplex();
            for (int i = 0; i < constraintNumber + variableNumber; i++) {  //  check if any coefficient in the index row is negative
                if (coefficients[0][i] < 0) {
                    foundOptimum = false;
                    break;
                }
                else {
                    foundOptimum = true;
                }  //  end of else
            }  //  end of for loop to check negative index coefficient

            //  check if artificial variable has been removed
            if (foundOptimum) {
                for (int j = 0; j < rowTitles.length; j++) {
                    for (int m = variableNumber + constraintNumber; m < columnTitles.length; m++) {
                        if (rowTitles[j].contains(columnTitles[m])) {
                            foundArtificialVariable = true;
                            foundOptimum = false;
                            break;
                        } else {
                            foundArtificialVariable = false;
                            foundOptimum = true;
                        }
                    }
                    if (foundArtificialVariable) {
                        break;
                    }
                }  //  end of for loop to check artificial variable
            }  //  end of if statement to check artificial variable rows

        }  //  end of while

        //  convert minimisation solution to maximisation solution
        if (optimisationType == 2) {
            coefficients[0][colSize - 1] *= -1;
        }

        System.out.printf("\n\nOptimum solution found after %d iteration(s)\n", iteration);
        System.out.printf("P = %.2f", coefficients[0][colSize - 1]);

    }

}  //  end of class
