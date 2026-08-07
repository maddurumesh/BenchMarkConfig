\# WEXA AI Ã¢â‚¬â€œ Graph Database Cloud Benchmark



A Java 17 benchmark application for evaluating graph database performance using a common database adapter architecture.



The current implementation has been successfully executed against \*\*CognoDB\*\* using a dataset containing \*\*91,489 nodes\*\* and \*\*200,000 relationships\*\*.



\---



\## Requirements



\* Java 17

\* Maven Wrapper

\* Internet access to the configured graph database

\* CognoDB credentials



\---



\## Project Structure



```text

cognodb-benchmark/

Ã¢â€â€š

Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ src/

Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ main/

Ã¢â€â€š       Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ java/

Ã¢â€â€š           Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ com/

Ã¢â€â€š               Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ wexa/

Ã¢â€â€š                   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ benchmark/

Ã¢â€â€š                       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ adapters/

Ã¢â€â€š                       Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ CognoDBAdapter.java

Ã¢â€â€š                       Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ Neo4jAdapter.java

Ã¢â€â€š                       Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ MemgraphAdapter.java

Ã¢â€â€š                       Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ FalkorDBAdapter.java

Ã¢â€â€š                       Ã¢â€â€š

Ã¢â€â€š                       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ BenchmarkApplication.java

Ã¢â€â€š                       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ BenchmarkConfig.java

Ã¢â€â€š                       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ BenchmarkResult.java

Ã¢â€â€š                       Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ BenchmarkRunner.java

Ã¢â€â€š                       Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ DatabaseAdapter.java

Ã¢â€â€š

Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ results/

Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ cognodb.json

Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ README.md

Ã¢â€â€š

Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ .env.example

Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ .gitignore

Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ pom.xml

Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ README.md

Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ mvnw.cmd

```



\---



\## Configuration



The benchmark uses environment variables for database credentials.



Required variables:



```text

COGNODB\_URI

COGNODB\_USERNAME

COGNODB\_PASSWORD

```



Example:



```text

COGNODB\_URI=bolt+s://your-cognodb-host

COGNODB\_USERNAME=your-username

COGNODB\_PASSWORD=your-password

```



\*\*Real credentials must never be committed to the repository.\*\*



Do not commit:



\* Database passwords

\* API keys

\* Private connection strings

\* Cloud credentials

\* `.env` files



\---



\## Build



The project uses the Maven Wrapper.



On Windows:



```powershell

.\\mvnw.cmd clean package -DskipTests

```



Expected result:



```text

BUILD SUCCESS

```



The executable JAR is generated at:



```text

target/cognodb-benchmark-1.0.0.jar

```



The current project was successfully compiled using Java 17.



\---



\## Running the Benchmark



Set the environment variables in PowerShell:



```powershell

$env:COGNODB\_URI="your-uri"

$env:COGNODB\_USERNAME="your-username"

$env:COGNODB\_PASSWORD="your-password"

```



Verify that the variables are configured without displaying their values:



```powershell

Write-Host "URI set:" (\[string]::IsNullOrWhiteSpace($env:COGNODB\_URI) -eq $false)

Write-Host "Username set:" (\[string]::IsNullOrWhiteSpace($env:COGNODB\_USERNAME) -eq $false)

Write-Host "Password set:" (\[string]::IsNullOrWhiteSpace($env:COGNODB\_PASSWORD) -eq $false)

```



Run the benchmark:



```powershell

java -jar target\\cognodb-benchmark-1.0.0.jar

```



\---



\## Benchmark Configuration



The current benchmark configuration is:



```text

Warm-up iterations:       20

Measurement iterations:  100

Concurrent clients:       10

Read/write mix:          80/20

Nodes:                 91,489

Relationships:        200,000

```



The benchmark reuses the existing dataset during normal execution.



\---



\## Workloads



The benchmark currently measures the following workloads.



\### 1-Hop Traversal



Traverses outgoing `KNOWS` relationships from a selected starting node for one hop.



```text

(start)-\[:KNOWS]->(target)

```



\---



\### 2-Hop Traversal



Traverses two outgoing `KNOWS` relationships.



```text

(start)-\[:KNOWS]->()-\[:KNOWS]->(target)

```



\---



