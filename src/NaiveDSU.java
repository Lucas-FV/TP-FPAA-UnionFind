public class NaiveDSU implements DisjointSet {
    private int[] parent;

    public NaiveDSU(int n) {
        parent = new int[n];
        // Inicialmente, cada elemento é seu próprio chefe
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    @Override
    public int find(int i) {
        // Enquanto o elemento não for chefe de si mesmo, continue subindo
        while (parent[i] != i) {
            i = parent[i];
        }
        return i; // Retorna a raiz encontrada
    }

    @Override
    public void union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);

        // Se eles já têm a mesma raiz, não faz nada
        if (rootI != rootJ) {
            // Liga a raiz de I na raiz de J de forma arbitrária
            parent[rootI] = rootJ; 
        }
    }
}