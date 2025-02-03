import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.sql.SQLOutput;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Solver implements SolverInterface {
    private double[][][] xSolution;
    private double[][] ySolution;
    private Model model;
    private IloCplex cplex;
    String[][] truckSolution;
    IloNumVar[][][] x;
    IloNumVar[][] y;
    IloNumVar[][][] z;
    IloNumVar[][][] w;
    IloNumVar[][] b;
    List<IloRange> constraintsList;
    double[][][] greedyX;
    SolverInterface greedySolver;


    public Solver() throws IloException {
        this.model = new Model();
        this.cplex = new IloCplex();
        this.xSolution = new double[model.getTrucksNumber()][model.getTanksNumber()][model.getMaximumAvailableTrips()];
        this.ySolution = new double[model.getTrucksNumber()][model.getMaximumAvailableTrips()];
        this.truckSolution = new String[model.getTrucksNumber()][model.getTotalOperationTime()];
        this.x = new IloNumVar[model.getTrucksNumber()][model.getTanksNumber()][model.getMaximumAvailableTrips()];
        this.y = new IloNumVar[model.getTrucksNumber()][model.getMaximumAvailableTrips()];
        this.z = new IloNumVar[model.getTrucksNumber()][model.getMaximumAvailableTrips()][model.getTotalOperationTime()];
        this.w = new IloNumVar[model.getTrucksNumber()][model.getMaximumAvailableTrips()][model.getTotalOperationTime()];
        this.b = new IloNumVar[model.getTrucksNumber()][model.getMaximumAvailableTrips()];
        this.greedySolver = new GreedyAlgorithmSolverWithIterations();
        this.constraintsList = new ArrayList<>();
        this.greedyX = new double[model.getTrucksNumber()][model.getTanksNumber()][model.getMaximumAvailableTrips()];
    }

    public double[][][] getxSolution() {
        return xSolution;
    }

    public void setxSolution(double[][][] xSolution) {
        this.xSolution = xSolution;
    }

    public double[][] getySolution() {
        return ySolution;
    }

    @Override
    public void solveModel(int noOfIterations) {

    }

    public void setySolution(double[][] ySolution) {
        this.ySolution = ySolution;
    }

    @Override
    public void solveModel() {

        try {
            //Create the CPLEX model

            cplex.setParam(IloCplex.Param.TimeLimit, 7200.0); //Set time limit 1 hours


//            activateGreedyAlgorithmSolver(10);

            //running solver
            initializeVariables();
            activateConstraintBasicPackage();
            activateObjectiveFunction();








//end of greedy solver implementation

//
//            for (int n = 0; n < model.getTrucksNumber(); n++) {
//                for (int i = 0; i < model.getTanksNumber(); i++) {
//                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                        greedyX[n][i][k] = 0;
//                    }
//                }
//            }
//
//            for (int n = 0; n < model.getTrucksNumber(); n++) {
//                for (int i = 0; i < model.getTanksNumber(); i++) {
//                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                        greedyX[n][i][k] = greedySolver.getxSolution()[n][i][k];
//                    }
//                }
//            }


            //!!!Declare the Constraints!!!


//            //Constraint #2
//            for (int n = 0; n < model.getTrucksNumber(); n++) {
//                IloLinearNumExpr constraintExpr = cplex.linearNumExpr();
//                for (int i = 0; i < model.getTanksNumber(); i++) {
//                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                        //For each truck
//                        constraintExpr.addTerm(2 * model.getTruckLoadingTime()[n] + 2 * model.getTruckDeliveryTimeToDepot()[i], x[n][i][k]);
//                    }
//                }
//                cplex.addLe(constraintExpr, model.getTotalOperationTime());
//            }//constraint #2


//                    for (int i = 0; i < model.getTanksNumber(); i++) {
//                        for (int t = 0; t < model.getTotalOperationTime(); t++) {
//                            IloLinearNumExpr constraintExpr = cplex.linearNumExpr();
//                            for (int n = 0; n < model.getTrucksNumber(); n++) {
//                                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                            constraintExpr.addTerm(1, r[i][n][k][t]);
//                        }
//                    }
//                            cplex.addLe(constraintExpr, 1);
//                }
//            } //Constraint #12


//            for (int i = 0; i < model.getTanksNumber(); i++) {
//                for (int t = 0; t < model.getTotalOperationTime(); t++) {
//                    for (int n = 0; n < model.getTrucksNumber(); n++) {
//                        for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                            IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
//                            IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
//                            constraintExpr1.addTerm(1, z[n][k][t]);
//                            int o = t + model.getTruckLoadingTime()[n] + model.getTruckDeliveryTimeToDepot()[i];
//                            if (o < model.getTotalOperationTime()) {
//                                constraintExpr2.addTerm(1, r[i][n][k][o]);
//                            }
//                            cplex.addLe(constraintExpr1, constraintExpr2);
//                        }
//                    }
//
//                }
//            } //Constraint #13


//
//            for (int i = 0; i < model.getTanksNumber(); i++) {
//                for (int t = 0; t < model.getTotalOperationTime(); t++) {
//                    for (int n = 0; n < model.getTrucksNumber(); n++) {
//                        for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                            IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
//                            IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
//                            constraintExpr1.addTerm(1, w[n][k][t]);
//                            int o = t + model.getTruckLoadingTime()[n] + model.getTruckDeliveryTimeToDepot()[i];
//                            if (o < model.getTotalOperationTime()) {
//                                constraintExpr2.addTerm(1, r[i][n][k][o]);
//                            }
//                            cplex.addLe(constraintExpr1, constraintExpr2);
//                        }
//                    }
//                }
//            } //Constraint #14


//            //Constraint #3a
//            for (int i = 0; i < model.getTanksNumber(); i++) {
//                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                    IloLinearNumExpr constraintExpr = cplex.linearNumExpr();
//                for (int n = 0; n < model.getTrucksNumber(); n++) {
//                        constraintExpr.addTerm(1, x[n][i][k]);
//                    }
//                    cplex.addLe(constraintExpr, 1);
//                }
//            } //Constraint #3a

//            //Constraint #4
//            for (int i = 0; i < model.getTanksNumber(); i++) {
//                IloLinearNumExpr constraintExpr = cplex.linearNumExpr();
//                for (int n = 0; n < model.getTrucksNumber(); n++) {
//                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                        constraintExpr.addTerm(2 * model.getTruckLoadingTime()[n] + 2 * model.getTruckDeliveryTimeToDepot()[i], x[n][i][k]);
//                    }
//                }
//                cplex.addLe(constraintExpr, model.getTotalOperationTime());
//            }//constraint #4

//
////            Constraint #4b
//            for (int i = 0; i < model.getTanksNumber(); i++) {
//                for (int n = 0; n < model.getTrucksNumber(); n++) {
//                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                        IloLinearNumExpr constraintExpr = cplex.linearNumExpr();
//                        constraintExpr.addTerm(1, y[n][k]);
//                        double rhs = model.getTotalOperationTime() - model.getTruckDeliveryTimeToDepot()[i];
//                        cplex.addLe(constraintExpr, rhs);
//                    }
//                }
//            }//constraint #4b


//
//
//
//            // Initialize MIP Start Variables
//            IloNumVar[] startX = new IloNumVar[(model.getTrucksNumber() * model.getTanksNumber() * model.getMaximumAvailableTrips()) + (model.getTrucksNumber() * model.getMaximumAvailableTrips())
//                    + (model.getTrucksNumber() * model.getMaximumAvailableTrips() * model.getTotalOperationTime())
//                    + (model.getTrucksNumber() * model.getMaximumAvailableTrips() * model.getTotalOperationTime())];
//            double[] startXValues = new double[model.getTrucksNumber() * model.getTanksNumber() * model.getMaximumAvailableTrips() + (model.getTrucksNumber() * model.getMaximumAvailableTrips())
//                    + (model.getTrucksNumber() * model.getMaximumAvailableTrips() * model.getTotalOperationTime())
//                    + (model.getTrucksNumber() * model.getMaximumAvailableTrips() * model.getTotalOperationTime())];
//
////
//
//
//            //Transform the x, y, z, w values in a solver format
//
//            //transform x variable
//            //initialize the required variables
//            double[][][] xVariable = new double[model.getTrucksNumber()][model.getTanksNumber()][model.getMaximumAvailableTrips()];
//            double[][] yVariable = new double[model.getTrucksNumber()][model.getMaximumAvailableTrips()];
//            double[][][] zVariable = new double[model.getTrucksNumber()][model.getMaximumAvailableTrips()][model.getTotalOperationTime()];
//            double[][][] wVariable = new double[model.getTrucksNumber()][model.getMaximumAvailableTrips()][model.getTotalOperationTime()];
//
//
//            for (int n = 0; n < model.getTrucksNumber(); n++) {
//                for (int i = 0; i < model.getTanksNumber(); i++) {
//                    for (int k = model.getMaximumAvailableTrips() - 1; k >= 0; k--) {
//                        xVariable[n][i][k] = greedySolver.getxSolution()[n][i][model.getMaximumAvailableTrips() - k-1];
//                    }
//                }
//            }
//            int[] truckTotalTrips = new int[model.getTrucksNumber()];
//            for (int p = 0; p < model.getTrucksNumber(); p++) {
//                truckTotalTrips[p] = 0;
//            }
//
//            //for each truck
//
//
//                            //calculate total trips per truck
//                            for (int p = 0; p < model.getTrucksNumber(); p++) {
//                                for (int i = 0; i < model.getTanksNumber(); i++) {
//                                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                                    xVariable[p][i][k] = 0;//initialize to zeros
//                                        if (greedySolver.getxSolution()[p][i][k] == 1) {
//                                        truckTotalTrips[p]++;
//                                        }
//                                    }
//                                }
//                            }
//
//
//                        for (int n = 0; n < model.getTrucksNumber(); n++) {
//                        int numberOfTripsBeginsFrom = model.getMaximumAvailableTrips()-truckTotalTrips[n];
//
//                            for (int i = 0; i < model.getTanksNumber(); i++) {
//                                int tempCounter = 0;
//                                for (int k = numberOfTripsBeginsFrom; k < model.getMaximumAvailableTrips(); k++) {
//                                    xVariable[n][i][k] = greedySolver.getxSolution()[n][i][tempCounter];
//                                    tempCounter++;
//                                }
//                            }
//                        }
//
//
//
//            ArrayList<Integer> tempList = new ArrayList<>();
//
//            for (int n = 0; n < model.getTrucksNumber(); n++) {
//                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                        if (greedySolver.getySolution()[n][k] == 0) {
//                            tempList.add(model.getTruckLoadingTime()[n]);
//                        } else {
//                            tempList.add((int) greedySolver.getySolution()[n][k]);
//                        }
//                    }
//
//                        Collections.sort(tempList);
//                        for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                            yVariable[n][k] = tempList.get(k);
//                        }
//                        tempList.clear();
//            }
//
//
//            for (int t = 0; t < model.getTotalOperationTime(); t++) {
//                for (int n = 0; n < model.getTrucksNumber(); n++) {
//                    for (int k = 1; k < model.getMaximumAvailableTrips(); k++) {
//                        for (int i = 0; i < model.getTanksNumber(); i++) {
//                            if (xVariable[n][i][k] == 1 && yVariable[n][k] == t) {
//                                zVariable[n][k][t - model.getTruckLoadingTime()[n]] = 1;
//                            }
//                        }
//                    }
//                }
//            }
//
//
//            for (int n = 0; n < model.getTrucksNumber(); n++) {
//                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
//                        for (int k = 0; k < z[n].length; k++) {
//                        if (zVariable[n][k][t] == 1) {
//                            for (int u = t; u < model.getTruckLoadingTime()[n] + t; u++) {
//                                wVariable[n][k][u] = 1;
//                            }
//                        }
//                    }
//                }
//            }
//
//
//            int idxX = 0;
//
//            for (int n = 0; n < model.getTrucksNumber(); n++) {
//                for (int i = 0; i < model.getTanksNumber(); i++) {
//                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                        startX[idxX] = x[n][i][k]; // Reference the existing variables
//                        startXValues[idxX] = xVariable[n][i][k]; // Assume greedyX is filled correctly
//                        idxX++;
//
//                    }
//                }
//            }
//
//
//            for (int n = 0; n < model.getTrucksNumber(); n++) {
//                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                    startX[idxX] = y[n][k]; // Reference the existing variables
//                    startXValues[idxX] = yVariable[n][k]; // Assume greedyX is filled correctly
//                    idxX++;
//                }
//            }
//
//            for (int n = 0; n < model.getTrucksNumber(); n++) {
//                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
//                        startX[idxX] = z[n][k][t]; // Reference the existing variables
//                        startXValues[idxX] = zVariable[n][k][t]; // Assume greedyX is filled correctly
//                        idxX++;
//                    }
//                }
//            }
//
//            for (int n = 0; n < model.getTrucksNumber(); n++) {
//                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
//                        startX[idxX] = w[n][k][t]; // Reference the existing variables
//                        startXValues[idxX] = wVariable[n][k][t]; // Assume greedyX is filled correctly
//                        idxX++;
//                    }
//                }
//            }
//
//
////
////            for (int p=0; p < idxX; p++) {
////                System.out.println("Variable "+startX[p]+ " with value "+startXValues[p]);
////            }
//
//
//// Add your MIP start
//
//cplex.addMIPStart(startX, startXValues);

            if (cplex.solve()) {
                printVariablesValues();
            } else {
                System.out.println("Solution not found.");
            }
        } catch (IloException e) {
            throw new RuntimeException(e);
        } finally {
            // Cleanup
            cplex.end();
        }
    } // model

    public void initializeVariables() throws IloException {
        //The solution will be stored in the table above. "L" means loading, "T{#tankNumber}" means traveling to Tank number #
        //"TD" means traveling to depot, "U{#tankNumber}" means unloading to depot
        //Initializing truck solution
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int t = 0; t < model.getTotalOperationTime(); t++) {
                truckSolution[n][t] = "0";
            }
        }


        //Declare variables
        //Variable #1
        //x_ivk∈{0,1}, it takes value 1 if truck ν visits delivery location i on
        //its kth trip, and 0 otherwise
        //It is a boolean
        //Define x
        //Initialize x
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int i = 0; i < model.getTanksNumber(); i++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    x[n][i][k] = cplex.boolVar(("x_" + n + "_" + i + "_" + k));
                } //Available Trips Loop
            } //Tanks Loop
        } //Trucks Loop

        //Variable #2
        //y_vk  represents the departure time of truck v on its kth trip
        // It is an integer as it represents a certain timeslot
        //It is NOT a boolean. It must be smaller that the Total Operation Time
        //Define y
        //Initialize y
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                y[n][k] = cplex.intVar(0, model.getTotalOperationTime(), "y_" + n + "_" + k);
                //We define an integer with lower bound 0 and maximum number the total
                //Operation Time as well as a naming style for debugging
            }
        }

        //Variable #3
        //z_vkτ∈{0,1} it takes value 1 if truck v is assigned as a loading
        //start time (on its kth trip) the time slot τ, and 0 otherwise.
        //It is a boolean
        //Define z
        //Initialize z
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    z[n][k][t] = cplex.boolVar("z_" + n + "_" + k + "_" + t);
                }
            }
        }

        //Variable #4
        //w_vkτ∈{0,1} it takes value 1 if truck v (on its kth trip) is serviced during time slot τ
        //and 0 otherwise
        //It is a boolean
        //Define w
        //Initialize w
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    w[n][k][t] = cplex.boolVar("w_" + n + "_" + k + "_" + t);
                }
            }
        }

        //Initialize b
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                b[n][k] = cplex.intVar(0, model.getTotalOperationTime(), "b_" + n + "_" + k);
                //We define an integer with lower bound 0 and maximum number the total
                //Operation Time as well as a naming style for debugging
            }
        }

        //Variable #5 TEST
        //w_vkτ∈{0,1} it takes value 1 if truck v (on its kth trip) is serviced during time slot τ
        //and 0 otherwise
        //It is a boolean
        //Define r
