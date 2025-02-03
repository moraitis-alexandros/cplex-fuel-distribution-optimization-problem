
/**
 * It has all the fixed variables
 */
public class Model {

    private int tanksNumber;
    private int[] truckCapacity;
    private int[] tankDemand;
    private int totalOperationTime;
    private int[] truckDeliveryTimeToDepot;
    private int[] truckLoadingTime;
    private int maximumAvailableTrips;
    private double bigM;
    private int trucksNumber;

    public int getTanksNumber() {
        return tanksNumber;
    }

    public void setTanksNumber(int tanksNumber) {
        this.tanksNumber = tanksNumber;
    }

    public int getTrucksNumber() {
        return trucksNumber;
    }

    public void setTrucksNumber(int trucksNumber) {
        this.trucksNumber = trucksNumber;
    }

    public double getBigM() {
        return bigM;
    }

    public void setBigM(double bigM) {
        this.bigM = bigM;
    }

    public int[] getTruckCapacity() {
        return truckCapacity;
    }

    public void setTruckCapacity(int[] truckCapacity) {
        this.truckCapacity = truckCapacity;
    }

    public int[] getTankDemand() {
        return tankDemand;
    }

    public void setTankDemand(int[] tankDemand) {
        this.tankDemand = tankDemand;
    }

    public int[] getTruckDeliveryTimeToDepot() {
        return truckDeliveryTimeToDepot;
    }

    public void setTruckDeliveryTimeToDepot(int[] truckDeliveryTimeToDepot) {
        this.truckDeliveryTimeToDepot = truckDeliveryTimeToDepot;
    }

    public int getTotalOperationTime() {
        return totalOperationTime;
    }

    public void setTotalOperationTime(int totalOperationTime) {
        this.totalOperationTime = totalOperationTime;
    }

    public int[] getTruckLoadingTime() {
        return truckLoadingTime;
    }

    public void setTruckLoadingTime(int[] truckLoadingTime) {
        this.truckLoadingTime = truckLoadingTime;
    }

    public int getMaximumAvailableTrips() {
        return maximumAvailableTrips;
    }

    public void setMaximumAvailableTrips(int maximumAvailableTrips) {
        this.maximumAvailableTrips = maximumAvailableTrips;
    }


    public Model() {
        this.bigM = 9999999;
        this.trucksNumber = 2;//Trucks Number
        this.truckCapacity = new int[]{5000, 8000};//Trucks Capacity Qν
        this.tanksNumber = 2;//Tanks Number
        this.totalOperationTime = 100;// given period (in timeslots) on loading positions
        this.maximumAvailableTrips = 6;//We do not know in advance the trips that each
        //vehicle will do. However we should initialize the 3d variable array so we want to
        //know it in advance. As a result we define a rather large random Maximum possible
        //number. !!!Caution if the trips of any truck exceed this number i will
        //probably have index out of bounds exception.
        this.truckLoadingTime = new int[]{1, 2};//Assuming that each truck
        //has the same loading and unloading time
        //from depot to each tank is the same for all trucks.
        //We can change it by altering the table into a 2dimensional table
        this.truckDeliveryTimeToDepot = new int[]{1, 2};
        this.totalOperationTime = totalOperationTime;
        this.tankDemand = new int[]{8000, 16000};//Tanks Demand qi
    }

}//model class