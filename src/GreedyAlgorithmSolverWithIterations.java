import java.time.Duration;
import java.time.Instant;

public class GreedyAlgorithmSolverWithIterations implements SolverInterface {


    private double[][][] xSolution;
    private double[][] ySolution;

    @Override
    public void solveModel() {

    }


    public void setxSolution(double[][][] xSolution) {
        this.xSolution = xSolution;
    }

    public void setySolution(double[][] ySolution) {
        this.ySolution = ySolution;
    }

    @Override
    public double[][][] getxSolution() {
        return xSolution;

    }

    @Override
    public double[][] getySolution() {
        return ySolution;
    }

    @Override
    public void solveModel(int noOfIterations) {
        Instant start = Instant.now();
        int bestSolutionFound = 200;
        SolverInterface bestSolverFound = null;
        Model model = new Model();
        for (int i=0 ; i<noOfIterations; i++) {
            SolverInterface solver = new GreedyAlgorithmSolver();
            solver.solveModel();
            SolutionViewer solutionViewer = new SolutionViewer(model, solver);
            solutionViewer.printSolution();
            if (solutionViewer.getLastLoadedTimeslot() < bestSolutionFound) {
                bestSolverFound = solver;
                bestSolutionFound = solutionViewer.getLastLoadedTimeslot();
            }
        }//for

        setxSolution(bestSolverFound.getxSolution());
        setySolution(bestSolverFound.getySolution());
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time Elapsed for Greedy Operation: "+timeElapsed.getSeconds()+" sec");
        System.out.println("********* After "+noOfIterations+" iterations the best result is: *********");

    }
}//class
