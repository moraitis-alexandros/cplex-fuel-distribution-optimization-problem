import ilog.concert.IloNumVar;

import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.IntStream;

public class GreedyAlgorithmSolver implements SolverInterface  {
    private double[][][] xSolution;
    private double[][] ySolution;
    private Model model;
    private double[][][] xTable;
    private double[][] yTable;
    private HashMap<Integer, Integer> truckCapacityMap;
    private HashMap<Integer, Integer> tankCapacityMap;
    int earliestRefuelTimeslot;
    int solutionFound;
    int[] truckLatestAvailableLoadingTimeslot;
    int[][][] tankUnloadingTimeslots;
    int[] currentTankLevel;
    int overallModelCapacity;
    int currentModelCapacity;
    int[] truckTripsNumber;
    int min;
    int refuelStart;
    int refuelEnd;
    int candidateTruck;
    int candidateTank;
    int minTruck;
    List<Map.Entry<Integer, Integer>>  truckCapacity;
    List<Map.Entry<Integer, Integer>>  tankCapacity;
    List<Integer> trucksAvailableAtZeroTimeslot;
    List<Double> tanksCumulativeProbability;

    public GreedyAlgorithmSolver() {
        this.model = new Model();
        xTable = new double[model.getTrucksNumber()][model.getTanksNumber()][model.getMaximumAvailableTrips()];
        yTable = new double[model.getTrucksNumber()][model.getMaximumAvailableTrips()];
        truckCapacityMap = new HashMap<>();
        tankCapacityMap = new HashMap<>();
        earliestRefuelTimeslot = 0;
        truckLatestAvailableLoadingTimeslot = new int[model.getTrucksNumber()];
        tankUnloadingTimeslots = new int[model.getTrucksNumber()][model.getTanksNumber()][model.getTotalOperationTime()];
        currentTankLevel = new int[model.getTanksNumber()];
        overallModelCapacity = 0;
        currentModelCapacity = 0;
        truckTripsNumber = new int[model.getTrucksNumber()];
        truckCapacity = new ArrayList<>();
        trucksAvailableAtZeroTimeslot = new ArrayList<>();
        tanksCumulativeProbability = new ArrayList<>();
        initialization();
        orderTrucksDescendingCapacity();
        orderTanksDescendingCapacity();

    }//constructor


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
    calculateTanksCumulativeProbabilities();
            while (!allTanksFull()) {
            candidateTank = findBestTankFit();
                if (currentTankLevel[candidateTank] < model.getTankDemand()[candidateTank]) {
                candidateTruck = findBestTruckFit();
                refreshModelVariables();
            }
            printCapacityDetails();
        }//iterate through each tank sequentially until each demand filled and then continues to the other
    solutionFound = earliestRefuelTimeslot;
    }//for model


    /**
     * Initializes the tables required for the greedy algorithm
     */
    public void initialization() {

        //Initialize yTable
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                yTable[n][k] = 0.0;
            }
        }

        //Initialize xTable
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            for (int k = 0; k < model.getMaximumAvailableTrips(); k++) {
                for (int i = 0; i < model.getTanksNumber(); i++) {
                    xTable[n][i][k] = 0;
                }
            }
        }

        setxSolution(xTable);
        setySolution(yTable);

        //Create for each tank its current fuel level
        for (int i = 0; i < model.getTanksNumber(); i++) {
            currentTankLevel[i] = 0;
            System.out.println("Tank " + i + " level: " + currentTankLevel[i]);
        }

        //Fill tankUnloadingTimeslots with 0
        for (int j = 0; j < model.getTrucksNumber(); j++) {
            for (int l = 0; l < model.getTanksNumber(); l++) {
                for (int i = 0; i < model.getTotalOperationTime(); i++) {
                    tankUnloadingTimeslots[j][l][i] =0;
                }
            }
        }

        //Fill TruckLatestAvailableLoadingTimeslot with 0
        for (int truck = 0; truck < model.getTrucksNumber(); truck++) {
            truckLatestAvailableLoadingTimeslot[truck] = 0;
        }


        //-------------Initialization Printings-------------
        //Find overall model capacity. We assume that the total
        //capacity can be covered from dtanker which is in depot
        for (int i : model.getTankDemand()) {
            overallModelCapacity += i;
        }
        System.out.println("---------------Overall Model Tank Capacity: " + overallModelCapacity + " -------------------");



        //Fill truckTripsNumber with 0
        for (int i = 0; i < model.getTrucksNumber(); i++) {
            truckTripsNumber[i] = 0;
        }



    }//initialization

    /**
     * From all the candidate Trucks returns the one that best fits the criteria
     * @return truck number
     */
    public int findBestTruckFit() {
        List<Integer> truckThatAreInZeroTimeslot = new ArrayList<>();
        min = 100000;
        int zeroTimeslotCounter = 0;
        //find in a loop the truck that returns the earliest to the depot
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            int pickedTruck = truckCapacity.get(n).getKey();
//            System.out.println("For truck "+pickedTruck+" LAT "+truckLatestAvailableLoadingTimeslot[pickedTruck]);
            if (truckLatestAvailableLoadingTimeslot[pickedTruck] < min ) {
                min = truckLatestAvailableLoadingTimeslot[pickedTruck];
                minTruck = pickedTruck;
            }
            if (min < earliestRefuelTimeslot) {
                min = earliestRefuelTimeslot;
            }
            //for the initialization. If it has more than one then
            //the truck chosen should be the truck number-counts
            if (truckLatestAvailableLoadingTimeslot[pickedTruck]==0) {
                zeroTimeslotCounter++;
            }

        }//find the truck with the earliest departure
