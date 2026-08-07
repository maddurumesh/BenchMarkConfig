\# WEXA AI



\# Graph Database Cloud Benchmarking



\## Final Technical Report



\---



\## 1. Executive Summary



This project implements a reproducible benchmarking framework for evaluating graph database systems under a common dataset and a consistent set of graph-oriented workloads.



The primary objective was to create a benchmark application capable of connecting to different graph database platforms through a common adapter interface, execute the same workload methodology against each database, collect performance measurements, and generate machine-readable JSON benchmark results.



The framework was implemented using \*\*Java 17 and Maven\*\*. A common `DatabaseAdapter` interface separates the benchmark logic from database-specific connectivity and query implementations. This design allows multiple graph database systems to be evaluated using the same `BenchmarkRunner`.



For the current completed execution, two databases were successfully benchmarked:



\* \*\*Neo4j\*\*

\* \*\*CognoDB Cloud\*\*



The project also contains adapter implementations for:



\* \*\*FalkorDB\*\*

\* \*\*Memgraph\*\*



However, FalkorDB and Memgraph were not included in the final performance comparison because completed benchmark result files were not generated for those systems.



A fixed subset of the \*\*SNAP soc-Pokec social network dataset\*\* containing \*\*91,489 nodes and 200,000 relationships\*\* was used.



The benchmark measured:



\* 1-hop traversal

\* 2-hop traversal

\* 3-hop traversal

\* Point lookup

\* Filtered lookup

\* Aggregation

\* Concurrent mixed read/write workload



For individual workloads, \*\*p50 and p95 latency\*\* were measured. For the concurrent mixed workload, \*\*queries per second (QPS)\*\* was measured.



Based on the recorded benchmark execution, Neo4j achieved substantially lower measured latency and higher mixed-workload throughput than CognoDB in the tested environment.



These results represent the specific deployment, database configuration, network conditions, and benchmark environment used for this assignment. They should not be interpreted as universal performance characteristics of either database platform.



\---



\# 2. Project Objective



The objective of the Graph Database Cloud Benchmarking project was to develop a practical and reproducible framework for comparing graph database performance.



The project focuses on creating a consistent methodology rather than relying on isolated database-specific tests.



The benchmark framework was designed to:



1\. Connect to graph database systems.

2\. Verify database connectivity.

3\. Load or reuse a fixed benchmark dataset.

4\. Verify node and relationship counts.

5\. Create or verify appropriate indexes.

6\. Execute warm-up operations.

7\. Execute standardized benchmark workloads.

8\. Measure individual query latency.

9\. Calculate p50 and p95 latency.

10\. Execute concurrent mixed read/write workloads.

11\. Calculate mixed workload throughput.

12\. Store benchmark results in JSON format.

13\. Support multiple database adapters.

14\. Keep credentials outside the source code.

15\. Make benchmark execution reproducible through fixed configuration values.



The overall architecture separates benchmark methodology from database-specific implementation.



\---



\# 3. Technology Stack



The project uses the following technologies.



| Technology     | Purpose                                                 |

| -------------- | ------------------------------------------------------- |

| Java 17        | Core benchmark implementation                           |

| Maven          | Build and dependency management                         |

| Neo4j Driver   | Database connectivity and Cypher execution              |

| CognoDB        | Cloud graph database benchmark target                   |

| Cypher         | Graph query language                                    |

| Jackson        | JSON result generation                                  |

| PowerShell     | Local benchmark execution and environment configuration |

| Docker         | Supporting containerized/deployment workflows           |

| SNAP soc-Pokec | Benchmark dataset                                       |

| Git/GitHub     | Source code and final submission repository             |



\---



\# 4. Dataset



\## 4.1 Dataset Source



The benchmark uses a fixed subset of the \*\*SNAP soc-Pokec social network dataset\*\*.



The dataset represents a social network in which nodes represent users and relationships represent connections between users.



For this benchmark, the data was represented using:



```text

Node Label:

Person



Relationship Type:

KNOWS

```



\---



\## 4.2 Dataset Size



The final benchmark dataset was verified as:



| Metric        |   Value |

| ------------- | ------: |

| Nodes         |  91,489 |

| Relationships | 200,000 |



The benchmark therefore satisfies the target requirement of using a graph containing more than 100,000 relationships.



\---



\## 4.3 Dataset Verification



