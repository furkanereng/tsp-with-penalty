/* CSE2246 - Project #2
 * Yasin Emre Çetin
 * Eren Emre Aycibin
 * Furkan Eren Gülçay
 * 
 * This is the main class that reads inputs, runs the algorithm from them and generates the outputs.
 */

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class TSPwP {
    static final String INPUTS[] = {"test-input-1.txt", "test-input-2.txt", "test-input-3.txt", "test-input-4.txt"};
    static final String OUTPUTS[] = {"test-output-1.txt", "test-output-2.txt", "test-output-3.txt", "test-output-4.txt"};
    static ArrayList<City> cities;

    public static void main(String args[]) {
        for (int i = 0; i < INPUTS.length; i++) {
            cities = new ArrayList<>();
            readInput(i);
            int numberOfCities = cities.size();
            ArrayList<City> tourForTSPwP = solveTSPwP(cities);
            writeOutput(i, numberOfCities, tourForTSPwP);
            System.out.println("Output - " + (i + 1) + " is generated.");
        }
    }

    /* Returns the found tour for the TSPwP problem. */
    public static ArrayList<City> solveTSPwP(ArrayList<City> cities) {
        if (cities == null || cities.isEmpty())
            return new ArrayList<>();
        
        /* Initializing the nearest neighbors of each cities. */
        TwoDimensionalTree citiesAsTree = new TwoDimensionalTree(cities);
        City.initializeNearestNeighbors(cities, citiesAsTree);

        /* Obtaining a simple TSP solution to use for TSPwP problem. */
        ArrayList<City> tour = solveTSP(cities);

        /* In order to optimize the run time. */
        final int MAX_ITERATIONS = 2000;

        /* Optimize the TSP tour with 2-opt changes. */
        boolean improved = true; int iterations = 0;
        while (improved && iterations < MAX_ITERATIONS) {
            improved = false;
            iterations++;
            
            int tourSize = tour.size();
            for (int i = 1; i < tourSize; i++) {
                City c1 = tour.get(i);

                for (int n = 0; n < c1.nearestNeighbors.length; n++) {
                    City c2 = c1.nearestNeighbors[n];
                    int j = tour.indexOf(c2);
                    if (j > i + 1 && j < tourSize) {
                        long swapCost = City.calculateNetCostForSwap(tour, i, j);
                        if (swapCost < 0) {
                            City.reverse(tour, i, j);
                            improved = true;
                            break;
                        }
                    }
                }

                if (improved) 
                    break;
            }
        }
        
        /* Tries to skipping a city and if it is a better choice, removes the city. */
        iterations = 0; improved = true;
        while (improved && iterations < MAX_ITERATIONS) {
            improved = false;
            iterations++;
            
            int tourSize = tour.size();
            if (tourSize <= 2) 
                break;
            
            /* Checks the whole tour, except the conditions of skipping first or last cities. */
            for (int i = 1; i < tourSize - 1; i++) {
                City prev = tour.get(i - 1);
                City curr = tour.get(i);
                City next = tour.get(i + 1);
                
                if (City.calculateNetCostForSkip(prev, curr, next) < 0) {
                    tour.remove(i);
                    improved = true;
                    break;
                }
            }
            
            /* Checks that what happens if we decide to skip the first city. */
            if (!improved && tourSize > 2) {
                City last = tour.get(tourSize - 1);
                City first = tour.get(0);
                City second = tour.get(1);
                
                if (City.calculateNetCostForSkip(last, first, second) < 0) {
                    tour.remove(0);
                    improved = true;
                }
            }
            
            /* Checks that what happens if we decide to skip the last city. */
            if (!improved && tourSize > 2) {
                City secondLast = tour.get(tourSize - 2);
                City last = tour.get(tourSize - 1);
                City first = tour.get(0);
                
                if (City.calculateNetCostForSkip(secondLast, last, first) < 0) {
                    tour.remove(tourSize - 1);
                    improved = true;
                }
            }
        }

        return tour;
    }

    /* Solves the TSP with the nearest neighbor approach. */
    public static ArrayList<City> solveTSP(ArrayList<City> cities) {
        HashSet<City> visited = new HashSet<>();
        ArrayList<City> tour = new ArrayList<>();

        /* Starting the tour with a selected city. */
        City current = cities.get(0);
        tour.add(current);
        visited.add(current);

        /* Constructing the TSP tour. */
        while (tour.size() < cities.size()) {
            City next = null;

            /* Finds the nearest unvisited city from the nearest neighbors. */
            for (City neighbor : current.nearestNeighbors) {
                if (!visited.contains(neighbor)) {
                    next = neighbor;
                    break;
                }
            }

            /* If the nearest neighbors do not contain any unvisited city, checks with a brute-force way. */
            if (next == null) {
                long minDistance = Long.MAX_VALUE;
                for (City c : cities) {
                    if (!visited.contains(c)) {
                        long distance = current.getSquaredDistance(c);
                        if (distance < minDistance) {
                            minDistance = distance;
                            next = c;
                        }
                    }
                }
            }

            /* Add the nearest unvisited city to the tour. */
            tour.add(next);
            visited.add(next);
            current = next;
        }

        return tour;
    }

    /* Reads the input file & initializes the penalty value and the cities. */
    public static void readInput(int i) {
        try {
            Scanner scanner = new Scanner(new File(INPUTS[i]));
            City.penalty = Integer.parseInt(scanner.nextLine().trim());
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().trim().split(" +");
                int id = Integer.parseInt(line[0]);
                int x = Integer.parseInt(line[1]);
                int y = Integer.parseInt(line[2]);
                cities.add(new City(id, x, y));
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("An error about input file " + INPUTS[i] + " is detected.");
            System.exit(1);
        }
    }

    /* Writes the output file. */
    public static void writeOutput(int i, int numberOfCities, ArrayList<City> tourForTSPwP) {
        try {
            PrintWriter output = new PrintWriter(OUTPUTS[i]);
            output.println((long)(City.getTourLength(tourForTSPwP) + (numberOfCities - tourForTSPwP.size()) * City.penalty) + " " + tourForTSPwP.size()); 
            for (City city : tourForTSPwP)
                output.println(city.id);
            output.close();
        } catch (Exception e) {
            System.out.println("An error about output file " + OUTPUTS[i] + " is detected.");
            System.exit(1);
        }
    }
}
