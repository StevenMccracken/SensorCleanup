import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Solves an applied version of the metric travelling salesman
 * problem using the nearest neighbor algorithm.
 * @author Steven McCracken
 */
public class SensorCleanup {
    public static ArrayList<Sensor> sensors, visited;
    public static long t1, t2;

    public static void main(String[] args) {
        if(args == null || args.length == 0) {
            System.out.println("Must provide a path for the input file in the arguments");
            System.exit(-1);
        }
        String input_file = args[0];

        sensors = new ArrayList<>();
        visited = new ArrayList<>();

        t1 = System.nanoTime();
        getInput(input_file); // Populate sensors arraylist from input file
        t2 = System.nanoTime();
        duration(t1, t2, "reading input");

        t1 = System.nanoTime();
        sensors.sort((a,b) -> a.compareTo(b)); // Sort the sensors based on the first coordinate
        t2 = System.nanoTime();
        duration(t1, t2, "sorting sensors");

        Random r = new Random();
        int curr_index = r.nextInt(sensors.size());
        Sensor curr_sensor = sensors.get(curr_index); // Pick a random sensor

        // Add the random sensor to the path
        curr_sensor.visit();
        visited.add(curr_sensor);

        t1 = System.nanoTime();
        
        // Make sure to visit all sensors
        while(visited.size() != sensors.size()) {
            // Get the closest sensor to curr_sensor
            Object[] updates = getClosestSensor(curr_sensor, curr_index);
            curr_sensor = (Sensor)updates[0];
            curr_index = (int)updates[1];

            // Visit that sensor and add it to the path
            curr_sensor.visit();
            visited.add(curr_sensor);
        }
        t2 = System.nanoTime();
        duration(t1, t2, "finding shortest path");

        t1 = System.nanoTime();
        writeToFile(visited);
        t2 = System.nanoTime();
        duration(t1, t2, "writing to file");
    }

    /**
     * Computes the Euclidean distance between two m-dimensional sensors
     * @param a the first sensor
     * @param b the second sensor
     * @return the euclidean distance between p and q
     */
    public static double distance(Sensor a, Sensor b) {
        double d = 0.0;
        for(int i = 0; i < a.coords.length; i++)
            d += Math.pow(a.coords[i] - b.coords[i], 2);
        return Math.sqrt(d);
    }

    /**
     * Finds the closest sensor to the input sensor based on their euclidean distance
     * @param source_sensor the sensor of interest
     * @param source_index the index of source in the static sensors arraylist
     * @return a tuple containing the closest sensor and it's index in the sensors arraylist
     */
    public static Object[] getClosestSensor(Sensor source_sensor, int source_index) {
        Sensor closest_sensor = null;
        int closest_sensor_index = -1;
        double min_distance = Double.MAX_VALUE;

        /*
        Move backwards from the source index until the difference between
        the x coordinates are greater than minimum distance found
         */
        for(int i = source_index - 1; i >= 0; i--) {
            Sensor neighbor = sensors.get(i);
            if(neighbor.visited) continue;

            double distance = distance(source_sensor, neighbor);
            if(distance < min_distance) {
                closest_sensor_index = i;
                min_distance = distance;
                closest_sensor = neighbor;
            }

            double delta_x = Math.abs(source_sensor.coords[0] - neighbor.coords[0]);
            if(delta_x > min_distance) break; // The closest sensor has been found in this direction
        }

        /*
        Move forwards from the source index until the difference between
        the x coordinates are greater than minimum distance found
         */
        for(int i = source_index + 1; i < sensors.size(); i++) {
            Sensor neighbor = sensors.get(i);
            if(neighbor.visited) continue;

            double distance = distance(source_sensor, neighbor);
            if(distance < min_distance) {
                closest_sensor_index = i;
                min_distance = distance;
                closest_sensor = neighbor;
            }

            double delta_x = Math.abs(source_sensor.coords[0] - neighbor.coords[0]);
            if(delta_x > min_distance) break; // The closest sensor has been found in this direction
        }

        // Wrap up the closest sensor object & it's index in the sensors array into this Object array
        return new Object[] {closest_sensor, closest_sensor_index};
    }

    /**
     * Gets sensor coordinates from input file and creates sensor objects
     */
    public static void getInput(String input_file_path) {
        try (Stream<String> stream = Files.lines(Paths.get(input_file_path))) {
            stream.forEach(line -> {
                // Split the input line string on each comma into an array. Turn the string array into a double array
                double[] coordinates = Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray();
                sensors.add(new Sensor(coordinates));
            });
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            System.out.printf("Invalid input file! - %s%n", input_file_path);
            System.exit(-1);
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Invalid data in input file! Every line must be of the form (number),(number),(number),(number)");
            System.exit(-1);
        }
    }

    /**
     * Writes the input arraylist to a file
     * @param path the answer to the problem containing the shortest path between all sensors
     */
    public static void writeToFile(ArrayList<Sensor> path) {
        try(FileWriter fw = new FileWriter("path.txt", false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            for(Sensor sensor : path) out.println(sensor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function to help benchmark execution time of arbitrary sections of code
     * @param t1 the start time
     * @param t2 the end time
     * @param message the purpose of the benchmark
     */
    public static void duration(long t1, long t2, String message) {
        double d = ((double)t2 - t1) / 1000000000;
        System.out.printf("Duration for %s: %.5f seconds%n", message, d);
    }
}