//        System.out.println("Minimum AFL Timeslot is " + min + " for Truck " + minTruck );
        //Find the candidate Truck

        if ((zeroTimeslotCounter > 1) && (zeroTimeslotCounter <= model.getTrucksNumber())) {
//            System.out.println("ENTERED with zero times counter: " +zeroTimeslotCounter);
            minTruck = truckCapacity.get(trucksAvailableAtZeroTimeslot.get(0)).getKey();
            trucksAvailableAtZeroTimeslot.remove(truckCapacity.get(candidateTruck).getKey());
        }
        System.out.println("Picked Truck "+minTruck);
        return minTruck;
    }

    /**
     * Print some details on Tank Capacity
     */
    public void printCapacityDetails() {
        System.out.println("Current Model Capacity: "+currentModelCapacity);

        for (int i=0; i< model.getTanksNumber(); i++) {
            System.out.println("Tank "+i+" current level is: "+currentTankLevel[i]);
        }
    }

    /**
     * After making an assignement it refreshes the necessary variables
     */
    public void refreshModelVariables() {
        //After finding best candidate tank and truck refresh the necessary variables

           xSolution[candidateTruck][candidateTank][truckTripsNumber[candidateTruck]] = 1;
           ySolution[candidateTruck][truckTripsNumber[candidateTruck]] = min + model.getTruckLoadingTime()[candidateTruck];
           truckLatestAvailableLoadingTimeslot[candidateTruck] = min + 2 * model.getTruckLoadingTime()[candidateTruck] + 2 * model.getTruckDeliveryTimeToDepot()[candidateTank];
           earliestRefuelTimeslot = min + model.getTruckLoadingTime()[candidateTruck];
           currentModelCapacity += model.getTruckCapacity()[candidateTruck];
           currentTankLevel[candidateTank] += model.getTruckCapacity()[candidateTruck];
           System.out.println("The earliest Refuel Timeslot is " + earliestRefuelTimeslot);
           refuelStart = min + model.getTruckLoadingTime()[candidateTruck] + model.getTruckDeliveryTimeToDepot()[candidateTank];
           refuelEnd = refuelStart + model.getTruckLoadingTime()[candidateTruck];
           for (int currentTimeslot = refuelStart; currentTimeslot < refuelEnd; currentTimeslot++) {
               tankUnloadingTimeslots[candidateTruck][candidateTank][currentTimeslot - 1] = 1;
           }
           System.out.println();
           truckTripsNumber[candidateTruck] += 1;



    }//refreshModelVariables

    /**
     * Creates the truckCapacitylist with trucks and their capacity on descending order.
     */
    public void orderTrucksDescendingCapacity() {

        for (int n = 0; n < model.getTrucksNumber(); n++) {
            truckCapacityMap.put(n,model.getTruckCapacity()[n]);
        }//initialize TruckList Capacity and order desc


        truckCapacity = new ArrayList<>(truckCapacityMap.entrySet());
        Collections.sort(truckCapacity, (i1, i2) -> i2.getValue().compareTo(i1.getValue()));
        System.out.println("---------------Trucks Ordered By Descending Capacity ---------------");
        for (int n = 0; n < model.getTrucksNumber(); n++) {
            System.out.println("Truck "+truckCapacity.get(n).getKey()+" has capacity "+truckCapacity.get(n).getValue());
        }//initialize TruckList Capacity and order desc

        for (int n = 0; n < model.getTrucksNumber(); n++) {
            trucksAvailableAtZeroTimeslot.add(truckCapacity.get(n).getKey());
            System.out.println("Truck picked is "+truckCapacity.get(n));
        }

    }//orderTrucksDescendingCapacity


    /**
     * Creates the tankCapacitylist with tanks and their capacity on descending order.
     */
    public void orderTanksDescendingCapacity() {
        for (int i = 0; i < model.getTanksNumber(); i++) {
            tankCapacityMap.put(i,model.getTankDemand()[i]);
//            tankCapacityMap.put(i,model.getTruckDeliveryTimeToDepot()[i]);

        }//initialize tankList Capacity and order desc
        tankCapacity = new ArrayList<>(tankCapacityMap.entrySet());
        Collections.sort(tankCapacity, (i1, i2) -> i2.getValue().compareTo(i1.getValue()));
        System.out.println("---------------Tanks Ordered By Descending Capacity ---------------");
        for (int i = 0; i < model.getTanksNumber(); i++) {
            System.out.println("Tank "+tankCapacity.get(i).getKey()+" has capacity "+tankCapacity.get(i).getValue());
        }//initialize tankList Capacity and order desc
    }//orderTrucksDescendingCapacity

    /**
     * Calculates the tankCumulativePropability list that puts in the list the tank 0. 1... and
     * its corresponding probability based on the total demand. ie smaller tanks will have less
     * probability to be chosen at first.
     */

    public void calculateTanksCumulativeProbabilities() {
        double cumulativeProbability = 0.0;
        //Calculate Tank Probabilities and then it adds them incrementally into tankCumulativeProbabilityList
        for (int i = 0; i <model.getTanksNumber(); i++) {
            //Division with a double and integer gives double, else if
            // i make the division of the two integers first and
            // then the cast to double the result is zero
            double tankProbability = ((double)model.getTankDemand()[i])/overallModelCapacity;
            cumulativeProbability += tankProbability;
            tanksCumulativeProbability.add(cumulativeProbability);
            System.out.println("Tank "+i+" pick propability "+tanksCumulativeProbability.get(i));
        }
    }//calculateTanksCumulativeProbabilities

    /**
     * It picks a tank based on random selection and on tank choose probability
     * @return the tank id
     */
    public int findBestTankFit() {
        //create a random number
        calculateTanksCumulativeProbabilities();
        double randomNumber = Math.random();
        OptionalInt selectedTankIndex = IntStream.range(0, tanksCumulativeProbability.size())
                .filter(i -> randomNumber < tanksCumulativeProbability.get(i))  // Compare randomNumber to the cumulative probability
                .findFirst();  // Get the first index that matches the condition
        return selectedTankIndex.orElse(0);
    }//findBestTankFit

    /**
     * Performs check if all tank capacities are covered
     * @return true if they are ALL FULL else false
     */
    public boolean allTanksFull() {
        for (int i = 0; i < model.getTanksNumber(); i++) {
            if (currentTankLevel[i] < model.getTankDemand()[i]) {
//                System.out.println("Tank is not full "+i+" and has current level"+currentTankLevel[i]+ " returning false");
                return false; //the tank i is not full
            }
        }
//        System.out.println("RETURNING TRUE");
        return true;
    }

}//class
