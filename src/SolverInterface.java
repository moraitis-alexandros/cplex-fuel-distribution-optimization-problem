public interface SolverInterface {

    void solveModel();

    double[][][] getxSolution();

    double[][] getySolution();
    void solveModel(int noOfIterations);

}
