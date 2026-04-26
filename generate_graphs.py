import sys, os

try:
    import pandas as pd
    import matplotlib.pyplot as plt
    import matplotlib.ticker as mticker
    import numpy as np
except ImportError as e:
    print(f"Pacote ausente: {e}")
    print("Instale com:  pip install pandas matplotlib numpy")
    sys.exit(1)

CORES = {
    "Naive":       "#e74c3c",
    "UnionByRank": "#e67e22",
    "FullTarjan":  "#27ae60",
}
ROTULOS = {
    "Naive":       "Naive  —  O(n)",
    "UnionByRank": "Union by Rank  —  O(log n)",
    "FullTarjan":  "Full Tarjan  —  O(α(n))",
}
MARCADORES = {"Naive": "o", "UnionByRank": "s", "FullTarjan": "^"}
IMPLS = ["Naive", "UnionByRank", "FullTarjan"]

plt.rcParams.update({
    "font.family":        "DejaVu Sans",
    "axes.spines.top":    False,
    "axes.spines.right":  False,
    "axes.grid":          True,
    "grid.alpha":         0.3,
    "figure.dpi":         130,
})

def carregar(path):
    df = pd.read_csv(path)
    df["time_ms"] = df["time_us"] / 1000.0
    agg = (df.groupby(["scenario", "n", "implementation"])
             .agg(time_ms=("time_ms", "mean"),
                  ops_per_find=("ops_per_find", "mean"))
             .reset_index())
    return agg

def linha(ax, df, cenario, impl, metrica):
    d = (df[(df["scenario"] == cenario) & (df["implementation"] == impl)]
         .sort_values("n"))
    if d.empty:
        return
    ax.plot(d["n"], d[metrica],
            color=CORES[impl], label=ROTULOS[impl],
            marker=MARCADORES[impl], linewidth=2, markersize=6, zorder=3)

def curvas_teoricas(ax, df, cenario, metrica):
    d = df[df["scenario"] == cenario]
    if d.empty or metrica != "ops_per_find":
        return
    ns = np.array(sorted(d["n"].unique()), dtype=float)
    ns_plot = np.linspace(ns.min(), ns.max(), 400)

    if cenario == "worst_case":
        nd = d[d["implementation"] == "Naive"].sort_values("n")
        if not nd.empty:
            n0, y0 = nd["n"].iloc[0], nd["ops_per_find"].iloc[0]
            if y0 > 0:
                C = y0 / n0
                ax.plot(ns_plot, C * ns_plot, "--", color="#c0392b", alpha=0.45,
                        linewidth=1.4, label="Teórico O(n)", zorder=2)

    elif cenario == "balanced":
        nd = d[d["implementation"] == "Naive"].sort_values("n")
        if not nd.empty:
            n0, y0 = nd["n"].iloc[0], nd["ops_per_find"].iloc[0]
            if y0 > 0 and np.log2(n0) > 0:
                C = y0 / np.log2(n0)
                ax.plot(ns_plot, C * np.log2(np.maximum(ns_plot, 2)), "--",
                        color="#c0392b", alpha=0.45, linewidth=1.4,
                        label="Teórico O(log n)", zorder=2)
        fd = d[d["implementation"] == "FullTarjan"].sort_values("n")
        if not fd.empty:
            y_ref = fd["ops_per_find"].mean()
            ax.axhline(y_ref, linestyle=":", color="#27ae60", alpha=0.5,
                       linewidth=1.4, label=f"Teórico O(α(n)) ≈ {y_ref:.1f}", zorder=2)

def formatar_eixo_n(ax):
    ax.xaxis.set_major_formatter(
        mticker.FuncFormatter(lambda x, _: f"{int(x):,}".replace(",", ".")))

def fig1_worst_tempo(df):
    fig, ax = plt.subplots(figsize=(8, 5))
    for impl in IMPLS:
        linha(ax, df, "worst_case", impl, "time_ms")
    ax.set_title("Cenário Adversarial: Tempo de Execução × n\n"
                 "(cadeia linear + find(0) repetido)", fontsize=11, fontweight="bold")
    ax.set_xlabel("Número de Elementos (n)")
    ax.set_ylabel("Tempo médio (ms)")
    ax.legend(fontsize=9)
    formatar_eixo_n(ax)
    plt.tight_layout()
    return fig

