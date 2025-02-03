import ilog.concert.IloException;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IloException {
        Model model = new Model();
        SolverInterface solver = new SolverAdvanced() ;
        solver.solveModel();
        SolutionViewer solutionViewer = new SolutionViewer(model, solver);
        solutionViewer.printSolution();
        SolutionChecker solutionChecker = new SolutionChecker(model, solutionViewer.getTruckSolution());
        solutionChecker.solutionCheck();

//end of solver implementation
    }//main

}//main class