\### 3-Hop Traversal



Traverses three outgoing `KNOWS` relationships.



```text

(start)-\[:KNOWS]->()-\[:KNOWS]->()-\[:KNOWS]->(target)

```



The 3-hop query uses a bounded result set:



```text

LIMIT 100

```



This prevents the benchmark from unnecessarily enumerating a very large number of paths.



\---



\### Point Lookup



Finds a `Person` node using its indexed `id` property.



\---



\### Filtered Lookup



Finds `Person` nodes using the `name` property.



\---



\### Aggregation



Executes a graph aggregation query over `Person` nodes.



\---



\### Mixed Workload



The mixed workload executes operations concurrently using:



```text

Concurrent clients: 10

Read operations:    approximately 80%

Write operations:   approximately 20%

```



Performance is reported as queries per second.



\---



\## Indexes



The benchmark creates/verifies an index for:



```text

Person.id

```



The `id` index supports point lookup and traversal starting-node lookup operations.



The benchmark also performs filtered lookup using:



```text

Person.name

```



If a dedicated `name` index is added in a future revision, the README and benchmark implementation should be updated accordingly.



\---



\# CognoDB Results



The latest successful benchmark execution produced the following measurements:



| Workload        | p50 (ms) | p95 (ms) |

| --------------- | -------: | -------: |

| 1-hop           | 1228.467 | 1532.337 |

| 2-hop           | 1228.449 | 1594.486 |

| 3-hop           | 1227.496 | 1637.764 |

| Point lookup    | 1230.397 | 3199.029 |

| Filtered lookup | 1228.852 | 1945.404 |

| Aggregation     | 1228.654 | 1696.725 |



\### Mixed Workload



```text

6.912 queries/sec

```



These values are measurements from the current CognoDB benchmark execution.



They should \*\*not\*\* be interpreted as universal CognoDB performance characteristics.



Network latency, cloud instance resources, database configuration, dataset distribution, and workload characteristics can significantly affect benchmark results.



\---



\## Dataset



The benchmark currently uses:



```text

Nodes:          91,489

Relationships: 200,000

```



The current execution intentionally reused the existing CognoDB dataset.



The benchmark therefore reported:



```text

loadTimeMs = 0

nodesPerSecond = 0

relationshipsPerSecond = 0

```



These values \*\*do not mean that database loading took zero time\*\*.



They mean that dataset ingestion was \*\*not performed during the current benchmark execution\*\*.



The original dataset loading process took approximately 2.5 hours. Therefore, the current benchmark avoids clearing and reloading the existing benchmark dataset during every execution.



A separate clean-database ingestion benchmark would be required to measure:



\* Dataset loading time

\* Nodes per second

\* Relationships per second

\* Ingestion throughput



\---



\## Result File



The machine-readable benchmark result is stored at:



```text

results/cognodb.json

```



The result contains:



\* Database name

\* Node count

\* Relationship count

\* p50 latency

\* p95 latency

\* Mixed workload QPS

\* Benchmark notes



Example:



```json

{

&#x20; "database": "CognoDB",

&#x20; "nodeCount": 91489,

&#x20; "relationshipCount": 200000,

&#x20; "mixedQueriesPerSecond": 6.911550145447526

}

```



The complete benchmark result should be read from:



```text

results/cognodb.json

```



\---



\## Reproducibility



The benchmark uses a fixed dataset and fixed benchmark configuration.



Current configuration:



```text

Nodes:                 91,489

Relationships:        200,000

Warm-up iterations:       20

Measurement iterations:  100

Concurrency:              10

Read/write mix:        80/20

```



The same Java benchmark application can be executed again against the same database configuration.



Because cloud database performance can vary over time, repeated executions may produce different latency and throughput values.



\---



\## Database Adapter Architecture



The benchmark defines a common:



```text

DatabaseAdapter

```



interface.



The interface provides operations such as:



```text

connect()

clearDatabase()

loadDataset()

countNodes()

countRelationships()

createIndexes()

pointLookup()

filteredLookup()

traverse()

aggregation()

writeOperation()

close()

```



The adapter architecture allows the same logical benchmark workloads to be implemented for multiple graph databases.



\---



\## Current Adapter Status