Before executing the benchmark workloads, the framework verifies the database contents using database queries.



The verification checks:



```text

Expected nodes: 91489

Expected relationships: 200000

```



Both completed benchmark targets passed this verification.



\### Neo4j



```text

Nodes: 91489

Relationships: 200000



Dataset verification PASSED.

```



\### CognoDB



```text

Nodes: 91489

Relationships: 200000



Dataset verification PASSED.

```



This ensures that both databases were evaluated against the same graph size.



\---



\# 5. Dataset Files



The repository contains the dataset-related files under:



```text

data/

├── nodes.csv

├── relationships.csv

└── soc-pokec-relationships.txt.gz

```



The generated CSV files provide a consistent input format for the database adapters.



The benchmark framework uses node and relationship identifiers so that the same logical graph structure can be represented across different graph database systems.



\---



\# 6. Benchmark Architecture



The benchmark follows an adapter-based architecture.



```text

&#x20;                        BenchmarkApplication

&#x20;                                 |

&#x20;                                 v

&#x20;                        BenchmarkConfig

&#x20;                                 |

&#x20;                                 v

&#x20;                        BenchmarkRunner

&#x20;                                 |

&#x20;                                 v

&#x20;                        DatabaseAdapter

&#x20;                                 |

&#x20;            +--------------------+--------------------+

&#x20;            |                    |                    |

&#x20;            v                    v                    v

&#x20;      Neo4jAdapter         CognoDBAdapter       Other Adapters

&#x20;                                                  |

&#x20;                                    +-------------+-------------+

&#x20;                                    |                           |

&#x20;                                    v                           v

&#x20;                               FalkorDBAdapter            MemgraphAdapter

```



The main components are described below.



\---



\# 7. DatabaseAdapter



`DatabaseAdapter` provides the common interface that database implementations follow.



The interface abstracts database-specific operations such as:



\* Connecting

\* Closing connections

\* Clearing data

\* Loading datasets

\* Counting nodes

\* Counting relationships

\* Creating indexes

\* Point lookup

\* Filtered lookup

\* Graph traversal

\* Aggregation

\* Write operations



This prevents the benchmark runner from becoming tightly coupled to a particular graph database.



\---



\# 8. BenchmarkRunner



`BenchmarkRunner` contains the common benchmark methodology.



Its responsibilities include:



\* Preparing benchmark configuration

\* Verifying dataset size

\* Creating/verifying indexes

\* Performing warm-up operations

\* Executing workload measurements

\* Calculating p50

\* Calculating p95

\* Running concurrent mixed workloads

\* Calculating QPS

\* Generating final benchmark results



The same benchmark runner is used for different database adapters.



This is important because it reduces methodological differences between database tests.



\---



\# 9. Benchmark Configuration



The benchmark was executed using the following configuration.



| Parameter              |    Value |

| ---------------------- | -------: |

| Warm-up iterations     |       20 |

| Measurement iterations |      100 |

| Concurrency            |       10 |

| Read percentage        |      80% |

| Write percentage       |      20% |

| Random seed            | 20260806 |



\---



\## 9.1 Warm-Up



Before measurement begins, the benchmark performs warm-up operations.



The warm-up phase executes the required read workloads before collecting the final latency measurements.



This helps reduce the influence of initial connection, cache, query compilation, or startup effects.



The configured warm-up count was:



```text

20 iterations

```



\---



\## 9.2 Measurement



Each individual workload was measured over:



```text

100 iterations

```



The framework records the elapsed time for each operation.



The measured nanosecond duration is converted into microseconds and then milliseconds.



The resulting latency samples are sorted before percentile calculation.



\---



\# 10. Percentile Measurement



The benchmark reports two latency values:



\### p50



p50 represents the median latency.



Approximately half of the measured operations completed at or below this latency.



\### p95



p95 represents the latency at which approximately 95% of the measured operations completed at or below the reported value.



p95 is particularly useful for identifying higher-latency behavior that is not visible from the median alone.



The benchmark uses linear interpolation between sorted latency samples when necessary.



\---



\# 11. Reproducible Random Node Selection



The benchmark uses a fixed random seed:



```text

20260806

```



The benchmark creates a list of node IDs from:



```text

1

through

91489

```



The list is shuffled using the fixed seed.



This means that benchmark executions use a reproducible randomized sequence of start-node IDs.



Using the same seed helps ensure that different database systems receive comparable starting-node selections.



