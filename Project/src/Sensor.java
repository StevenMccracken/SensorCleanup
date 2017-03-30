/**
 * Class representing a "sensor" in 4-dimensional space
 * @author Steven McCracken
 */
class Sensor {
    public double[] coords;
    public boolean visited;

    /**
     * Constructor for the Sensor class
     * @param coords the m-coordinates for the sensor
     */
    public Sensor(double[] coords) {
        this.coords = coords;
        this.visited = false;
    }

    /**
     * Marks the sensor as visited
     */
    public void visit() { this.visited = true; }


    @Override
    public String toString() {
        return coords[0] + "," + coords[1] + "," + coords[2] + "," + coords[3];
    }

    /**
     * Compares this sensor to the input sensor based on their first coordinate
     * @param p the sensor to compare this sensor to
     * @return an int that indicates this is smaller than, equal to, or greater than p
     */
    public int compareTo(Sensor p) {
        if(this.coords[0] > p.coords[0]) return 1;
        else if(this.coords[0] < p.coords[0]) return -1;
        else return 0;
    }
}