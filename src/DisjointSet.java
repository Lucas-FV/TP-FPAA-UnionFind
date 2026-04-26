public interface DisjointSet {
    int find(int i);
    void union(int i, int j);

    default long getOperationCount() { return 0; }
    default void resetOperationCount() {}
}
