import java.io.*;
import java.util.*;

public class Benchmark {

    private static final int NUM_TRIALS = 3;

    private static final int[] WORST_N_NAIVE = {1000, 5000, 10000, 30000, 50000, 100000};
    private static final int[] WORST_N_ALL   = {1000, 5000, 10000, 30000, 50000, 100000,
                                                 300000, 700000, 1000000};
    private static final int   WORST_FINDS   = 1000;

    private static final int[] BALANCED_N    = {1024, 4096, 16384, 65536, 262144, 1048576};
    private static final int   BALANCED_FINDS = 5000;

    private static final int[] RANDOM_N_NAIVE = {1000, 10000, 100000};
    private static final int[] RANDOM_N_ALL   = {1000, 10000, 100000, 500000, 1000000};

    public static void main(String[] args) throws IOException {
        System.err.println("=== DSU Benchmark ===");

        warmup();

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("benchmark_results.csv")));
        out.println("scenario,n,implementation,trial,time_us,total_ops,finds_count,ops_per_find");

        System.err.println("[1/3] Cenário: pior caso (cadeia linear)");
        runWorstCase(out);

        System.err.println("[2/3] Cenário: árvore balanceada");
        runBalanced(out);

        System.err.println("[3/3] Cenário: aleatório");
        runRandom(out);

        out.close();
        System.err.println("[OK]  Resultados gravados em benchmark_results.csv");
        System.err.println("      Execute: python generate_graphs.py");
    }

    private static void runWorstCase(PrintWriter out) throws IOException {
        for (int n : WORST_N_ALL) {
            System.err.printf("      n = %,d%n", n);
            boolean naive = contains(WORST_N_NAIVE, n);
            for (int t = 1; t <= NUM_TRIALS; t++) {
                gc();
                if (naive) record(out, "worst_case", n, "Naive",
                        measureWorstCase(new NaiveDSU(n), n, WORST_FINDS), WORST_FINDS, t);
                record(out, "worst_case", n, "UnionByRank",
                        measureWorstCase(new UnionByRankDSU(n), n, WORST_FINDS), WORST_FINDS, t);
                record(out, "worst_case", n, "FullTarjan",
                        measureWorstCase(new FullTarjanDSU(n), n, WORST_FINDS), WORST_FINDS, t);
                out.flush();
            }
        }
    }

    private static void runBalanced(PrintWriter out) throws IOException {
        for (int n : BALANCED_N) {
            System.err.printf("      n = %,d%n", n);
            for (int t = 1; t <= NUM_TRIALS; t++) {
                gc();
                record(out, "balanced", n, "Naive",
                        measureBalanced(new NaiveDSU(n), n, BALANCED_FINDS), BALANCED_FINDS, t);
                record(out, "balanced", n, "UnionByRank",
                        measureBalanced(new UnionByRankDSU(n), n, BALANCED_FINDS), BALANCED_FINDS, t);
                record(out, "balanced", n, "FullTarjan",
                        measureBalanced(new FullTarjanDSU(n), n, BALANCED_FINDS), BALANCED_FINDS, t);
                out.flush();
            }
        }
    }

    private static void runRandom(PrintWriter out) throws IOException {
        for (int n : RANDOM_N_ALL) {
            System.err.printf("      n = %,d%n", n);
            boolean naive = contains(RANDOM_N_NAIVE, n);
            int finds = Math.min(n, 50000);
            for (int t = 1; t <= NUM_TRIALS; t++) {
                gc();
                long seed = 42L + t;
                if (naive) record(out, "random", n, "Naive",
                        measureRandom(new NaiveDSU(n), n, seed, finds), finds, t);
                record(out, "random", n, "UnionByRank",
                        measureRandom(new UnionByRankDSU(n), n, seed, finds), finds, t);
                record(out, "random", n, "FullTarjan",
                        measureRandom(new FullTarjanDSU(n), n, seed, finds), finds, t);
                out.flush();
            }
        }
    }

    static Result measureWorstCase(DisjointSet dsu, int n, int numFinds) {
        for (int i = 0; i < n - 1; i++) dsu.union(i, i + 1);
        dsu.resetOperationCount();

        long t0 = System.nanoTime();
        for (int i = 0; i < numFinds; i++) dsu.find(0);
        long elapsed = System.nanoTime() - t0;

        return new Result(elapsed / 1000, dsu.getOperationCount());
    }

    static Result measureBalanced(DisjointSet dsu, int n, int numFinds) {
        for (int step = 1; step < n; step *= 2)
            for (int i = 0; i + step < n; i += 2 * step)
                dsu.union(i, i + step);
        dsu.resetOperationCount();

        long t0 = System.nanoTime();
        Random rand = new Random(42);
        for (int i = 0; i < numFinds; i++) dsu.find(rand.nextInt(n));
        long elapsed = System.nanoTime() - t0;

        return new Result(elapsed / 1000, dsu.getOperationCount());
    }

    static Result measureRandom(DisjointSet dsu, int n, long seed, int numFinds) {
        Random rand = new Random(seed);
        for (int i = 0; i < n / 2; i++) dsu.union(rand.nextInt(n), rand.nextInt(n));
        dsu.resetOperationCount();

        long t0 = System.nanoTime();
        for (int i = 0; i < numFinds; i++) dsu.find(rand.nextInt(n));
        long elapsed = System.nanoTime() - t0;

        return new Result(elapsed / 1000, dsu.getOperationCount());
    }

    static void record(PrintWriter out, String scenario, int n, String impl,
                       Result r, int finds, int trial) {
        double opsPerFind = finds > 0 ? (double) r.totalOps / finds : 0.0;
        out.printf(Locale.US, "%s,%d,%s,%d,%d,%d,%d,%.6f%n",
                scenario, n, impl, trial, r.timeUs, r.totalOps, finds, opsPerFind);
    }

    static boolean contains(int[] arr, int val) {
        for (int x : arr) if (x == val) return true;
        return false;
    }

    static void gc() { System.gc(); }

    static void warmup() {
        System.err.println("      (aquecimento JVM...)");
        for (int i = 0; i < 5; i++) {
            measureWorstCase(new NaiveDSU(500), 500, 200);
            measureWorstCase(new UnionByRankDSU(500), 500, 200);
            measureWorstCase(new FullTarjanDSU(500), 500, 200);
        }
        gc();
    }

    static class Result {
        final long timeUs;
        final long totalOps;
        Result(long timeUs, long totalOps) { this.timeUs = timeUs; this.totalOps = totalOps; }
    }
}
