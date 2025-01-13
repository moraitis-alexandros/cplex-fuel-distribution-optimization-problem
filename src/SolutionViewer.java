import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.*;

/**
 * A class for viewing the solution of the fuel distribution model
 */
public class SolutionViewer {

    private String[][] refinedTruckSolution;
    private int tanksNumber;
    private int trucksNumber;
    private int totalOperationTime;
    private int[] truckDeliveryTimeToDepot;
    private int maximumAvailableTrips;
    private int[] truckLoadingTime;
    private double[][][] xSolution;
    private  double[][] ySolution;

    public String[][] getTruckSolution() {
        return truckSolution;
    }

    public void setTruckSolution(String[][] truckSolution) {
        this.truckSolution = truckSolution;
    }

    private String[][] truckSolution;
    private Vector<Integer> v;

    public int getLastLoadedTimeslot() {
        return lastLoadedTimeslot;
    }

    public void setLastLoadedTimeslot(int lastLoadedTimeslot) {
        this.lastLoadedTimeslot = lastLoadedTimeslot;
    }

    private int lastLoadedTimeslot;
    public String[][] getRefinedTruckSolution() {
        return refinedTruckSolution;
    }

    public void setRefinedTruckSolution(String[][] refinedTruckSolution) {
        this.refinedTruckSolution = refinedTruckSolution;
    }

    public  SolutionViewer(Model model, SolverInterface solverInterface) {
        this.trucksNumber = model.getTrucksNumber();
        this.totalOperationTime = model.getTotalOperationTime();
        this.tanksNumber = model.getTanksNumber();
        this.truckDeliveryTimeToDepot = model.getTruckDeliveryTimeToDepot();
        this.maximumAvailableTrips = model.getMaximumAvailableTrips();
        this.truckLoadingTime = model.getTruckLoadingTime();
        this.xSolution = solverInterface.getxSolution();
       this.ySolution = solverInterface.getySolution();
        this.truckSolution =  new String[this.trucksNumber][this.totalOperationTime];
        this.refinedTruckSolution = new String[this.trucksNumber][findLargestIndex()];
        this.v = new Vector<>();
    }


