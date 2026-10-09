import java.util.*;

public class Clusterer {
    private List<List<WeightedEdge<Integer, Double>>> adjList; // the adjacency list of the original graph
    private List<List<WeightedEdge<Integer, Double>>> mstAdjList; // the adjacency list of the minimum spanning tree
    private List<List<Integer>> clusters; // a list of k points, each representing one of the clusters.
    private double cost; // the distance between the closest pair of clusters

    public Clusterer(double[][] distances, int k) {
        int n = distances.length;

        adjList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    adjList.get(i).add(new WeightedEdge<>(i, j, distances[i][j]));
                }
            }
        }

        mstAdjList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            mstAdjList.add(new ArrayList<>());
        }

        prims(0);
        makeKCluster(k);
    }

// Implements Prim's algorithm to compute a minimum spanning tree
// starting from the given node. The resulting MST is stored in mstAdjList.
    private void prims(int start) {
    int n = adjList.size();
        // dist[v] = smallest edge weight connecting v to the current MST
    double[] dist = new double[n];
        
        // prev[v] = parent of v in the MST
    int[] prev = new int[n];
        // seen[v] = whether v has been discovered
    boolean[] seen = new boolean[n];
        // done[v] = whether v has already been added to the MST
    boolean[] done = new boolean[n];

    Arrays.fill(dist, Double.MAX_VALUE);
    Arrays.fill(prev, -1);

    // Distance to start = 0, add start to PQ with priority 0, mark start as "seen"
    dist[start] = 0;
    seen[start] = true;

    PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingDouble(v -> dist[v]));
    pq.add(start);

    // Priority queue used to pick the next vertex with the smallest distance
    while (!pq.isEmpty()) {

        
        // Remove vertex with smallest distance from the priority queue
        int curr = pq.poll();

        // mark curr as "done"
        done[curr] = true;


        // add the edge (prev[curr], curr) to the spanning tree
        if (prev[curr] != -1) {
            mstAdjList.get(prev[curr]).add(new WeightedEdge<>(prev[curr], curr, dist[curr]));
            mstAdjList.get(curr).add(new WeightedEdge<>(curr, prev[curr], dist[curr]));
        }

        // for each neighbor v of curr
        for (WeightedEdge<Integer, Double> edge : adjList.get(curr)) {
            int v = edge.destination;
            double d = edge.weight;

            if (!seen[v]) {
                // mark v as "seen", set distance, set previous, add to PQ
                seen[v] = true;
                dist[v] = d;
                prev[v] = curr;
                pq.add(v);
            } else if (!done[v] && d < dist[v]) {
                // if v is not "done" && d < distance to v: update and decreaseKey
                dist[v] = d;
                prev[v] = curr;
                // Simulate decreaseKey: remove and re-add with updated priority
                pq.remove(v);
                pq.add(v);
            }
        }
    }
}


    // Removes the k-1 largest edges from the MST and then
    // uses BFS to find the connected components (clusters).    
    private void makeKCluster(int k) {
       int n = mstAdjList.size();
        // Collect all unique edges from the MST (avoid duplicates)
        List<WeightedEdge<Integer, Double>> allEdges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (WeightedEdge<Integer, Double> edge : mstAdjList.get(i)) {
                if (edge.source < edge.destination) {   // was edge.getSrc() / edge.getDest()
                    allEdges.add(edge);
                }
            }
        }

        // Sort descending by weight — heaviest edges first
        allEdges.sort((a, b) -> Double.compare(b.weight, a.weight));  // was .getWeight()

        // Remove the k-1 heaviest edges from the MST
        Set<String> removedEdges = new HashSet<>();
        for (int i = 0; i < k - 1; i++) {
            WeightedEdge<Integer, Double> e = allEdges.get(i);
            removedEdges.add(e.source + "," + e.destination);          // was getSrc/getDest
            removedEdges.add(e.destination + "," + e.source);
        }
        cost = allEdges.get(k - 2).weight;   // was .getWeight()

       // Use BFS to find connected components in the pruned MST
        int[] clusterID = new int[n];
        Arrays.fill(clusterID, -1);
        int numClusters = 0;

        for (int i = 0; i < n; i++) {
            if (clusterID[i] != -1) continue;

            Queue<Integer> queue = new LinkedList<>();
            queue.add(i);
            clusterID[i] = numClusters;

            while (!queue.isEmpty()) {
                int node = queue.poll();
                for (WeightedEdge<Integer, Double> edge : mstAdjList.get(node)) {
                    int neighbor = edge.destination;    // was edge.getDest()
                    String key = node + "," + neighbor;
                    if (clusterID[neighbor] == -1 && !removedEdges.contains(key)) {
                        clusterID[neighbor] = numClusters;
                        queue.add(neighbor);
                    }
                }
            }
            numClusters++;
        }

        // Build clusters list from clusterID assignments
        clusters = new ArrayList<>();
        for (int i = 0; i < numClusters; i++) {
            clusters.add(new ArrayList<>());
        }
        for (int i = 0; i < n; i++) {
            clusters.get(clusterID[i]).add(i);
        }
    }



    

    public List<List<Integer>> getClusters() {
        return clusters;
    }

    public double getCost() {
        return cost;
    }

}
