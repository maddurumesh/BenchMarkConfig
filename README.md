# WEXA AI — Graph Database Cloud Benchmark

## Overview

This project benchmarks graph database performance using a fixed social-network dataset and a consistent workload methodology.

The benchmark currently includes completed benchmark runs for:

* **Neo4j**
* **CognoDB Cloud**

The project also contains adapter implementations for FalkorDB and Memgraph, but benchmark results for those databases were not included in this execution.

The primary goal is to compare graph database performance under common graph workloads using the same dataset, query patterns, warm-up configuration, measurement iterations, and concurrency.

---

## Dataset

The benchmark uses a fixed subset derived from the **SNAP soc-Pokec social network dataset**.

### Dataset size

| Metric            |    Value |
| ----------------- | -------: |
| Nodes             |   91,489 |
| Relationships     |  200,000 |
| Relationship type |  `KNOWS` |
| Node label        | `Person` |

The same dataset size was verified successfully in both Neo4j and CognoDB.

### Dataset files

```text
data/
├── nodes.csv
├── relationships.csv
└── soc-pokec-relationships.txt.gz
```

---

## Benchmark Configuration

The benchmark uses the following configuration:

| Configuration          |     Value |
| ---------------------- | --------: |
| Warm-up iterations     |        20 |
| Measurement iterations |       100 |
| Concurrency            |        10 |
| Read/write mix         | 80% / 20% |
| Random seed            |  20260806 |

A fixed random seed is used for reproducible start-node selection.

---

## Workloads

The following workloads were executed:

### 1. 1-hop traversal

Find neighboring nodes one relationship away from a selected starting node.

### 2. 2-hop traversal

Traverse two `KNOWS` relationships from the starting node.

### 3. 3-hop traversal

Traverse three `KNOWS` relationships from the starting node.

### 4. Point lookup

Find a `Person` node using its indexed ID.

### 5. Filtered lookup

Find nodes using the `name` property.

### 6. Aggregation

Count nodes in the graph.

### 7. Mixed workload

A concurrent workload consisting of:

* 80% read operations
* 20% write operations
* 10 concurrent clients

The benchmark reports queries per second (QPS) for the mixed workload.

---

# Benchmark Results

## Neo4j

Dataset verification:

```text
Nodes: 91489
Relationships: 200000
Dataset verification PASSED
```

### Latency

| Workload        | p50 (ms) | p95 (ms) |
| --------------- | -------: | -------: |
| 1-hop           |    29.66 |    37.10 |
| 2-hop           |    27.43 |    36.02 |
| 3-hop           |    26.10 |    32.61 |
| Point lookup    |    25.87 |    33.04 |
| Filtered lookup |    75.13 |   142.01 |
| Aggregation     |    26.65 |    38.93 |

### Mixed workload

```text
152.08 queries/sec
```

---

## CognoDB Cloud

Dataset verification:

```text
Nodes: 91489
Relationships: 200000
Dataset verification PASSED
```

### Latency

| Workload        | p50 (ms) | p95 (ms) |
| --------------- | -------: | -------: |
| 1-hop           |  1302.37 |  5527.40 |
| 2-hop           |  1019.35 |  2650.25 |
| 3-hop           |  1019.91 |  1870.49 |
| Point lookup    |  1206.22 |  3485.92 |
| Filtered lookup |  1075.50 |  2069.22 |
| Aggregation     |  1052.74 |  1681.59 |

### Mixed workload

```text
4.31 queries/sec
```

---

# Neo4j vs CognoDB

| Workload        | Neo4j p50 | CognoDB p50 |
| --------------- | --------: | ----------: |
| 1-hop           |  29.66 ms |  1302.37 ms |
| 2-hop           |  27.43 ms |  1019.35 ms |
| 3-hop           |  26.10 ms |  1019.91 ms |
| Point lookup    |  25.87 ms |  1206.22 ms |
| Filtered lookup |  75.13 ms |  1075.50 ms |
| Aggregation     |  26.65 ms |  1052.74 ms |

### Mixed workload comparison

| Database |    QPS |
| -------- | -----: |
| Neo4j    | 152.08 |
| CognoDB  |   4.31 |

Under this benchmark configuration, Neo4j achieved substantially lower latency and higher mixed-workload throughput than the measured CognoDB environment.

These results should be interpreted as measurements from this specific benchmark configuration and deployment environment rather than as a universal ranking of the database products.

---

# Project Structure