\---



\# 12. Benchmark Workloads



The framework evaluates seven major workload categories.



\---



\## 12.1 1-Hop Traversal



The 1-hop traversal starts from a selected node and follows one `KNOWS` relationship.



Conceptually:



```text

(start)-\[:KNOWS]->(target)

```



The benchmark limits the returned results to prevent excessive result materialization.



\---



\## 12.2 2-Hop Traversal



The 2-hop traversal follows two relationship levels.



Conceptually:



```text

(start)-\[:KNOWS]->()-\[:KNOWS]->(target)

```



This evaluates deeper graph traversal compared with the 1-hop workload.



\---



\## 12.3 3-Hop Traversal



The 3-hop workload follows three relationship levels.



Conceptually:



```text

(start)

&#x20;  |

&#x20;KNOWS

&#x20;  |

&#x20;  v

&#x20;  ()

&#x20;  |

&#x20;KNOWS

&#x20;  |

&#x20;  v

&#x20;  ()

&#x20;  |

&#x20;KNOWS

&#x20;  |

&#x20;  v

(target)

```



This workload evaluates the database's ability to navigate multiple graph relationship levels.



\---



\## 12.4 Point Lookup



The point lookup searches for a specific node using its identifier.



Conceptually:



```cypher

MATCH (n:Person {id: $id})

RETURN n

LIMIT 1

```



The benchmark creates/verifies an index on the `Person.id` property.



\---



\## 12.5 Filtered Lookup



The filtered lookup searches for a node using its name property.



Conceptually:



```cypher

MATCH (n:Person)

WHERE n.name = $name

RETURN n

LIMIT 10

```



This workload represents a property-based filtering operation.



\---



\## 12.6 Aggregation



The aggregation workload counts the number of nodes.



Conceptually:



```cypher

MATCH (n:Person)

RETURN count(n) AS total

```



This measures an aggregation operation over the graph.



\---



\## 12.7 Mixed Read/Write Workload



The mixed workload uses:



```text

80% reads

20% writes

```



The benchmark uses 10 concurrent clients.



For every five operations:



```text

4 reads

1 write

```



The write operation updates a benchmark-specific property:



```text

benchmarkWrite = true

```



The purpose of this workload is to evaluate behavior under concurrent activity rather than isolated reads.



\---



\# 13. Neo4j Benchmark



Neo4j was successfully benchmarked using the fixed dataset.



Dataset verification:



```text

Nodes: 91489

Relationships: 200000

```



Verification result:



```text

Dataset verification PASSED.

```



\---



\# 14. Neo4j Latency Results



| Workload        |      p50 |       p95 |

| --------------- | -------: | --------: |

| 1-hop           | 29.66 ms |  37.10 ms |

| 2-hop           | 27.43 ms |  36.02 ms |

| 3-hop           | 26.10 ms |  32.61 ms |

| Point lookup    | 25.87 ms |  33.04 ms |

| Filtered lookup | 75.13 ms | 142.01 ms |

| Aggregation     | 26.65 ms |  38.93 ms |



The lowest measured Neo4j p50 among these workloads was the point lookup at approximately:



```text

25.87 ms

```



The highest p50 was filtered lookup:



```text

75.13 ms

```



The filtered lookup also produced the highest p95:



```text

142.01 ms

```



\---



\# 15. Neo4j Mixed Workload



The concurrent mixed workload produced:



```text

152.08 QPS

```



Configuration:



```text

Concurrency: 10

Read workload: 80%

Write workload: 20%

Measurement iterations per client: 100

```



The measured throughput indicates that the tested Neo4j environment processed significantly more operations per second than the tested CognoDB environment under the same benchmark configuration.



\---



\# 16. CognoDB Benchmark



CognoDB Cloud was also successfully benchmarked using the same fixed dataset.



Dataset verification:



```text

Nodes: 91489

Relationships: 200000

```



Verification result:



```text

Dataset verification PASSED.

```



The benchmark connected successfully using environment-based credentials.



Credentials were intentionally not included in the repository or benchmark result files.



\---



\# 17. CognoDB Latency Results



| Workload        |        p50 |        p95 |

| --------------- | ---------: | ---------: |

| 1-hop           | 1302.37 ms | 5527.40 ms |

| 2-hop           | 1019.35 ms | 2650.25 ms |