def fig2_worst_ops(df):
    fig, ax = plt.subplots(figsize=(8, 5))
    for impl in IMPLS:
        linha(ax, df, "worst_case", impl, "ops_per_find")
    curvas_teoricas(ax, df, "worst_case", "ops_per_find")
    ax.set_title("Cenário Adversarial: Operações por Find × n\n"
                 "(evidencia a classe de complexidade)", fontsize=11, fontweight="bold")
    ax.set_xlabel("Número de Elementos (n)")
    ax.set_ylabel("Operações por find (média)")
    ax.legend(fontsize=9)
    formatar_eixo_n(ax)
    plt.tight_layout()
    return fig

def fig3_balanced_ops(df):
    fig, ax = plt.subplots(figsize=(8, 5))
    for impl in IMPLS:
        linha(ax, df, "balanced", impl, "ops_per_find")
    curvas_teoricas(ax, df, "balanced", "ops_per_find")
    ax.set_title("Cenário Árvore Balanceada: Operações por Find × n\n"
                 "(UnionByRank = O(log n)  vs  FullTarjan = O(α(n)))",
                 fontsize=11, fontweight="bold")
    ax.set_xlabel("Número de Elementos (n)  —  escala log₂")
    ax.set_ylabel("Operações por find (média)")
    ax.set_xscale("log", base=2)
    ax.legend(fontsize=9)
    plt.tight_layout()
    return fig

def fig4_random_tempo(df):
    fig, ax = plt.subplots(figsize=(8, 5))
    for impl in IMPLS:
        linha(ax, df, "random", impl, "time_ms")
    ax.set_title("Cenário Aleatório: Tempo de Execução × n\n"
                 "(n/2 unions + finds aleatórios)", fontsize=11, fontweight="bold")
    ax.set_xlabel("Número de Elementos (n)")
    ax.set_ylabel("Tempo médio (ms)")
    ax.legend(fontsize=9)
    formatar_eixo_n(ax)
    plt.tight_layout()
    return fig

def fig_combinada(df):
    fig, axes = plt.subplots(2, 2, figsize=(14, 10))
    fig.suptitle("Análise Comparativa das Implementações DSU",
                 fontsize=13, fontweight="bold", y=1.01)

    cenarios_metricas = [
        ("worst_case", "time_ms",     "Adversarial — Tempo (ms)"),
        ("worst_case", "ops_per_find", "Adversarial — Ops/Find"),
        ("balanced",   "ops_per_find", "Balanceada — Ops/Find"),
        ("random",     "time_ms",     "Aleatório — Tempo (ms)"),
    ]

    for ax, (cenario, metrica, titulo) in zip(axes.flat, cenarios_metricas):
        for impl in IMPLS:
            linha(ax, df, cenario, impl, metrica)
        if metrica == "ops_per_find":
            curvas_teoricas(ax, df, cenario, metrica)
        if cenario == "balanced" and metrica == "ops_per_find":
            ax.set_xscale("log", base=2)
        else:
            formatar_eixo_n(ax)
        ax.set_title(titulo, fontsize=10, fontweight="bold")
        ax.set_xlabel("n")
        ax.set_ylabel(metrica.replace("_", " "))
        ax.legend(fontsize=7)

    plt.tight_layout()
    return fig

def main():
    csv = "benchmark_results.csv"
    if not os.path.exists(csv):
        print(f"Erro: '{csv}' não encontrado.")
        print("Compile e execute o benchmark Java primeiro:")
        print("  javac -cp src src\\*.java")
        print("  java  -cp src Benchmark")
        sys.exit(1)

    os.makedirs("results", exist_ok=True)
    df = carregar(csv)

    figuras = [
        ("1_adversarial_tempo",  fig1_worst_tempo(df)),
        ("2_adversarial_ops",    fig2_worst_ops(df)),
        ("3_balanceada_ops",     fig3_balanced_ops(df)),
        ("4_aleatorio_tempo",    fig4_random_tempo(df)),
        ("0_combinado",          fig_combinada(df)),
    ]

    for nome, fig in figuras:
        for ext in ("png", "pdf"):
            path = f"results/{nome}.{ext}"
            fig.savefig(path, bbox_inches="tight")
            print(f"  Salvo: {path}")
        plt.close(fig)

    print("\nPronto! Os PDFs em results/ podem ser inseridos diretamente no artigo SBC.")

if __name__ == "__main__":
    main()
