import java.util.Random;

public class Main {

    public static void main(String[] args) {
        int[] nValues = {10000, 20000, 30000, 40000, 50000};
        int numFinds = 100000;

        System.out.println("Iniciando bateria de testes...\n");
        System.out.println("N_Elementos,Tipo_DSU,Tempo_ms");

        for (int n : nValues) {
            DisjointSet naive = new NaiveDSU(n);
            long timeNaive = runWorstCaseExperiment(naive, n, numFinds);
            System.out.println(n + ",Naive," + timeNaive);

            DisjointSet rank = new UnionByRankDSU(n);
            long timeRank = runWorstCaseExperiment(rank, n, numFinds);
            System.out.println(n + ",UnionByRank," + timeRank);

            DisjointSet tarjan = new FullTarjanDSU(n);
            long timeTarjan = runWorstCaseExperiment(tarjan, n, numFinds);
            System.out.println(n + ",FullTarjan," + timeTarjan);
        }
    }

    private static long runWorstCaseExperiment(DisjointSet dsu, int n, int numFinds) {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < n - 1; i++) {
            dsu.union(i, i + 1);
        }

        Random rand = new Random(42);
        for (int i = 0; i < numFinds; i++) {
            int elementToFind = rand.nextInt(n);
            dsu.find(elementToFind);
        }

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}