| 3-hop           | 1019.91 ms | 1870.49 ms |

| Point lookup    | 1206.22 ms | 3485.92 ms |

| Filtered lookup | 1075.50 ms | 2069.22 ms |

| Aggregation     | 1052.74 ms | 1681.59 ms |



The measured CognoDB p50 values were approximately one second or greater for the recorded workloads.



The highest p95 value occurred for the 1-hop traversal:



```text

5527.40 ms

```



This indicates significantly higher tail latency in this particular execution environment.



\---



\# 18. CognoDB Mixed Workload



The concurrent mixed workload produced:



```text

4.31 QPS

```



Configuration:



```text

Concurrency: 10

Read workload: 80%

Write workload: 20%

Measurement iterations per client: 100

```



\---



\# 19. Direct Performance Comparison



The following table compares the p50 latency values from the completed executions.



| Workload        | Neo4j p50 | CognoDB p50 |

| --------------- | --------: | ----------: |

| 1-hop           |  29.66 ms |  1302.37 ms |

| 2-hop           |  27.43 ms |  1019.35 ms |

| 3-hop           |  26.10 ms |  1019.91 ms |

| Point lookup    |  25.87 ms |  1206.22 ms |

| Filtered lookup |  75.13 ms |  1075.50 ms |

| Aggregation     |  26.65 ms |  1052.74 ms |



The measurements show that Neo4j had lower p50 latency across all six individual workloads in this benchmark execution.



\---



\# 20. Mixed Workload Comparison



| Database | Mixed Workload QPS |

| -------- | -----------------: |

| Neo4j    |             152.08 |

| CognoDB  |               4.31 |



The measured throughput difference was substantial in the tested environments.



Neo4j:



```text

152.08 QPS

```



CognoDB:



```text

4.31 QPS

```



Therefore, for this particular execution, Neo4j achieved the higher measured mixed-workload throughput.



\---



\# 21. Performance Interpretation



The results demonstrate a significant performance difference between the two tested environments.



Neo4j produced:



\* Lower median latency

\* Lower p95 latency

\* Higher mixed-workload throughput



CognoDB produced:



\* Higher measured query latency

\* Higher tail latency for several workloads

\* Lower mixed-workload throughput



However, these results should be interpreted carefully.



The benchmark measures the actual environment in which the tests were executed. It does not establish that one graph database is universally faster than another.



Performance can depend on factors including:



\* Cloud region

\* Network path

\* Database hardware

\* Database configuration

\* Database version

\* Index configuration

\* Connection behavior

\* Query execution engine

\* Resource allocation

\* Concurrent load

\* Cloud service tier



Therefore, the correct conclusion is:



> In the tested benchmark environment and configuration, Neo4j demonstrated substantially lower measured latency and higher mixed-workload throughput than CognoDB.



\---



\# 22. Indexing



The benchmark verifies/creates an index on the node ID property.



The logical index is:



```text

Person.id

```



The Neo4j/Cypher-style definition used by the adapter is conceptually:



```cypher

CREATE INDEX person\_id\_index

IF NOT EXISTS

FOR (n:Person)

ON (n.id)

```



This supports efficient point lookup operations.



\---



\# 23. Database Adapter Implementations



The project contains adapter implementations for four graph database systems.



```text

src/main/java/com/wexa/benchmark/adapters/

│

├── CognoDBAdapter.java

├── FalkorDBAdapter.java

├── MemgraphAdapter.java

└── Neo4jAdapter.java

```



The common interface allows the benchmark runner to communicate with each implementation through the same abstraction.



\---



\# 24. Database Completion Status



The current implementation and benchmark execution status is:



| Database | Adapter | Benchmark Executed | Result Available |

| -------- | ------- | ------------------ | ---------------- |

| Neo4j    | Yes     | Yes                | Yes              |

| CognoDB  | Yes     | Yes                | Yes              |

| FalkorDB | Yes     | No                 | No               |

| Memgraph | Yes     | No                 | No               |



Therefore, the \*\*four database adapters are present\*\*, but only \*\*two databases have completed benchmark executions\*\*.



Only Neo4j and CognoDB are included in the final numerical performance comparison because their benchmark results were actually generated and verified.



\---



\# 25. Result Files



The final benchmark results are stored in:



```text

results/

├── neo4j.json

└── cognodb.json

```



The JSON files contain structured benchmark information including:



\* Database name