```text
cognodb-benchmark/
│
├── data/
│   ├── nodes.csv
│   ├── relationships.csv
│   └── soc-pokec-relationships.txt.gz
│
├── docker/
│
├── results/
│   ├── cognodb.json
│   └── neo4j.json
│
├── scripts/
│   ├── generate-dataset.py
│   └── run-benchmark.ps1
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── wexa/
│                   └── benchmark/
│                       ├── BenchmarkApplication.java
│                       ├── BenchmarkConfig.java
│                       ├── BenchmarkResult.java
│                       ├── BenchmarkRunner.java
│                       ├── DatabaseAdapter.java
│                       │
│                       └── adapters/
│                           ├── CognoDBAdapter.java
│                           ├── FalkorDBAdapter.java
│                           ├── MemgraphAdapter.java
│                           └── Neo4jAdapter.java
│
├── .env.example
├── .gitignore
├── pom.xml
└── README.md
```

---

# Reproducibility

## Requirements

* Java 17+
* Maven Wrapper
* Docker
* PowerShell
* Access to the target graph database
* Valid database credentials

The project uses Maven and can be built with:

```powershell
.\mvnw.cmd clean package
```

Successful compilation produces:

```text
target/cognodb-benchmark-1.0.0.jar
```

---

## Environment Variables

Credentials must not be committed to the repository.

Example:

```text
COGNODB_URI=<CognoDB URI>
COGNODB_USERNAME=<username>
COGNODB_PASSWORD=<password>
```

Neo4j uses:

```text
NEO4J_URI=<Neo4j URI>
NEO4J_USERNAME=<username>
NEO4J_PASSWORD=<password>
```

Actual passwords should remain local and must never be committed to Git.

---

# Benchmark Methodology

The benchmark follows these steps:

1. Connect to the database.
2. Verify the expected dataset.
3. Verify/create the node ID index.
4. Execute warm-up operations.
5. Execute the measurement iterations.
6. Record individual operation latency.
7. Calculate p50 and p95 latency.
8. Execute the concurrent mixed workload.
9. Calculate mixed-workload QPS.
10. Store the benchmark result as JSON.

The benchmark intentionally avoids fabricating ingestion throughput when the existing dataset is reused.

For the reported runs, the dataset was already present in the database and therefore:

```text
loadTimeMs = 0
```

does not represent a zero-millisecond ingestion operation. It indicates that ingestion was not measured during that benchmark execution.

---

# Result Files

Benchmark results are stored under:

```text
results/
├── cognodb.json
└── neo4j.json
```

The JSON result contains:

* Database name
* Node count
* Relationship count
* p50 latency
* p95 latency
* Mixed workload QPS
* Warm-up iterations
* Measurement iterations
* Concurrency
* Dataset description
* Benchmark methodology
* Environment metadata where available

---

# Database Adapter Architecture

The benchmark uses a common `DatabaseAdapter` interface.

Each database adapter provides operations for:

* Connection
* Dataset loading
* Dataset clearing
* Node counting
* Relationship counting
* Index creation
* Point lookup
* Filtered lookup
* Graph traversal
* Aggregation
* Write operations
* Connection cleanup

This allows the same benchmark runner to execute consistent workloads against different graph database implementations.

---

# Completed Benchmark Coverage

| Database | Adapter | Dataset verified | Benchmark executed | Result file            |
| -------- | ------- | ---------------- | ------------------ | ---------------------- |
| Neo4j    | Yes     | Yes              | Yes                | `results/neo4j.json`   |
| CognoDB  | Yes     | Yes              | Yes                | `results/cognodb.json` |
| FalkorDB | Yes     | Not executed     | Not executed       | —                      |
| Memgraph | Yes     | Not executed     | Not executed       | —                      |

FalkorDB and Memgraph adapter implementations are present in the project, but no benchmark results are claimed for them in this submission.

---

# Limitations

The benchmark client does not automatically observe cloud-provider infrastructure specifications or database storage footprint.

Therefore, unless supplied through environment variables, these fields are reported as:

```text
Not observable from benchmark client.
```

The results are also dependent on:

* Database deployment configuration
* Network latency
* Cloud infrastructure
* Database version
* Index configuration
* Resource availability
* Concurrent workload conditions

Therefore, the results represent this specific test environment.

---

# Conclusion

The benchmark successfully validated a common graph dataset containing 91,489 nodes and 200,000 relationships and measured Neo4j and CognoDB using the same workload methodology.

Neo4j produced substantially lower measured latency and higher mixed-workload throughput in the recorded execution, while CognoDB was successfully connected and benchmarked as a cloud-hosted graph database.

The benchmark framework is extensible through the `DatabaseAdapter` abstraction and contains adapter implementations for additional graph databases.
