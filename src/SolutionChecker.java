import java.util.ArrayList;
import java.util.List;

/**
 * Checks all timeslots provided by the refinedTruckSolution for violations
 */
public class SolutionChecker {
    private static final String LOADING = "L";
    private static final String DEPARTURE_PREFIX = "D";
    private static final String UNLOADING_PREFIX = "U";

    private String[][] solution;
    private Model model;
    private int timeslotLength;
    private int[] currentTankLevel;

    public boolean isNotGood() {
        return notGood;
    }

    public void setNotGood(boolean notGood) {
        this.notGood = notGood;
    }

    private boolean notGood;

    public SolutionChecker(Model model, String[][] solution) {
        this.model = model;
        this.solution = solution;
        this.timeslotLength = this.solution[0].length;
        this.currentTankLevel = new int[model.getTanksNumber()];
    }

    public void solutionCheck() {
        System.out.println("**********");
        System.out.println("Checking for violations...");
        boolean violationsFound = false;

        //initialize current Tank Level
        for (int i = 0; i < model.getTanksNumber(); i++) {
            currentTankLevel[i] = 0;
        }

        for (int t = 0; t < this.timeslotLength; t++) {
            int loadingCounter = 0;
            List<String> seenUnloading = new ArrayList<>();

            for (int n = 0; n < model.getTrucksNumber(); n++) {
                String currentSlot = this.solution[n][t];

                // Check for loading violations
                if (LOADING.equals(currentSlot)) {
                    loadingCounter++;
                }
//
//                // Check for unloading violations
//                if (currentSlot.startsWith(UNLOADING_PREFIX)) {
//                    if (seenUnloading.contains(currentSlot)) {
//                        violationsFound = true;
//                        System.out.println("Duplicate unloading detected on Timeslot: " + t);
//                    } else {
//                        seenUnloading.add(currentSlot);
//                    }
//                }
            }

            if (loadingCounter > 1) {
                violationsFound = true;
                System.out.println("Violation of loading found on Timeslot: " + t);
            }
        }

        System.out.println("**********");
        System.out.println("Checking for demand compliance...");

        int totalDemand = 0;
        boolean demandViolationsFound = false;
        notGood = false;

        // Loop through each timeslot to calculate total demand
        for (int u = 0; u < this.timeslotLength; u++) {
            for (int n = 0; n < model.getTrucksNumber(); n++) {
                String currentSlot = this.solution[n][u];

                // Check for unloading demands
                if (currentSlot.startsWith(DEPARTURE_PREFIX)) {
                    String depotId = currentSlot.substring(1); // Extract depot identifier

                    currentTankLevel[Integer.parseInt(depotId)] += model.getTruckCapacity()[n];
                    ; // Assume this method retrieves demand for the depot
                    // Accumulate total demand from all depots
                    totalDemand += model.getTruckCapacity()[n];
                }
            }
        }

                for (int i = 0; i < model.getTanksNumber(); i++) {
                    if (currentTankLevel[i] < model.getTankDemand()[i]) {
                        demandViolationsFound = true;
                        System.out.println("Tank "+i+" current capacity " + currentTankLevel[i] + " is less than demand " + model.getTankDemand()[i]);
                    }
                }

              for (int i = 0; i < model.getTanksNumber(); i++) {
                    if (currentTankLevel[i] > model.getTankDemand()[i]) {
                        System.out.println("Tank "+i+" current capacity " + currentTankLevel[i] + " is more than demand " + model.getTankDemand()[i]);
                    }
                }





            if (!demandViolationsFound) {
                System.out.println("Demand compliance check OK. No violations found.");
            }


            if (!violationsFound && !demandViolationsFound) {
                System.out.println("Solution OK. No violations found on Loading/Unloading.");
            } else {
                System.out.println("Found Errors in the process...");
                notGood = true;
            }

        }
}//solution checker class