\* Node count

\* Relationship count

\* Load information

\* p50 values

\* p95 values

\* Mixed workload QPS

\* Warm-up configuration

\* Measurement configuration

\* Concurrency

\* Dataset source

\* Benchmark notes



\---



\# 26. Example Result Structure



A result file follows the general structure:



```json

{

&#x20; "database": "Neo4j",

&#x20; "nodeCount": 91489,

&#x20; "relationshipCount": 200000,

&#x20; "p50": {},

&#x20; "p95": {},

&#x20; "mixedQueriesPerSecond": 152.078,

&#x20; "warmupIterations": 20,

&#x20; "measurementIterations": 100,

&#x20; "concurrency": 10

}

```



The exact numerical results are preserved in the repository's JSON files.



\---



\# 27. Reproducibility



Reproducibility was an important part of the benchmark design.



The framework records or fixes:



\* Dataset size

\* Dataset source

\* Node identifiers

\* Relationship structure

\* Warm-up count

\* Measurement count

\* Concurrency

\* Read/write ratio

\* Random seed

\* Workload definitions

\* Percentile methodology

\* Result format



The fixed random seed is:



```text

20260806

```



The benchmark uses the same randomized node-selection strategy across workloads.



This helps reduce variation caused by using completely different starting nodes for different database executions.



\---



\# 28. Credential Security



Database credentials are not hard-coded into the benchmark source code.



The application retrieves credentials from environment variables.



For example:



```text

COGNODB\_URI

COGNODB\_USERNAME

COGNODB\_PASSWORD

```



Neo4j uses corresponding environment variables.



The actual passwords should remain private and must not be committed to GitHub.



The repository should contain only an example environment file such as:



```text

.env.example

```



with placeholder values.



The real `.env` file should remain ignored by Git.



\---



\# 29. Environment Configuration



The benchmark configuration validates that required environment variables exist before attempting to connect.



If a required variable is missing, the application throws an error rather than attempting to run with incomplete credentials.



This provides a basic safeguard against accidentally executing the benchmark with missing database connection information.



\---



\# 30. Project Structure



The final project structure is approximately:



```text

cognodb-benchmark/

│

├── .env

├── .env.example

├── .gitignore

├── mvnw.cmd

├── pom.xml

├── README.md

│

├── .mvn/

│   └── wrapper/

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

└── target/

&#x20;   └── cognodb-benchmark-1.0.0.jar

```



\---



\# 31. Build Verification



The project successfully compiled using Maven.



The build produced:



```text

BUILD SUCCESS

```



The generated executable artifact was:



```text

target/cognodb-benchmark-1.0.0.jar

```



The project was compiled using Java 17.



The successful Maven build demonstrates that the benchmark source code and dependencies were resolved correctly.



\---



\# 32. Benchmark Execution Verification



The completed CognoDB execution produced output similar to:



```text

Connected.



Using existing CognoDB dataset...

Nodes: 91489

Relationships: 200000

Expected nodes: 91489

Expected relationships: 200000



Dataset verification PASSED.



Running 1-hop traversal...

1-hop p50=1302.367 ms p95=5527.400 ms



Running 2-hop traversal...

2-hop p50=1019.346 ms p95=2650.246 ms



Running 3-hop traversal...

3-hop p50=1019.906 ms p95=1870.490 ms



Running point lookup...

point-lookup p50=1206.216 ms p95=3485.922 ms



Running filtered lookup...

filtered-lookup p50=1075.503 ms p95=2069.216 ms



Running aggregation...

aggregation p50=1052.741 ms p95=1681.587 ms



Running mixed workload...

Mixed workload: 4.31 queries/sec

```



This output provides direct evidence that the benchmark executed successfully.



\---



\# 33. Dataset Reuse



The final benchmark execution reused an existing dataset instead of clearing and reloading the database during every benchmark run.



The result files explicitly record:



```text

Existing dataset reused for this benchmark execution.

No database clear or dataset reload was performed.

Ingestion throughput was therefore not measured.

```



This is important because load throughput values were not fabricated.



The benchmark reports zero load throughput for the reuse-based execution because ingestion was not part of that particular measurement.



\---



\# 34. Ingestion Throughput



For the final recorded executions:



```text

loadTimeMs = 0

nodesPerSecond = 0

relationshipsPerSecond = 0

```



These values should not be interpreted as saying that loading the dataset literally required zero milliseconds.



