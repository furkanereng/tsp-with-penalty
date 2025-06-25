/* CSE2246 - Project #2
 * Yasin Emre Çetin
 * Eren Emre Aycibin
 * Furkan Eren Gülçay
 * 
 * TwoDimensionalTree is a KD-Tree with the k = 2 and used for finding the nearest neighbors of a City.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class TwoDimensionalTree {
    /* Node definition for the tree. */
    private static class TreeNode {
        City city;
        TreeNode left, right;
        int minX, maxX, minY, maxY;  

        TreeNode(City city) {
            this.city = city;
            this.minX = this.maxX = city.x;
            this.minY = this.maxY = city.y;
        }
    }
    private TreeNode root;

    /* Constructs a 2D-Tree from the input list of cities. */
    public TwoDimensionalTree(ArrayList<City> cities) {
        root = constructTree(cities, true);
    }

    /* Constructs a 2D-Tree from the list of cities. */
    private TreeNode constructTree(List<City> cities, boolean compareByX) {
        if (cities == null || cities.isEmpty())
            return null;

        /* Sorts the cities according to x or y coordinates. */
        Collections.sort(cities, new Comparator<City>() {
            @Override
            public int compare(City c1, City c2) {
                if (compareByX)
                    return Integer.compare(c1.x, c2.x);
                else
                    return Integer.compare(c1.y, c2.y);
            }
        });

        /* Finds the middle city and puts it to the root of the subtree. */
        int indexOfMid = cities.size() / 2;
        City midCity = cities.get(indexOfMid);
        TreeNode rootOfSubtree = new TreeNode(midCity);

        /* Constructs the branches of the subtree recursively. */
        rootOfSubtree.left = constructTree(cities.subList(0, indexOfMid), !compareByX);
        rootOfSubtree.right = constructTree(cities.subList(indexOfMid + 1, cities.size()), !compareByX);

        /* Update the bounding boxes of the cities. */
        if (rootOfSubtree.left != null) {
            rootOfSubtree.minX = Math.min(rootOfSubtree.minX, rootOfSubtree.left.minX);
            rootOfSubtree.maxX = Math.max(rootOfSubtree.maxX, rootOfSubtree.left.maxX);
            rootOfSubtree.minY = Math.min(rootOfSubtree.minY, rootOfSubtree.left.minY);
            rootOfSubtree.maxY = Math.max(rootOfSubtree.maxY, rootOfSubtree.left.maxY);
        }
        if (rootOfSubtree.right != null) {
            rootOfSubtree.minX = Math.min(rootOfSubtree.minX, rootOfSubtree.right.minX);
            rootOfSubtree.maxX = Math.max(rootOfSubtree.maxX, rootOfSubtree.right.maxX);
            rootOfSubtree.minY = Math.min(rootOfSubtree.minY, rootOfSubtree.right.minY);
            rootOfSubtree.maxY = Math.max(rootOfSubtree.maxY, rootOfSubtree.right.maxY);
        }

        /* Returns the root of the tree at the end of the recursive calls. */
        return rootOfSubtree;
    }

    /* Initializes nearest neighbors array of the input city. */
    public void updateNearestNeighborsOf(City city, int k) {
        /* Setting up a maximum heap to find and add the nearest neighbors. */
        PriorityQueue<TreeNode> maxHeap = new PriorityQueue<>(k, new Comparator<TreeNode>() {
            @Override
            public int compare(TreeNode n1, TreeNode n2) {
                long d1 = city.getSquaredDistance(n1.city);
                long d2 = city.getSquaredDistance(n2.city);
                return Long.compare(d2, d1); 
            }
        });

        /* Traverses the tree and updates the heap content. */
        findKNearestNeighbors(root, city, k, true, maxHeap);

        /* Copies the heap content to the city's nearest neighbors array. The nearest city will be at the beginning. */
        city.nearestNeighbors = new City[k];
        for (int i = k - 1; i >= 0; i--)
            city.nearestNeighbors[i] = maxHeap.poll().city;
    }

    /* Returns the minimum squared distance. */
    private long getMinSquaredDistance(City target, TreeNode node) {
        if (node == null) 
            return Long.MAX_VALUE;
        long dx = Math.max(0, Math.max(target.x - node.maxX, node.minX - target.x));
        long dy = Math.max(0, Math.max(target.y - node.maxY, node.minY - target.y));
        return dx * dx + dy * dy;
    }

    /* Finds the nearest k cities of the input city and stores them in the input max-heap. */
    private void findKNearestNeighbors(TreeNode node, City target, int k, boolean compareByX, PriorityQueue<TreeNode> maxHeap) {
        if (node == null)
            return;
        
        /* Skip if the minimum possible distance is greater than current maximum. */
        if (!maxHeap.isEmpty() && getMinSquaredDistance(target, node) > target.getSquaredDistance(maxHeap.peek().city))
            return;

        /* Updates the heap content. */
        long dist = target.getSquaredDistance(node.city);
        if (!target.equals(node.city)) {
            /* If heap is not full, add the node. */
            if (maxHeap.size() < k)
                maxHeap.offer(node);
            /* If heap is full and the current node has a closer city, remove the farthest city and add current city. */
            else if (dist < target.getSquaredDistance(maxHeap.peek().city)) {
                maxHeap.poll();
                maxHeap.offer(node);
            }
        }

        /* Uses x or y coordinates. */
        int targetCoordinate, nodeCoordinate;
        if (compareByX) {
            targetCoordinate = target.x;
            nodeCoordinate = node.city.x;
        } else {
            targetCoordinate = target.y;
            nodeCoordinate = node.city.y;
        }

        /* Determining the first and second subtrees to check for the nearest neighbor. */
        TreeNode first, second;
        if (targetCoordinate < nodeCoordinate) {
            first = node.left;
            second = node.right;
        } else {
            first = node.right;
            second = node.left;
        }

        findKNearestNeighbors(first, target, k, !compareByX, maxHeap);

        /* Only check second branch if necessary. */
        if (maxHeap.size() < k || getMinSquaredDistance(target, second) < target.getSquaredDistance(maxHeap.peek().city))
            findKNearestNeighbors(second, target, k, !compareByX, maxHeap);
    }
} 
