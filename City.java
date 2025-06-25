/* CSE2246 - Project #2
 * Yasin Emre Çetin
 * Eren Emre Aycibin
 * Furkan Eren Gülçay
 * 
 * This class stores the unique ID of the city, the coordinates of that city and a penalty value that is the same for each city.
 * This class includes distance calculations and initializing the nearest neighbors with a 2D-Tree.
 */

import java.util.ArrayList;
import java.util.Collections;

public class City {
    public int id, x, y;
    public City nearestNeighbors[];
    public static int penalty;

    public City(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    /* Finds at most k = 30 neighbors of each cities. */
    public static void initializeNearestNeighbors(ArrayList<City> cities, TwoDimensionalTree citiesAsTree) {
        int k = Math.min(30, cities.size() - 1);
        for (City city : cities)
            citiesAsTree.updateNearestNeighborsOf(city, k);
    }

    /* Calculates the rounded Euclidian distance between this city and the input city. */
    public long getDistance(City city) {
        long dx = this.x - city.x;
        long dy = this.y - city.y;
        return (long)Math.round(Math.sqrt(dx * dx + dy * dy));
    }

    /* Calculates the square of the distance in order to do faster comparisons. */
    public long getSquaredDistance(City city) {
        long dx = this.x - city.x;
        long dy = this.y - city.y;
        return dx * dx + dy * dy;
    }

    /* Returns the tour length of the input tour. */
    public static long getTourLength(ArrayList<City> tour) {
        long length = 0;
        for (int i = 0; i < tour.size() - 1; i++)
            length += tour.get(i).getDistance(tour.get(i + 1));
        length += tour.get(tour.size() - 1).getDistance(tour.get(0));
        return length;
    }

    /* Calculates the net cost that will be obtained as a result of changing the locations of the cities at indices i and j.
     * Net Cost > 0: Tour length will become greater, which is unwanted.
     * Net Cost < 0: Tour length will become smaller, which shows that the swap can be performed.
     */
    public static long calculateNetCostForSwap(ArrayList<City> tour, int i, int j) {
        City c1 = tour.get(i - 1);
        City c2 = tour.get(i);
        City c3 = tour.get(j);
        City c4 = tour.get((j + 1) % tour.size());

        /* Cost calculations. */
        long costBeforeSwap = c1.getDistance(c2) + c3.getDistance(c4);
        long costAfterSwap = c1.getDistance(c3) + c2.getDistance(c4);

        /* Returns the net cost obtained from the swap. */
        return costAfterSwap - costBeforeSwap;
    }

    /* Calculates the net cost for skipping the current city.
     * Net Cost > 0: (Tour length + Penalties) will become greater, which is unwanted.
     * Net Cost < 0: (Tour length + Penalties) will become smaller, which shows that skipping the current city is a better choice.
     */
    public static long calculateNetCostForSkip(City prev, City curr, City next) {
        long costBeforeSkip = prev.getDistance(curr) + curr.getDistance(next);
        long costAfterSkip = prev.getDistance(next) + penalty;
        return costAfterSkip - costBeforeSkip;
    }

    /* Reverses the tour in the range [i, j] indices. */
    public static void reverse(ArrayList<City> tour, int i, int j) {
        while (i < j) {
            Collections.swap(tour, i, j);
            i++;
            j--;
        }
    }

    /* Checks that is the input city is the same city with this city, via using unique ID value. */
    @Override
    public boolean equals(Object city) {
        return this.id == ((City)city).id;
    }
}
