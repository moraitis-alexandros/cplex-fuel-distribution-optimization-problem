import ilog.concert.IloException;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IloException {

        //Greedy Algorithm Implementation
//        int bestSolutionFound = 200;
//        int iterations = 1;
//        SolverInterface bestSolverFound = null;
//        SolutionViewer bestSolutionViewer;
//        Model model = new Model();
//        for (int i=0 ; i<iterations; i++) {
//            SolverInterface solver = new GreedyAlgorithmSolver();
//            solver.solveModel();
//            SolutionViewer solutionViewer = new SolutionViewer(model, solver);
//            solutionViewer.printSolution();
//            SolutionChecker solutionChecker = new SolutionChecker(model, solutionViewer.getRefinedTruckSolution());
//            solutionChecker.solutionCheck();
//            if (solutionViewer.getLastLoadedTimeslot() < bestSolutionFound) {
//                bestSolverFound = solver;
//                bestSolutionFound = solutionViewer.getLastLoadedTimeslot();
//            }
//        }//for
//
//        System.out.println();
//        bestSolutionViewer = new SolutionViewer(model, bestSolverFound);
//        System.out.println("********* After "+iterations+" iterations the best result is: *********");
//        bestSolutionViewer.printSolution();
//        SolutionChecker solutionChecker = new SolutionChecker(model, bestSolutionViewer.getRefinedTruckSolution());
//        solutionChecker.solutionCheck();

//end of greedy solver implementation


//    Solver Implementation
        Model model = new Model();
        SolverInterface solver = new Solver() ; //Optimal Earliest Refuel Timeslot:19
        solver.solveModel();
        SolutionViewer solutionViewer = new SolutionViewer(model, solver);
        solutionViewer.printSolution();
        SolutionChecker solutionChecker = new SolutionChecker(model, solutionViewer.getTruckSolution());
        solutionChecker.solutionCheck();

//end of solver implementation

    }//main

}//main class