//            IloNumVar[][][][] r = new IloNumVar[model.getTanksNumber()][model.getTrucksNumber()][model.getMaximumAvailableTrips()][model.getTotalOperationTime()];
//            //Initialize r
//            for (int i = 0; i < model.getTanksNumber(); i++) {
//                for (int n = 0; n < model.getTrucksNumber(); n++) {
//                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                        for (int t = 0; t < model.getTotalOperationTime(); t++) {
//                            r[i][n][k][t] = cplex.boolVar("r_" + i + "_" + n + "_" +k + "_"+ t);
//                        }
//                    }
//                }
//            }

        //Variable #For waiting time at depot


    }//initializeVariables

    public void activateObjectiveFunction() throws IloException {
    IloNumVar M = cplex.numVar(0, Double.MAX_VALUE, IloNumVarType.Float, "M");
    cplex.addMinimize(M);
    for (int n = 0; n < model.getTrucksNumber(); n++) {
        for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
            cplex.addGe(M, y[n][k]);
        }
    }
}//activateObjectiveFunction


    public void activateConstraint1() throws IloException {
        //Constraint #1
        for (int i = 0; i < model.getTanksNumber(); i++) {
            //The first for corresponds to "For every"
            IloLinearNumExpr constraintExpr = cplex.linearNumExpr();
            //The rest for loops correspond to "ΣΣ"
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    constraintExpr.addTerm(model.getTruckCapacity()[n], x[n][i][k]);
                    //add.term has the coefficient/weight multiplication with the variable

                }
            }
            cplex.addGe(constraintExpr, model.getTankDemand()[i], "constraint for tank i: " + i);
        }//constraint #1
    }//activateConstraint1

    public void activateConstraint2() throws IloException {
        //Constraint #2
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                IloLinearNumExpr constraintExpr = cplex.linearNumExpr();
                for (int i = 0; i < model.getTanksNumber(); i++) {
                    constraintExpr.addTerm(1, x[n][i][k]);
                }
                cplex.addLe(constraintExpr, 1);
            }
        } //Constraint #2
    }

    public void activateConstraint3() throws IloException {

        //Constraint #3
        for (int t = 0; t < model.getTotalOperationTime(); t++) {
            IloLinearNumExpr constraintExpr = cplex.linearNumExpr();
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    constraintExpr.addTerm(1, z[n][k][t]);
                }
            }
            cplex.addLe(constraintExpr, 1); //1 is the number of loading positions
        }//constraint #3
    }//activateConstraint3

    public void activateConstraint4() throws IloException {
        //Constraint #4
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    constraintExpr1.addTerm(1, z[n][k][t]);
                }
                for (int i = 0; i < model.getTanksNumber(); i++) {
                    constraintExpr2.addTerm(1, x[n][i][k]);
                }
                cplex.addEq(constraintExpr1, constraintExpr2);
            }
        }//Constraint #4
    }//activateConstraint4

    public void activateConstraint5() throws IloException {
        //Constraint #5
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    IloLinearNumExpr constraintExpr = cplex.linearNumExpr();
                    constraintExpr.addTerm(model.getTruckLoadingTime()[n], z[n][k][t]);

                    for (int s = t; s < Math.min(model.getTotalOperationTime(), t + model.getTruckLoadingTime()[n]); s++) {
                        constraintExpr.addTerm(-1, w[n][k][s]);
                    }
                    constraintExpr.addTerm(model.getBigM(), z[n][k][t]);
                    cplex.addLe(constraintExpr, model.getBigM());
                }
            }
        }
    }//activateConstraint5

    public void activateConstraint6() throws IloException {
        //Constraint #6
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    constraintExpr1.addTerm(model.getTruckLoadingTime()[n], z[n][k][t]);
                    constraintExpr2.addTerm(1, w[n][k][t]);
                }
                cplex.addEq(constraintExpr1, constraintExpr2);
            }
        }//Constraint #6
    }//activateConstraint6

    public void activateConstraint7() throws IloException {
        //Constraint #7
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();

                constraintExpr1.addTerm(1, y[n][k]);

                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    constraintExpr1.addTerm(-t, z[n][k][t]);
                }

                cplex.addEq(constraintExpr1, model.getTruckLoadingTime()[n]);
            }
        }//Constraint #7
    }//activateConstraint7

    public void activateConstraint8() throws IloException {
        //Constraint #8
        for (int t = 0; t < model.getTotalOperationTime(); t++) {
            IloLinearNumExpr constraintExpr = cplex.linearNumExpr();
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    constraintExpr.addTerm(1, w[n][k][t]);
                }
            }
            cplex.addLe(constraintExpr, 1); //1 is the number of loading positions
        }//constraint 8
    }//activateConstraint8

    public void activateConstraint9() throws IloException {

        //Constraint #9
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips() - 1; k++) {

                IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();

                constraintExpr1.addTerm(1, y[n][k + 1]);
                constraintExpr2.addTerm(1, y[n][k]);
                constraintExpr2.addTerm(1, b[n][k + 1]);


                for (int i = 0; i < model.getTanksNumber(); i++) {
                    constraintExpr2.addTerm(2 * model.getTruckLoadingTime()[n] + 2 * model.getTruckDeliveryTimeToDepot()[i], x[n][i][k]);
                }

//                     Add constraint with a label
                IloRange constraint10 = (IloRange) cplex.addEq(constraintExpr1, constraintExpr2, "Constraint_10_" + n + "_" + k);
                constraintsList.add(constraint10); // Assuming constraintsList is a pre-declared List<IloRange>
                System.out.println(constraintExpr2.toString());
//                        cplex.addGe(constraintExpr1, constraintExpr2);
            }
        }//constraint #9
    }//activateConstraint9

    public void activateConstraint10() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 1; k < model.getMaximumAvailableTrips(); k++) {
                IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();

                constraintExpr1.addTerm(1, b[n][k]);


                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    constraintExpr2.addTerm(t, z[n][k][t]);
                }

