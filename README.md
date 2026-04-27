# 🌳 Conectividade Dinâmica: Análise de Desempenho do Union-Find

**Curso:** Engenharia de Software
**Disciplina:** Fundamentos de Projeto e Análise de Algoritmos (FPAA) - 2026/1
**Professor:** João Pedro O. Batisteli
**Alunos:** Andre Xavier, João Pedro Guimarães, Lucas Vilela, Marina Cabalzar

---

## 📖 Sobre o Projeto

Este repositório contém o código-fonte e a análise experimental do trabalho prático sobre **Conectividade Dinâmica**. O objetivo central do projeto é implementar e avaliar o impacto de estruturas de dados otimizadas na complexidade assintótica de algoritmos.

A pesquisa investiga como as técnicas introduzidas por Robert Tarjan transformam a viabilidade de algoritmos em larga escala, gerenciando um conjunto de elementos através da estrutura **Disjoint Set Union (DSU)**.

## 🚀 Implementações

O projeto avalia três variantes do DSU:

1. **Naive (Ingênua):** Implementação básica sem heurísticas de balanceamento ou compressão.
2. **Union by Rank:** Otimização utilizando a heurística de união pela altura da árvore.
3. **Full Tarjan:** Implementação avançada combinando *Union by Rank* com *Path Compression*.

## 📂 Estrutura do Repositório

- `/src`: Código-fonte em Java contendo as interfaces, as três implementações do DSU e o laboratório de testes experimentais.
- `/docs`: Artigo técnico formatado no padrão da SBC (Sociedade Brasileira de Computação), contendo a fundamentação teórica e a discussão dos resultados.
- `/scripts`: (Opcional) Scripts auxiliares para plotagem dos gráficos de desempenho.

## 🛠️ Tecnologias e Ambiente

- **Linguagem Principal:** Java
- **Versão da JVM/JDK:** 

## ⚙️ Como Executar

Para reproduzir os experimentos e testar as estruturas:

1. Clone este repositório:
   ```bash
   git clone [https://github.com/Lucas-FV/TP-FPAA-UnionFind.git)
