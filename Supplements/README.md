# Supplements

This directory contains the supplementary materials for the experiments.

## Structure

```text
Supplements/
|- EcoreGenTLShowcase/
|- Experiments/
   |- Benchmarks/
   |- RQ1/
   |- RQ2/
   |- RQ3/
```

## EcoreGenTLShowcase

`EcoreGenTLShowcase/` contains a runnable showcase project for EcoreGenTL, including the packaged `EcoreGenTL.jar`, prompt templates, model files, and a simple library management example.

We recommend running this showcase example first to get familiar with EcoreGenTL.

## Experiments

`Experiments/` contains benchmark materials, scripts used for generation and testing, raw experimental outputs, statistical-test summaries, and case analyses.

### Benchmarks

`Experiments/Benchmarks/` contains the benchmark problems used in the experiments.

```text
Benchmarks/
|- JavaBench/
|  |- PA19/
|  |- PA20/
|  |- PA21/
|  |- PA22/
|- TLBench/
   |- OLRS/
   |- OPMS/
   |- OPRS/
   |- ORS/
   |- R2_EmployeeManagementSystem/
   |- R123_School/
   |- R12_RentedCarGalleryManagementSystem/
   |- R132_MunicipalLibrary/
   |- R144_AirlineFlights/
   |- R22_IPOApplication/
```

### RQ1

`Experiments/RQ1/` contains the materials for RQ1.

```text
RQ1/
|- Scripts/
|- Raw Results/
|- RQ1_SignificanceTest.md
```

`Scripts/` contains code-generation and test-running scripts used for the compared methods.

`Scripts/CodeGen/EcoreGenTL/` contains the `.mwe2` scripts used by our method to generate code. These scripts are provided for reference only; 

`Raw Results/` is organized as:

```text
Raw Results/{Benchmark}/{Method}/{Model}/{Problem}/
```

RQ1 methods include:

```text
EcoreGenTL
VL
VL-FIX
OpenCode
SWE-Agent-Mini
```

The raw result folders contain copied Java source files while preserving their original relative paths.

### RQ2

`Experiments/RQ2/` contains the materials for the ablation study.

```text
RQ2/
|- Raw Results/
|- RQ2_SignificanceTest.md
```

`Raw Results/` is organized as:

```text
Raw Results/{Benchmark}/{Variant}/{Model}/{Problem}/
```

RQ2 variants include:

```text
Default
nocompression
nocontext
nofix
norequire
noreview
```

`RQ2_SignificanceTest.md` reports the paired permutation-test results for the ablation study.

### RQ3

`Experiments/RQ3/` contains case analyses:

```text
JavaBench_case_analysis.md
TLBench_case_analysis.md
```

These files analyze the test cases failed on JavaBench and TLBench.