The repository currently contains adapter classes for:



\* CognoDB

\* Neo4j

\* Memgraph

\* FalkorDB



\### CognoDB



The CognoDB adapter is the currently implemented and executed adapter.



\### Neo4j



The Neo4j adapter class currently exists in the repository.



It has not yet been independently benchmarked in the current submission.



\### Memgraph



The Memgraph adapter class currently exists in the repository.



It has not yet been independently benchmarked in the current submission.



\### FalkorDB



The FalkorDB adapter class currently exists in the repository.



It has not yet been independently benchmarked in the current submission.



The comparison database adapters should \*\*not\*\* be treated as independently measured implementations until they are connected to their respective database systems and executed using the same benchmark configuration.



\---



\## Limitations



The current benchmark has the following limitations:



1\. The current execution reused an existing dataset instead of measuring ingestion.

2\. Ingestion throughput is therefore not included in the current performance results.

3\. Only CognoDB has been executed and measured so far.

4\. The other database adapters have not yet been independently benchmarked.

5\. Cross-database performance comparisons should not be reported until the same workloads are executed against the comparison databases.

6\. CPU and memory consumption are not currently captured in the result JSON.

7\. Network latency and cloud resource variability may affect measured latency.

8\. The current benchmark reports latency and throughput but does not provide a complete cost-per-query analysis.



These limitations are documented to keep the benchmark results transparent and reproducible.



\---



\## Security



Never commit credentials to Git.



The following should remain local:



```text

.env

```



Credentials should be supplied through environment variables.



Before publishing the repository, verify:



```powershell

git status

```



Also inspect tracked files for accidentally committed credentials.



If credentials have previously been exposed, rotate them before publishing the repository.



\---



\## Current Result Summary



```text

&#x20;                   Dataset

&#x20;                      |

&#x20;                      v

&#x20;              91,489 nodes

&#x20;             200,000 relationships

&#x20;                      |

&#x20;                      v

&#x20;                   CognoDB

&#x20;                      |

&#x20;      +---------------+---------------+

&#x20;      |               |               |

&#x20;      v               v               v

&#x20;   1-hop            2-hop           3-hop

&#x20;   1028.7ms         1232.7ms        1242.2ms

&#x20;   p50              p50             p50

&#x20;      |

&#x20;      +--> Point Lookup

&#x20;      |    1220.7ms p50

&#x20;      |

&#x20;      +--> Filtered Lookup

&#x20;      |    1185.8ms p50

&#x20;      |

&#x20;      +--> Aggregation

&#x20;      |    1140.5ms p50

&#x20;      |

&#x20;      +--> Mixed Workload

&#x20;           6.912 queries/sec

&#x20;                      |

&#x20;                      v

&#x20;             results/cognodb.json

```



\---



\## Conclusion



The current implementation successfully demonstrates an end-to-end graph database benchmarking workflow against CognoDB.



The benchmark:



\* Connects to a cloud-hosted CognoDB instance

\* Reuses a fixed graph dataset

\* Verifies node and relationship counts

\* Creates/verifies indexes

\* Executes 1-hop traversal

\* Executes 2-hop traversal

\* Executes 3-hop traversal with a bounded result set

\* Executes point lookup

\* Executes filtered lookup

\* Executes aggregation

\* Executes a concurrent mixed workload

\* Measures p50 latency

\* Measures p95 latency

\* Measures mixed-workload throughput

\* Produces a machine-readable JSON result



The latest successful benchmark execution completed with:



```text

91,489 nodes

200,000 relationships



1-hop:

p50 = 1228.467 ms

p95 = 1532.337 ms



2-hop:

p50 = 1228.449 ms

p95 = 1594.486 ms



3-hop:

p50 = 1227.496 ms

p95 = 1637.764 ms



Point lookup:

p50 = 1230.397 ms

p95 = 3199.029 ms



Filtered lookup:

p50 = 1228.852 ms

p95 = 1945.404 ms



Aggregation:

p50 = 1228.654 ms

p95 = 1696.725 ms



Mixed workload:

6.912 queries/sec

```



This result represents the current CognoDB benchmark run and provides a baseline for future benchmarking against other graph database systems.