//                        constraintExpr2.addTerm(-1,y[n][k-1]);
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    constraintExpr2.addTerm(-t - model.getTruckLoadingTime()[n], z[n][k - 1][t]);
                }


                for (int i = 0; i < model.getTanksNumber(); i++) {
                    constraintExpr2.addTerm(-model.getTruckLoadingTime()[n] - 2 * model.getTruckDeliveryTimeToDepot()[i], x[n][i][k - 1]);
                }


                IloRange constraint11 = (IloRange) cplex.addEq(constraintExpr1, constraintExpr2, "Constraint_11_" + n + "_" + k);
                constraintsList.add(constraint11); // Assuming constraintsList is a pre-declared List<IloRange>
                System.out.println(constraintExpr2.toString());
//                        cplex.addEq(constraintExpr1, constraintExpr2);
            }
        }
    }//activateConstraint10

    public void activateConstraintBasicPackage() throws IloException {
        activateConstraint1();
        activateConstraint2();
        activateConstraint3();
        activateConstraint4();
        activateConstraint5();
        activateConstraint6();
        activateConstraint7();
        activateConstraint8();
        activateConstraint9();
        activateConstraint10();
    }//


    public void printVariablesValues() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int i = 0; i < model.getTanksNumber(); i++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    this.xSolution[n][i][k] = cplex.getValue(x[n][i][k]);
                    this.ySolution[n][k] = cplex.getValue(y[n][k]);
                }
            }
        }

        System.out.println("RESULTS DIRECTLY FROM CPLEX");

        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int i = 0; i < model.getTanksNumber(); i++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    System.out.println("x_" + n + "_" + i + "_" + k + ": " + cplex.getValue(x[n][i][k]));
                }
            }
        }


        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                System.out.println("y_" + n + "_" + k + ": " + cplex.getValue(y[n][k]));
            }
        }


        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    System.out.println("z_" + n + "_" + k + "_" + t + ": " + cplex.getValue(z[n][k][t]));
                }
            }
        }

        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    System.out.println("w_" + n + "_" + k + "_" + t + ": " + cplex.getValue(w[n][k][t]));
                }
            }
        }

        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 1; k < model.getMaximumAvailableTrips(); k++) {
                System.out.println("b_" + n + "_" + k + ": " + cplex.getValue(b[n][k]));
            }
        }

        for (IloRange constraint : constraintsList) {
            double slack = cplex.getSlack(constraint); // Now IloRange is used correctly
            System.out.println("Slack for " + constraint.getName() + ": " + slack);
        }

