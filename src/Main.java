import java.util.Random;

public class Main {

    public static void main(String[] args) {
        // Tamanhos dos conjuntos de dados (o 'n' crescendo para os gráficos)
        int[] nValues = {10000, 20000, 30000, 40000, 50000};
        int numFinds = 100000; // Quantidade fixa de buscas para estressar o algoritmo

        System.out.println("Iniciando bateria de testes...\n");
        
        // Cabeçalho no formato CSV para facilitar a geração de gráficos depois
        System.out.println("N_Elementos,Tipo_DSU,Tempo_ms");

        for (int n : nValues) {
            // Teste 1: Versão Ingênua (Naive)
            DisjointSet naive = new NaiveDSU(n);
            long timeNaive = runWorstCaseExperiment(naive, n, numFinds);
            System.out.println(n + ",Naive," + timeNaive);

            // Teste 2: Versão com Rank
            DisjointSet rank = new UnionByRankDSU(n);
            long timeRank = runWorstCaseExperiment(rank, n, numFinds);
            System.out.println(n + ",UnionByRank," + timeRank);

            // TODO: Teste 3 - Full Tarjan entrará aqui depois
            // DisjointSet tarjan = new FullTarjanDSU(n);
            // long timeTarjan = runWorstCaseExperiment(tarjan, n, numFinds);
            // System.out.println(n + ",FullTarjan," + timeTarjan);
        }
    }

    /**
     * Executa o experimento forçando o pior caso para o Union-Find.
     */
    private static long runWorstCaseExperiment(DisjointSet dsu, int n, int numFinds) {
        long startTime = System.currentTimeMillis();

        // 1. Fase de Construção (Pior Caso)
        // Conecta todo mundo em uma linha reta: 0 -> 1 -> 2 -> ... -> n-1
        for (int i = 0; i < n - 1; i++) {
            dsu.union(i, i + 1);
        }

        // 2. Fase de Busca (Estresse)
        // Sorteamos números aleatórios para buscar. 
        // Na versão Ingênua, buscar o chefe do elemento 0 vai percorrer todos os 'n' elementos.
        Random rand = new Random(42); // Usamos uma semente fixa (42) para que o teste seja justo e repetível
        for (int i = 0; i < numFinds; i++) {
            int elementToFind = rand.nextInt(n);
            dsu.find(elementToFind);
        }

        long endTime = System.currentTimeMillis();
        
        // Retorna a duração total em milissegundos
        return endTime - startTime;
    }
}