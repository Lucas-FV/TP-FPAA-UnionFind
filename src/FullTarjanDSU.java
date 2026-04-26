public class FullTarjanDSU implements DisjointSet {
    private int[] parent;
    private int[] rank;
    private long operationCount = 0;

    public FullTarjanDSU(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    @Override
    public int find(int i) {
        if (parent[i] != i) {
            operationCount++;
            parent[i] = find(parent[i]);
        }
        return parent[i];
    }

    @Override
    public void union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);
        if (rootI != rootJ) {
            if (rank[rootI] < rank[rootJ]) {
                parent[rootI] = rootJ;
            } else if (rank[rootI] > rank[rootJ]) {
                parent[rootJ] = rootI;
            } else {
                parent[rootI] = rootJ;
                rank[rootJ]++;
            }
        }
    }

    @Override public long getOperationCount() { return operationCount; }
    @Override public void resetOperationCount() { operationCount = 0; }
}