Instead, they indicate that the benchmark execution reused an already-loaded dataset and did not perform a new ingestion measurement.



This distinction is explicitly documented in the generated JSON result files.



\---



\# 35. Benchmark Limitations



The benchmark has several limitations.



\### 35.1 Infrastructure Visibility



The benchmark client cannot automatically observe all infrastructure information.



For example:



\* Exact cloud instance specification

\* Storage footprint

\* Underlying physical hardware

\* Internal database resource allocation



are not necessarily available to the client.



\---



\### 35.2 Network Effects



Cloud database performance may be influenced by network latency.



A database running remotely may incur:



```text

Client

&#x20;  |

&#x20;  | Network latency

&#x20;  v

Cloud database

```



Therefore, measured latency may include both query execution time and communication overhead.



\---



\### 35.3 Database Configuration



Database performance depends heavily on configuration.



Different settings for:



\* Memory

\* Cache

\* Indexes

\* Connection pools

\* Query execution

\* Storage

\* CPU allocation



can produce different results.



\---



\### 35.4 Limited Benchmark Duration



The benchmark uses:



```text

20 warm-up iterations

100 measurement iterations

10 concurrent clients

```



This provides a practical benchmark but does not represent long-running production workloads.



\---



\### 35.5 Limited Workload Variety



The benchmark focuses on a defined set of graph operations.



Real production applications may include:



\* More complex traversals

\* Large result sets

\* Multiple relationship types

\* Complex filtering

\* Writes involving multiple entities

\* Transactions

\* Concurrent graph modifications



These are outside the scope of the current benchmark.



\---



\# 36. Interpretation of Results



The benchmark should be interpreted as an empirical comparison of the tested environments.



The results demonstrate that, under the recorded configuration:



\### Neo4j



\* Lower p50 latency

\* Lower p95 latency

\* Higher mixed workload throughput



\### CognoDB



\* Higher p50 latency

\* Higher p95 latency

\* Lower mixed workload throughput



The benchmark therefore successfully provides quantitative measurements rather than relying only on qualitative descriptions.



\---



\# 37. What the Project Demonstrates



The project demonstrates practical knowledge of:



\### Java



\* Object-oriented design

\* Interfaces

\* Classes

\* Exception handling

\* Collections

\* Concurrency

\* Executors

\* Futures

\* File handling



\### Database Engineering



\* Database connectivity

\* Cypher queries

\* Index creation

\* Graph traversal

\* Aggregation

\* Read/write operations



\### Benchmarking



\* Warm-up

\* Measurement iterations

\* Latency measurement

\* Percentiles

\* Concurrency

\* Throughput

\* Reproducibility



\### Cloud Database Usage



\* Cloud database connectivity

\* Environment-based credentials

\* Remote database benchmarking

\* Network-aware performance interpretation



\### Software Engineering



\* Adapter pattern

\* Configuration separation

\* Result serialization

\* JSON output

\* Reusable benchmark runner



\---



\# 38. Design Benefits



The adapter-based design provides several benefits.



\## 38.1 Separation of Concerns



The benchmark runner does not need to know the implementation details of every database.



Instead:



```text

BenchmarkRunner

&#x20;      |

&#x20;      v

DatabaseAdapter

&#x20;      |

&#x20;      +---- Database-specific implementation

```



This makes the framework easier to extend.



\---



\## 38.2 Consistent Workloads



All supported adapters expose the same logical operations.



This improves comparability between benchmark targets.



\---



\## 38.3 Extensibility



A new graph database can be added by implementing the `DatabaseAdapter` interface and providing database-specific query and connection logic.



The core benchmark runner does not need to be redesigned.



\---



\# 39. Final Database Status



The final status of the four database targets is:



| Database | Adapter Present | Dataset Verified | Benchmark Completed | Included in Comparison |

| -------- | --------------- | ---------------- | ------------------- | ---------------------- |

| Neo4j    | Yes             | Yes              | Yes                 | Yes                    |

| CognoDB  | Yes             | Yes              | Yes                 | Yes                    |

| FalkorDB | Yes             | Not completed    | No                  | No                     |

| Memgraph | Yes             | Not completed    | No                  | No                     |



This distinction is important for accurate reporting.



The project should not claim that performance benchmarking was completed for all four databases.



Instead, the accurate statement is:



