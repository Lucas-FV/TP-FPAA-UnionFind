public interface DisjointSet {
    /**
     * Retorna o representante (ou "pai") do conjunto ao qual o elemento i pertence.
     */
    int find(int i);

    /**
     * Conecta os conjuntos que contêm i e j, mesclando-os em um único subconjunto.
     */
    void union(int i, int j);
}