//
//                System.out.println("AFTER ENTRY");
//                for (int n = 0; n< model.getTrucksNumber(); n++) {
//                    for (int i = 0; i < model.getTanksNumber(); i++) {
//                        for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                            System.out.println("x_"+n+"_"+i+"_"+k+": "+xVariable[n][i][k]);
//                        }
//                    }
//                }
//
//                for (int n = 0; n< model.getTrucksNumber(); n++) {
//                        for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                            System.out.println("y_"+n+"_"+k+": "+yVariable[n][k]);
//                        }
//
//                }
//
//
//
//                for (int n = 0; n< model.getTrucksNumber(); n++) {
//                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                        for (int t = 0; t < model.getTotalOperationTime(); t++) {
//                            System.out.println("z_"+n+"_"+k+"_"+t+": "+zVariable[n][k][t]);
//                        }
//                    }
//                }
//
//                for (int n = 0; n< model.getTrucksNumber(); n++) {
//                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                        for (int t = 0; t < model.getTotalOperationTime(); t++) {
//                            System.out.println("w_"+n+"_"+k+"_"+t+": "+wVariable[n][k][t]);
//                        }
//                    }
//                }


//                System.out.println("BEFORE ENTRY");
//                for (int n = 0; n< model.getTrucksNumber(); n++) {
//                    for (int i = 0; i < model.getTanksNumber(); i++) {
//                        for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
//                            System.out.println("x_"+n+"_"+i+"_"+k+": "+ greedyAlgorithmSolver.getxSolution()[n][i][k]);
//                        }
//                    }
//                }

    }//printVariablesValues

    public void activateGreedyAlgorithmSolver(int noOfIterations) {
        greedySolver.solveModel(noOfIterations);
    }//activateGreedyAlgorithmSolver

}//class