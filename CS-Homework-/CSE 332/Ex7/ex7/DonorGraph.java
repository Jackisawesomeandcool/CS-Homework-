import java.util.*;

public class DonorGraph {
    public List<List<Match>> adjList;

    // The donatingTo array indicates which repient each donor is
    // affiliated with. Specifically, the donor at index i has volunteered
    // to donate a kidney on behalf of recipient donatingTo[i].
    // The matchScores 2d array gives the match scores associated with each
    // donor-recipient pair. Specificically, matchScores[x][y] gives the
    // HLA score for donor x and reciplient y.
    
    
    public DonorGraph(int[] donorToBenefit, int[][] matchScores) {
    adjList = new ArrayList<>();
    int numRecipients = matchScores[0].length;

    // initialize adjacency list for each beneficiary
    for (int i = 0; i < numRecipients; i++) {
        adjList.add(new ArrayList<>());
    }

    for (int i = 0; i < donorToBenefit.length; i++) {
        int beneficiary = donorToBenefit[i];

        for (int j = 0; j < numRecipients; j++) {
            if (matchScores[i][j] >= 60) {
                // store as Match object
                adjList.get(beneficiary).add(new Match(i, beneficiary, j));
            }
        }
    }

    }

    // Will be used by the autograder to verify your graph's structure.
    // It's probably also going to helpful for your debugging.
    public boolean isAdjacent(int start, int end) {
        for (Match m : adjList.get(start)) {
            if (m.recipient == end)
                return true;
        }
        return false;
    }

    // Will be used by the autograder to verify your graph's structure.
    // It's probably also going to helpful for your debugging.
    public int getDonor(int beneficiary, int recipient) {
        for (Match m : adjList.get(beneficiary)) {
            if (m.recipient == recipient)
                return m.donor;
        }
        return -1;
    }


    // returns a chain of matches to make a donor cycle
    // which includes the given recipient.
    // Returns an empty list if no cycle exists.
    public List<Match> findCycle(int recipient) {
         boolean[] visited = new boolean[adjList.size()]; // nodes in recursion stack
        boolean[] done = new boolean[adjList.size()];    // fully explored nodes
        List<Match> path = new ArrayList<>();

        if (dfsHelper(recipient, recipient, visited, done, path)) {
            return path; // cycle found
        }

        return new ArrayList<>(); // no cycle
    }
    

    // returns true or false to indicate whether there
    // is some cycle which includes the given recipient.
    public boolean hasCycle(int recipient) {
         return !findCycle(recipient).isEmpty();
    }


        // Returns true if a cycle  path is stored in 'path'
    private boolean dfsHelper(int start, int current, boolean[] visited, boolean[] done, List<Match> path) {
        visited[current] = true;  // mark node in recursion stack

        for (Match m : adjList.get(current)) {
            int next = m.recipient;

            if (next == start) {          // cycle completed
                path.add(m);
                return true;
            }

            if (!visited[next] && !done[next]) {
                path.add(m);              // add edge to path
                if (dfsHelper(start, next, visited, done, path)) return true;
                path.remove(path.size() - 1);  // backtrack
            }
        }

        visited[current] = false;  // remove from  stack
        done[current] = true;      // mark as explored
        return false;
    }
}