    public void printSolution() {

        //The solution will be stored in the truckSolution[][] above. "L" means loading, "T{#tankNumber}" means traveling to Tank number #
        //"TD" means traveling to depot, "U{#tankNumber}" means unloading to depot
        //Initializing truck solution
        for (int n = 0; n < trucksNumber; n++) {
            for (int t = 0; t < totalOperationTime; t++) {
                truckSolution[n][t] = "0";
            }
        }

        //Make a solution table for each truck - trip - destination
        //find the total trips for each truck
        //create an array for each truck that has elements equal to trips value equal to tank.
        int[][] trucksTrips = new int[trucksNumber][maximumAvailableTrips];
        int[][] truckTanks = new int[trucksNumber][maximumAvailableTrips];
        int counter = 0;
        for (int n = 0; n < trucksNumber; n++) {
            ArrayList<Double> tripsOfTruckIntoTank = new ArrayList<Double>();
            ArrayList<Integer> tanksOnEachTruckTrip = new ArrayList<Integer>();

            for (int i = 0; i < tanksNumber; i++) {
                for (int k = 0; k < maximumAvailableTrips; k++) {
                    if (xSolution[n][i][k] > 0.5) { //check if solution found is feasible
                        counter++;
                        tripsOfTruckIntoTank.add(ySolution[n][k]);
                        tanksOnEachTruckTrip.add(i);
                    }
                }
            }

            trucksTrips[n] = new int[tripsOfTruckIntoTank.size()];
            truckTanks[n] = new int[tanksOnEachTruckTrip.size()]; //the assigned trips and the assigned tanks are equal


            for (int k = 0; k < tripsOfTruckIntoTank.size(); k++) {
                trucksTrips[n][k] = tripsOfTruckIntoTank.get(k).intValue();
                truckTanks[n][k] = tanksOnEachTruckTrip.get(k);
            }
            counter = 0;

        }

        //Storing the solution into a 2d string array named truckSolution
        for (int n = 0; n < trucksNumber; n++) {
            for (int o = 0; o < trucksTrips[n].length; o++) {
                int truckRefuellingStart = trucksTrips[n][o] - truckLoadingTime[n];
                int truckDepartureTime = trucksTrips[n][o];
                for (int t = 0; t < totalOperationTime; t++) {
                    if (truckRefuellingStart == t) {
                        truckSolution[n][t] = "L";
                    }

                    if (truckDepartureTime == t) {
                        truckSolution[n][t] = "D" + truckTanks[n][o];
                    }

                    if (t >= truckRefuellingStart && t <= truckDepartureTime) {
                        if (Objects.equals(truckSolution[n][t], "0")) {
                            truckSolution[n][t] = "L";
                        }
                    }

                    int truckArrivalTime = truckDepartureTime + truckDeliveryTimeToDepot[truckTanks[n][o]];
                    int truckFinishUnloadingToDepotTime = truckArrivalTime + truckLoadingTime[n];

                    if (t >= truckArrivalTime && t < truckFinishUnloadingToDepotTime) {
                        if (Objects.equals(truckSolution[n][t], "0")) {
                            truckSolution[n][t] = "U" + truckTanks[n][o];
                        }
                    }

                    if (t >= truckFinishUnloadingToDepotTime && t < truckFinishUnloadingToDepotTime + truckDeliveryTimeToDepot[truckTanks[n][o]]) {
                        if (Objects.equals(truckSolution[n][t], "0")) {
                            truckSolution[n][t] = "TD";
                        }
                    }

                    if (t > truckDepartureTime && t <= truckArrivalTime) {
                        if (Objects.equals(truckSolution[n][t], "0")) {
                            truckSolution[n][t] = "T" + truckTanks[n][o];
                        }
                    }
                }
            }

        }

        //find the last loaded timeslot
        setLastLoadedTimeslot(0);
        //Remove 0 timeslots that are larger for the last arrival on depot
        int largestIndex = 0;

            for (int t = 0; t < totalOperationTime; t++) { //This for loop must be first
                for (int n = 0; n < trucksNumber; n++) {//This for loop must be second
                if (Objects.equals(truckSolution[n][t], "TD" )) {
                    largestIndex = t ;
                }
                    if (Objects.equals(truckSolution[n][t], "L" )) {
                        lastLoadedTimeslot = t;
                    }
            }
        }

//        refinedTruckSolution = new String[trucksNumber][largestIndex + 1];
//        for (int t = 0; t <= largestIndex; t++) {
//            for (int n = 0; n < trucksNumber; n++) {
//                refinedTruckSolution[n][t] = "0";
//                refinedTruckSolution[n][t] = truckSolution[n][t];
//            }
//        }



        //The truck trips at this point are not ordered by the correct trip, i.e. in trip 0 will be at timeslot 53
        //and trip 1 will be at timeslot 7. This happens because???? The solution is to order
        //But we must be careful to order simultaneously and the corresponding tanks.
        //As a result we order --> truckTanks[][] and truckTrips[][]


//        for (int n=0; n < trucksNumber; n++) {
//            for (int k = 0; k < trucksTrips[n].length; k++) {
//                v.add(trucksTrips[n][k]);
//            }
//
//            // Elements in vector gets sorted
//            Collections.sort(v);
//            for (int k = 0; k < trucksTrips[n].length; k++) {
//
//                // Sorted elements are pushed back from
//                // vector to row
//                trucksTrips[n][k] = v.get(k);
//            }
//            // Elements are removed from vector
//            v.removeAll(v);
//
//        }

        //Make a diy sorting function
        for (int n = 0; n < trucksNumber; n++) {

            // Sorting each truck's trips and tanks in ascending order
            for (int i = 0; i < trucksTrips[n].length - 1; i++) {
                //WE NEED ANOTHER LOOP to make multiple passes
                for (int j = 0; j < trucksTrips[n].length - 1 - i; j++) {

                    // Check if the next element is smaller
                    if (trucksTrips[n][j] > trucksTrips[n][j + 1]) {

                        // Swap trucksTrips elements
                        int tempTrip = trucksTrips[n][j];
                        trucksTrips[n][j] = trucksTrips[n][j + 1];
                        trucksTrips[n][j + 1] = tempTrip;

                        // Swap corresponding truckTanks elements
                        int tempTank = truckTanks[n][j];
                        truckTanks[n][j] = truckTanks[n][j + 1];
                        truckTanks[n][j + 1] = tempTank;
                    }
                }
            }
        }


        System.out.println("**************");
        for (int n = 0; n < trucksNumber; n++) {
            System.out.println("Truck " + n + " trips: " + trucksTrips[n].length);
            System.out.println("Truck " + n + " starts refuelling at timeslots: ");
            for (int o = 0; o < trucksTrips[n].length; o++) {
                int truckRefuellingStart = trucksTrips[n][o] - truckLoadingTime[n];
                System.out.println(truckRefuellingStart + ", for tank " + truckTanks[n][o] + ", on trip " + o);
            }
            System.out.println();
            System.out.println("**************");
        }

////Printing Horizonatlly
//        // First, print the time labels
//        System.out.print("Timeslot    "); // Padding for the "Truck |" label
//        for (int o = 0; o < largestIndex + 1; o++) {
//            System.out.printf("%-3d", o); // Print time with spacing of 3
//        }
//        System.out.println();
//
//        for (int n = 0; n < trucksNumber; n++) {
//            System.out.printf("Truck %-2d |  ", n); // Truck label with consistent spacing
//            for (int o = 0; o < truckSolution[n].length; o++) {
//                System.out.printf("%-3s", truckSolution[n][o]); // Align truck data
//            }
//            System.out.println();
//        }

//Print Vertically
        // Print truck labels in the header row
        System.out.print("Timeslot | ");
        for (int n = 0; n < trucksNumber; n++) {
            System.out.printf("Truck %-3d", n); // Label each truck with padding
        }
        System.out.println(); // Move to the next line after the truck labels

// Print each timeslot with its corresponding truck data
        for (int o = 0; o < totalOperationTime; o++) {
//        for (int o = 0; o < largestIndex + 1; o++) {
            System.out.printf("%-14d", o); // Print the timeslot with spacing
            for (int n = 0; n < trucksNumber; n++) {
                System.out.printf("%-9s", truckSolution[n][o]); // Print truck data for each timeslot
            }
            System.out.println(); // Move to the next line after each timeslot
        }

        System.out.println();
        int departureTime = lastLoadedTimeslot + 1;
        System.out.println("Departure of Last Loaded Vehicle in Timeslot "+departureTime);


        //            int[][] timeline = new int[trucksNumber][totalOperationTime];
//
//            // Populate the timeline array based on z and w
//            for (int n = 0; n < trucksNumber; n++) {
//                for (int k = 0; k < maximumAvailableTrips; k++) {
//                    for (int t = 0; t < totalOperationTime; t++) {
//                        if (cplex.getValue(z[n][k][t]) > 0.5 || cplex.getValue(w[n][k][t]) > 0.5) {
//                            timeline[n][t] = 1; // Mark as 1 if truck n is loading or being serviced at time t
//                        }
//                    }
//                }
//            }

//            // Print the timeline
//            System.out.println("Timeline (1 = Service/Loading, 0 = Idle):");
//            System.out.print("Truck   |   ");
//            for (int t = 0; t < totalOperationTime; t++) {
//                System.out.printf("%2d ", t);
//            }
//            System.out.println();
//
//            for (int n = 0; n < trucksNumber; n++) {
//                System.out.print("Truck " + n + "   | ");
//                for (int t = 0; t < totalOperationTime; t++) {
//                    System.out.printf("%2d ", timeline[n][t]);
//                }
//                System.out.println();
//            }

    }//printSolutionFunction

    public int findLargestIndex() {
        //Remove 0 timeslots that are larger for the last arrival on depot
        int largestIndex = 0;
        for (int n = 0; n < trucksNumber; n++) {
            for (int t = 0; t < totalOperationTime; t++) {
                if (Objects.equals(truckSolution[n][t], "TD")) {
                    largestIndex = t;
                }
            }
        }
        return largestIndex;
    }//findLargestIndex


}//class
