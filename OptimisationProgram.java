import java.util.Scanner;

public class OptimisationProgram {

    public static void main (String[] args) {

        Scanner scan = new Scanner(System.in);
        int constraintNumber = 0;
        int variableNumber = 0;
        boolean foundException;
        String[] optimisation = {"Maximisation", "Minimisation"};
        int option = 0;

        //  input option
        foundException = true;
        while (foundException) {
            try {
                option = inputInteger("optimisation problem:\n" +
                        "1. Maximisation\n" +
                        "2. Minimisation");
                if (option == 1 || option == 2) {
                    foundException = false;
                }
            } catch (Exception e) {
                printErrorMessage("integer");
            }
        }

        //  input constraint number
        foundException = true;
        while (foundException) {
            try {
                constraintNumber = inputInteger("number of constraints:");
                if (constraintNumber > 0) {
                    foundException = false;
                }
            } catch (Exception e) {
                printErrorMessage("integer");
            }
        }

        //  input variable number
        foundException = true;
        while (foundException) {
            try {
                variableNumber = inputInteger("number of variables:");
                if (variableNumber > 0) {
                    foundException = false;
                }
            } catch (Exception e) {
                printErrorMessage("integer");
            }
        }

        Simplex simplex = new Simplex(constraintNumber, variableNumber, option);

        System.out.println("Enter a " + optimisation[option - 1] + " problem in the form:");
        System.out.printf("%10s%10s%10s%10s%10s\n", "", "x1", "x2", "</>/=", "Solution");
        System.out.printf("\n%10s%10s%10s%10s%10s\n", "P = ", "a", "b", "<", "c");
        System.out.printf("\n%10s%10s%10s%10s%10s\n", "Constraint 1:", "a1", "b1", "<", "c1");
        System.out.printf("\n%10s%10s%10s%10s%10s\n", "Constraint 2:", "a2", "b2", "<", "c2");
        System.out.printf("\n%10s%10s%10s%10s%10s\n\n", "Constraint n:", "an", "bn", "<", "cn");

        //  initialise coefficients
        //  the elements in each row are the coefficients of the basic, slack and surplus variables
        for (int i = 0; i < simplex.coefficients.length; i++) {
            for (int j = 0; j < simplex.coefficients[i].length; j++) {
                simplex.coefficients[i][j] = 0;
            }
        }

        //  input coefficients
        for (int i = 0; i < simplex.coefficients.length; i++) {
            if (i == 0) {
                System.out.println("\nEnter coefficients of the variables in P:");
            }
            else {
                System.out.println("\nEnter coefficients for constraint " + i + ": ");
            }

            for (int j = 0; j < simplex.coefficients[i].length; j++) {
                if (j != simplex.coefficients[i].length - 1 && j < variableNumber) {
                    //  input coefficient
                    foundException = true;
                    while (foundException) {
                        try {
                            simplex.coefficients[i][j] = inputDouble("coefficient for x" + (j + 1) + ": ");
                            foundException = false;
                        } catch (Exception e) {
                            printErrorMessage("number");
                        }
                    }
                }
                else if (j == simplex.coefficients[i].length - 1 && i != 0) {
                    //  input </>/=
                    foundException = true;
                    while (foundException) {
                        System.out.println("Enter inequality/equation symbol: ");
                        simplex.symbols[i - 1] = scan.next();

                        if (simplex.symbols[i - 1].equals("<") || simplex.symbols[i - 1].equals(">") || simplex.symbols[i - 1].equals("=")) {
                            foundException = false;
                        }
                    }

                    //  input solution
                    foundException = true;
                    while (foundException) {
                        try {
                            simplex.coefficients[i][j] = inputDouble("solution:");
                            foundException = false;
                        } catch (Exception e) {
                            printErrorMessage("number");
                        }
                    }  //  end of while
                }  //  end of else if
            }  //  end of inner for
        }  //  end of outer for

        simplex.prepareSimpex();

        simplex.optimise();

    }  //  end of main



    public static int inputInteger(String message) {
        System.out.println("Enter " + message);
        Scanner scan = new Scanner(System.in);
        int x = scan.nextInt();
        return x;
    }  //  end of inputValue()

    public static double inputDouble(String message) {
        System.out.println("Enter " + message);
        Scanner scan = new Scanner(System.in);
        double x = scan.nextInt();
        return x;
    }  //  end of inputValue()


    public static void printErrorMessage(String inputType) {
        System.out.println("Please enter a valid " + inputType);
    }  //  end of printErrorMessage()

}  //  end of class