> The framework contains adapters for four graph database systems, while completed benchmark executions and final numerical results are currently available for Neo4j and CognoDB.



\---



\# 40. Final Results Summary



| Category       |   Neo4j | CognoDB |

| -------------- | ------: | ------: |

| Nodes          |  91,489 |  91,489 |

| Relationships  | 200,000 | 200,000 |

| Warm-up        |      20 |      20 |

| Measurements   |     100 |     100 |

| Concurrency    |      10 |      10 |

| Read workload  |     80% |     80% |

| Write workload |     20% |     20% |

| Mixed QPS      |  152.08 |    4.31 |



The individual latency results are:



| Workload        | Neo4j p50 | CognoDB p50 |

| --------------- | --------: | ----------: |

| 1-hop           |  29.66 ms |  1302.37 ms |

| 2-hop           |  27.43 ms |  1019.35 ms |

| 3-hop           |  26.10 ms |  1019.91 ms |

| Point lookup    |  25.87 ms |  1206.22 ms |

| Filtered lookup |  75.13 ms |  1075.50 ms |

| Aggregation     |  26.65 ms |  1052.74 ms |



\---



\# 41. Final Conclusion



The Graph Database Cloud Benchmarking project successfully implements a reusable Java-based benchmarking framework for graph databases.



The framework provides:



\* Common database adapter architecture

\* Reproducible dataset selection

\* Dataset verification

\* Standardized graph workloads

\* Warm-up execution

\* Latency measurement

\* p50 calculation

\* p95 calculation

\* Concurrent execution

\* Mixed read/write workload

\* QPS measurement

\* JSON result generation

\* Environment-based credential management

\* Extensible database adapter design



The final benchmark dataset was verified at:



```text

91,489 nodes

200,000 relationships

```



Two database systems were successfully benchmarked:



```text

Neo4j

CognoDB

```



The project also contains adapter implementations for:



```text

FalkorDB

Memgraph

```



but completed benchmark executions for those two systems were not included in the final numerical comparison.



For the completed benchmark execution, Neo4j demonstrated substantially lower measured latency across the tested workloads and significantly higher mixed-workload throughput than CognoDB.



The measured mixed workload results were:



```text

Neo4j   : 152.08 QPS

CognoDB :   4.31 QPS

```



The results provide a reproducible quantitative comparison of the two tested database environments.



The findings should be interpreted within the exact infrastructure, network, database configuration, dataset, and workload conditions used for this assignment.



\---



\# 42. Final Submission Checklist



Before pushing the project to GitHub, verify the following:



\* \[x] Java source code included

\* \[x] Maven configuration included

\* \[x] Database adapter interface included

\* \[x] Neo4j adapter included

\* \[x] CognoDB adapter included

\* \[x] FalkorDB adapter included

\* \[x] Memgraph adapter included

\* \[x] Dataset files included/available as appropriate

\* \[x] Neo4j benchmark result generated

\* \[x] CognoDB benchmark result generated

\* \[x] Dataset verification completed

\* \[x] 91,489 nodes verified

\* \[x] 200,000 relationships verified

\* \[x] p50 latency recorded

\* \[x] p95 latency recorded

\* \[x] Mixed workload QPS recorded

\* \[x] Credentials kept outside source code

\* \[x] `.env.example` available

\* \[x] `.env` excluded from Git

\* \[x] Build verified with Maven

\* \[x] Final benchmark results stored in JSON

\* \[x] Final technical report prepared



\---



\# 43. Submission Statement



This project represents the completed implementation and benchmark execution performed for the WEXA AI Graph Database Cloud Benchmarking assignment.



The reported measurements are based on actual benchmark executions performed against the verified dataset containing 91,489 nodes and 200,000 relationships.



The final repository contains the benchmark framework, database adapters, benchmark result files, dataset-generation/supporting scripts, and project documentation required to understand and reproduce the completed portions of the benchmark.



\*\*Final completed benchmark targets:\*\*



```text

Neo4j    - Completed

CognoDB  - Completed



FalkorDB - Adapter available; benchmark not completed

Memgraph - Adapter available; benchmark not completed

```



\*\*Final measured comparison:\*\*



```text

Neo4j    - 152.08 QPS

CognoDB  -   4.31 QPS

```



\*\*Dataset:\*\*



```text

91,489 nodes

200,000 relationships

```



\---



\## End of Final Technical Report



