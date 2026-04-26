public class NaiveDSU implements DisjointSet {
    private int[] parent;
    private long operationCount = 0;

    public NaiveDSU(int n) {
        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    @Override
    public int find(int i) {
        while (parent[i] != i) {
            operationCount++;
            i = parent[i];
        }
        return i;
    }

    @Override
    public void union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);
        if (rootI != rootJ) {
            parent[rootI] = rootJ;
        }
    }

    @Override public long getOperationCount() { return operationCount; }
    @Override public void resetOperationCount() { operationCount = 0; }
}
