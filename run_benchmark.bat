@echo off
chcp 65001 > nul
echo ============================================
echo  DSU Benchmark - TP FPAA
echo ============================================
echo.

echo [1/3] Compilando...
javac -cp src src\DisjointSet.java src\NaiveDSU.java src\UnionByRankDSU.java src\FullTarjanDSU.java src\Main.java src\Benchmark.java
if %ERRORLEVEL% neq 0 (
    echo ERRO: falha na compilacao. Verifique se o Java esta instalado.
    pause & exit /b 1
)
echo       OK

echo.
echo [2/3] Executando benchmark (pode demorar 2-5 minutos)...
java -Xss16m -cp src Benchmark
if %ERRORLEVEL% neq 0 (
    echo ERRO: falha na execucao do benchmark.
    pause & exit /b 1
)
echo       OK - resultados em benchmark_results.csv

echo.
echo [3/3] Gerando graficos...
python generate_graphs.py
if %ERRORLEVEL% neq 0 (
    echo AVISO: falha ao gerar graficos.
    echo Instale as dependencias: pip install pandas matplotlib numpy
    pause & exit /b 1
)
echo       OK - graficos em results\

echo.
echo ============================================
echo  Concluido! Abra a pasta results\ para ver
echo  os graficos prontos para o artigo SBC.
echo ============================================
pause
