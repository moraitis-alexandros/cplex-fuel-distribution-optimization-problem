import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SolverAdvanced implements SolverInterface {
    private double[][][] xSolution;
    private double[][] ySolution;
    private Model model;
    private IloCplex cplex;
    String[][] truckSolution;
    IloNumVar[][][] x; //ok x{nik}
    IloNumVar[][] y; //ok y{nk}
    IloNumVar[][][] z; //ok z{nkt}
    IloNumVar[][][] w; //ok w{nkt}
    IloNumVar[][] b;
    List<IloRange> constraintsList;
    double[][][] greedyX;
    SolverInterface greedySolver;

    //New Advanced Model variables introduced
    IloNumVar[][][] s; // s{nkt}
    IloNumVar[][][] f; // f{nkt}
    IloNumVar[][][][] l; //l{inkt}
    IloNumVar[][][][] q; //k{inkt}
    IloNumVar[][][][] r; //r{inkt}
    IloNumVar[][][] v; //y{ink}
    IloNumVar[][][][] u; //u{inkt}


    public SolverAdvanced() throws IloException {
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

        //New Advanced Model variables initialized
        this.s = new IloNumVar[model.getTrucksNumber()][model.getMaximumAvailableTrips()][model.getTotalOperationTime()];
        this.f = new IloNumVar[model.getTrucksNumber()][model.getMaximumAvailableTrips()][model.getTotalOperationTime()];
        this.l = new IloNumVar[model.getTanksNumber()][model.getTrucksNumber()][model.getMaximumAvailableTrips()][model.getTotalOperationTime()];
        this.q = new IloNumVar[model.getTanksNumber()][model.getTrucksNumber()][model.getMaximumAvailableTrips()][model.getTotalOperationTime()];
        this.r = new IloNumVar[model.getTanksNumber()][model.getTrucksNumber()][model.getMaximumAvailableTrips()][model.getTotalOperationTime()];
        this.v = new IloNumVar[model.getTanksNumber()][model.getTrucksNumber()][model.getMaximumAvailableTrips()];
        this.u = new IloNumVar[model.getTanksNumber()][model.getTrucksNumber()][model.getMaximumAvailableTrips()][model.getTotalOperationTime()];


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


//            activateGreedyAlgorithmSolver(100);

            //running solver
            initializeVariables();
            activateConstraintAdvancedPackage();
            activateObjectiveFunction();


            if (cplex.solve()) {
//                printVariablesValues();
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
        //Initializing truck solution
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int t = 0; t < model.getTotalOperationTime(); t++) {
                truckSolution[n][t] = "0";
            }
        }


        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int i = 0; i < model.getTanksNumber(); i++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    x[n][i][k] = cplex.boolVar(("x_" + n + "_" + i + "_" + k)); //initialize x
                    // !!KEEI IN MIND that in both x, v the indexes in mathematical formulation are ink
                    // and in Solver are nik. Also y{ink} in mathematical solver is equal to v{nik} in CPLEX program
                } //Available Trips Loop
            } //Tanks Loop
        } //Trucks Loop


            for (int i = 0; i < model.getTanksNumber(); i++) {
                for (int n = 0; n < model.getTrucksNumber(); n++) {
                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    v[n][i][k] = cplex.boolVar(("v_" + i + "_" + n + "_" + k));  //initialize v,
                    // !!KEEI IN MIND that in both x, v the indexes in mathematical formulation are ink
                    // and in Solver are nik. Also y{ink} in mathematical solver is equal to v{nik} in CPLEX program
                } //Available Trips Loop
            } //Tanks Loop
        } //Trucks Loop

        //Initialize y
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                y[n][k] = cplex.intVar(0, model.getTotalOperationTime(), "y_" + n + "_" + k);
                //We define an integer with lower bound 0 and maximum number the total
                //Operation Time as well as a naming style for debugging
            }
        }


        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    z[n][k][t] = cplex.boolVar("z_" + n + "_" + k + "_" + t); //initialize z
                    w[n][k][t] = cplex.boolVar("w_" + n + "_" + k + "_" + t); //initialize w
                    s[n][k][t] = cplex.boolVar("s_" + n + "_" + k + "_" + t); //initialize s
                    f[n][k][t] = cplex.boolVar("f_" + n + "_" + k + "_" + t); //initialize f
                }
            }
        }

        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        l[i][n][k][t] = cplex.boolVar("l_" + i + "_" + n + "_" + k + "_" + t); //initialize l
                        q[i][n][k][t] = cplex.boolVar("q_" + i + "_" + n + "_" + k + "_" + t); //initialize q
                        r[i][n][k][t] = cplex.boolVar("r_" + i + "_" + n + "_" + k + "_" + t); //initialize r
                        u[i][n][k][t] = cplex.boolVar("u_" + i + "_" + n + "_" + k + "_" + t); //initialize u
                    }
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



    //In paper correspond with constraint 15
    public void activateConstraint11() throws IloException {

        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int t = 0; t < model.getTotalOperationTime(); t++) {
                IloLinearNumExpr constraintExpr = cplex.linearNumExpr();
                for (int n = 0; n < model.getTrucksNumber(); n++) {
                    for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                        constraintExpr.addTerm(1, u[i][n][k][t]);
                    }
                }
                cplex.addLe(constraintExpr, 1);
            }
        }
    }

    //In paper correspond with constraint c1
    public void activateConstraint12() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips() ; k++) {
                for (int t = 0; t < model.getTotalOperationTime() - 1; t++) {
                    IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                    IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                    constraintExpr1.addTerm(1, s[n][k][t]);
                    constraintExpr2.addTerm(1, s[n][k][t + 1]);
                    cplex.addLe(constraintExpr1, constraintExpr2);
                }
            }
        }
    }//activateConstraint12

    //In paper correspond with constraint c2
    public void activateConstraint13() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int t = 0; t < model.getTotalOperationTime() -1; t++) {
                    IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                    IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                    constraintExpr1.addTerm(1, f[n][k][t]);
                    constraintExpr2.addTerm(1, f[n][k][t + 1]);
                    cplex.addLe(constraintExpr1, constraintExpr2);
                }
            }
        }
    }//activateConstraint13

    //In paper correspond with constraint c3
    public void activateConstraint14() throws IloException {
        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    for (int t = 0; t < model.getTotalOperationTime() - 1; t++) {
                        IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                        IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                        constraintExpr1.addTerm(1, l[i][n][k][t]);
                        constraintExpr2.addTerm(1, l[i][n][k][t + 1]);
                        cplex.addLe(constraintExpr1, constraintExpr2);
                    }
                }
            }
        }
    }//activateConstraint14

    //In paper correspond with constraint c4
    public void activateConstraint15() throws IloException {
        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    for (int t = 0; t < model.getTotalOperationTime() - 1; t++) {
                        IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                        IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                        constraintExpr1.addTerm(1, q[i][n][k][t]);
                        constraintExpr2.addTerm(1, q[i][n][k][t + 1]);
                        cplex.addLe(constraintExpr1, constraintExpr2);
                    }
                }
            }
        }
    }//activateConstraint15

    //In paper correspond with constraint c5
    public void activateConstraint16() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                    IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                    constraintExpr1.addTerm(1, w[n][k][t]);
                    constraintExpr2.addTerm(1, s[n][k][t]);
                    constraintExpr2.addTerm(-1, f[n][k][t]);
                    cplex.addEq(constraintExpr1, constraintExpr2);
                }
            }
        }
    }//activateConstraint16

    //In paper correspond with constraint c6
    public void activateConstraint17() throws IloException {
        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                        IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                        constraintExpr1.addTerm(1, u[i][n][k][t]);
                        constraintExpr2.addTerm(1, l[i][n][k][t]);
                        constraintExpr2.addTerm(-1, q[i][n][k][t]);
                        cplex.addEq(constraintExpr1, constraintExpr2);
                    }
                }
            }
        }
    }//activateConstraint17

    //In paper correspond with constraint c7
    public void activateConstraint18() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    constraintExpr1.addTerm(1, w[n][k][t]);
                }

                constraintExpr2.setConstant(model.getTruckLoadingTime()[n] - model.getBigM());
                for (int i = 0; i < model.getTanksNumber(); i++) {
                    constraintExpr2.addTerm(model.getBigM(), x[n][i][k]);
                }
                cplex.addGe(constraintExpr1, constraintExpr2);
            }
        }
    }//activateConstraint18

    //In paper correspond with constraint c8
    public void activateConstraint19() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    constraintExpr1.addTerm(1, w[n][k][t]);
                }

                for (int i = 0; i < model.getTanksNumber(); i++) {
                    constraintExpr2.addTerm(model.getTruckLoadingTime()[n], x[n][i][k]);
                }
                cplex.addLe(constraintExpr1, constraintExpr2);
            }
        }
    }//activateConstraint19

    //In paper correspond with constraint c9
    public void activateConstraint20() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int i = 0; i < model.getTanksNumber(); i++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                    IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        constraintExpr1.addTerm(1, u[i][n][k][t]);
                    }

                    constraintExpr2.setConstant(model.getTruckDeliveryTimeToDepot()[i] - model.getBigM());
                    constraintExpr2.addTerm(model.getBigM(), x[n][i][k]);
                    cplex.addGe(constraintExpr1, constraintExpr2);
                }
            }
        }
    }//activateConstraint20

    //In paper correspond with constraint c10
    public void activateConstraint21() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int i = 0; i < model.getTanksNumber(); i++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                    IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        constraintExpr1.addTerm(1, u[i][n][k][t]);
                    }
                    constraintExpr2.addTerm(model.getTruckDeliveryTimeToDepot()[i], x[n][i][k]);
                    cplex.addLe(constraintExpr1, constraintExpr2);
                }
            }
        }
    }//activateConstraint21

    //In paper correspond with constraint c11
    public void activateConstraint22() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                    IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                    constraintExpr1.addTerm(t, z[n][k][t]);
                    constraintExpr2.addTerm(t, s[n][k][t]);
                    cplex.addLe(constraintExpr1, constraintExpr2);
                }
            }
        }
    }//activateConstraint22

    //In paper correspond with constraint c12
    public void activateConstraint23() throws IloException {
        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                        IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                        constraintExpr1.addTerm(t, r[i][n][k][t]);
                        constraintExpr2.addTerm(t, l[i][n][k][t]);
                        cplex.addLe(constraintExpr1, constraintExpr2);
                    }
                }
            }
        }
    }//activateConstraint23

    //In paper correspond with constraint c13
    public void activateConstraint24() throws IloException {
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
        }
    }//activateConstraint24

    //In paper correspond with constraint c14
    public void activateConstraint25() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int i = 0; i < model.getTanksNumber(); i++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                    IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();

                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        constraintExpr1.addTerm(1, r[i][n][k][t]);
                    }


                    constraintExpr2.addTerm(1, x[n][i][k]);

                    cplex.addEq(constraintExpr1, constraintExpr2);
                }
            }
        }
    }//activateConstraint25


    //In paper correspond with constraint c15
    public void activateConstraint26() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                    IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                    constraintExpr1.addTerm(1, y[n][k]);
                    constraintExpr2.addTerm(t, s[n][k][t]);
                    cplex.addGe(constraintExpr1, constraintExpr2);
                }
            }
        }
    }//activateConstraint26


    //In paper correspond with constraint c16
    public void activateConstraint27() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                    IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                    constraintExpr1.addTerm(1, y[n][k]);
                    constraintExpr2.addTerm(t, f[n][k][t]);
                    cplex.addLe(constraintExpr1, constraintExpr2);
                }
            }
        }
    }//activateConstraint27

    //In paper correspond with constraint c17
    public void activateConstraint28() throws IloException {
        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                        IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();

                        constraintExpr1.setConstant(model.getBigM());
                        constraintExpr1.addTerm(-model.getBigM(), x[n][i][k]);
                        constraintExpr1.addTerm(1, v[i][n][k]);
                        constraintExpr2.addTerm(t, l[i][n][k][t]);

                        cplex.addGe(constraintExpr1, constraintExpr2);
                    }
                }
            }
        }
    }//activateConstraint28

    //In paper correspond with constraint c18
    public void activateConstraint29() throws IloException {
        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                        IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();

                        constraintExpr1.setConstant(-model.getBigM());
                        constraintExpr1.addTerm(model.getBigM(), x[n][i][k]);
                        constraintExpr1.addTerm(1, v[i][n][k]);
                        constraintExpr2.addTerm(t, q[i][n][k][t]);

                        cplex.addLe(constraintExpr1, constraintExpr2);
                    }
                }
            }
        }
    }//activateConstraint29

    //In paper correspond with constraint c19
    public void activateConstraint30() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int i = 0; i < model.getTanksNumber(); i++) {
                for (int k = 1; k < model.getMaximumAvailableTrips(); k++) {
                    IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                    IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                    constraintExpr1.setConstant(model.getBigM());
                    constraintExpr1.addTerm(-model.getBigM(), x[n][i][k]);

                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        constraintExpr1.addTerm(t, z[n][k][t]);
                    }
                    constraintExpr2.addTerm(1, v[i][n][k - 1]);
                    constraintExpr2.setConstant(model.getTruckDeliveryTimeToDepot()[i]);
                    cplex.addGe(constraintExpr1, constraintExpr2);
                }
            }
        }
    }//activateConstraint30

    //In paper correspond with constraint c20
    public void activateConstraint31() throws IloException {
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int i = 0; i < model.getTanksNumber(); i++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    IloLinearNumExpr constraintExpr1 = cplex.linearNumExpr();
                    IloLinearNumExpr constraintExpr2 = cplex.linearNumExpr();
                    constraintExpr1.setConstant(model.getBigM());
                    constraintExpr1.addTerm(-model.getBigM(), x[n][i][k]);

                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        constraintExpr1.addTerm(t, r[i][n][k][t]);
                    }

                    constraintExpr2.addTerm(1, y[n][k]);
                    constraintExpr2.setConstant(model.getTruckDeliveryTimeToDepot()[i]);
                    cplex.addGe(constraintExpr1, constraintExpr2);
                }
            }
        }
    }//activateConstraint31






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


    public void activateConstraintAdvancedPackage() throws IloException {
        activateConstraint1();
        activateConstraint2();
        activateConstraint8();
        activateConstraint11();
        activateConstraint12();
        activateConstraint13();
        activateConstraint14();
        activateConstraint15();
        activateConstraint16();
        activateConstraint17();
        activateConstraint18();
        activateConstraint19();
        activateConstraint20();
        activateConstraint21();
        activateConstraint22();
        activateConstraint23();
        activateConstraint24();
        activateConstraint25();
        activateConstraint26();
        activateConstraint27();
        activateConstraint28();
        activateConstraint29();
        activateConstraint30();
        activateConstraint31();
    }//activateConstraintAdvancedPackage

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
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    System.out.println("s_" + n + "_" + k + "_" + t + ": " + cplex.getValue(s[n][k][t]));
                }
            }
        }

        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int t = 0; t < model.getTotalOperationTime(); t++) {
                    System.out.println("f_" + n + "_" + k + "_" + t + ": " + cplex.getValue(f[n][k][t]));
                }
            }
        }

        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        System.out.println("l_" + i +"_" + n + "_" + k + "_" + t + ": " + cplex.getValue(l[i][n][k][t]));
                    }
                }
            }
        }

        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        System.out.println("q(k)_" + i +"_" + n + "_" + k + "_" + t + ": " + cplex.getValue(q[i][n][k][t]));
                    }
                }
            }
        }

        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        System.out.println("r_" + i +"_" + n + "_" + k + "_" + t + ": " + cplex.getValue(r[i][n][k][t]));
                    }
                }
            }
        }

        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                    for (int t = 0; t < model.getTotalOperationTime(); t++) {
                        System.out.println("u_" + i +"_" + n + "_" + k + "_" + t + ": " + cplex.getValue(u[i][n][k][t]));
                    }
                }
            }
        }

        for (int i = 0; i < model.getTanksNumber(); i++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                        System.out.println("y(v)_" + i +"_" + n + "_" + k + ": " + cplex.getValue(v[i][n][k]));
                    }
                }
            }




        for (IloRange constraint : constraintsList) {
            double slack = cplex.getSlack(constraint); // Now IloRange is used correctly
            System.out.println("Slack for " + constraint.getName() + ": " + slack);
        }



    }//printVariablesValues

    public void activateGreedyAlgorithmSolver(int noOfIterations) {
        greedySolver.solveModel(noOfIterations);
    }//activateGreedyAlgorithmSolver



}//class