public class UnionByRankDSU implements DisjointSet {
    private int[] parent;
    private int[] rank;

    public UnionByRankDSU(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0; // Todas as árvores começam com altura 0
        }
    }

    @Override
    public int find(int i) {
        // O find continua igual ao Naive (ainda não tem a compressão do Tarjan)
        while (parent[i] != i) {
            i = parent[i];
        }
        return i;
    }

    @Override
    public void union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);

        if (rootI != rootJ) {
            // Árvore menor vai para debaixo da maior
            if (rank[rootI] < rank[rootJ]) {
                parent[rootI] = rootJ;
            } else if (rank[rootI] > rank[rootJ]) {
                parent[rootJ] = rootI;
            } else {
                // Se têm a mesma altura, escolhemos uma e aumentamos sua altura em 1
                parent[rootI] = rootJ;
                rank[rootJ]++;
            }
        }
    }
}