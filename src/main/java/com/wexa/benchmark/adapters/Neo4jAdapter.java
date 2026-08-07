package com.wexa.benchmark.adapters;

import com.wexa.benchmark.BenchmarkConfig;
import com.wexa.benchmark.DatabaseAdapter;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Neo4jAdapter implements DatabaseAdapter {

    private static final int BATCH_SIZE = 1000;

    private final String uri;
    private final String username;
    private final String password;

    private Driver driver;

    public Neo4jAdapter(BenchmarkConfig config) {
        this.uri = config.getUri();
        this.username = config.getUsername();
        this.password = config.getPassword();
    }

    @Override
    public String getName() {
        return "Neo4j";
    }

    @Override
    public void connect() {

        driver = GraphDatabase.driver(
                uri,
                AuthTokens.basic(
                        username,
                        password
                )
        );

        driver.verifyConnectivity();

        System.out.println("Connected to Neo4j.");
    }

    private Session session() {

        if (driver == null) {
            throw new IllegalStateException(
                    "Neo4j database is not connected."
            );
        }

        return driver.session();
    }

    @Override
    public void clearDatabase() {

        try (Session session = session()) {

            session.executeWrite(tx -> {

                tx.run(
                        "MATCH (n) DETACH DELETE n"
                ).consume();

                return null;
            });
        }
    }

    @Override
    public long loadDataset(
            String nodeFile,
            String relationshipFile) {

        long start =
                System.currentTimeMillis();

        System.out.println();
        System.out.println("Loading nodes...");

        loadNodes(nodeFile);

        /*
         * Create the node ID index before loading
         * relationships so MATCH by id is efficient.
         */
        System.out.println("Creating/verifying indexes...");

        createIndexes();

        System.out.println("Loading relationships...");

        loadRelationships(relationshipFile);

        long elapsed =
                System.currentTimeMillis() - start;

        System.out.println();
        System.out.println(
                "Dataset loading completed in "
                        + elapsed
                        + " ms."
        );

        return elapsed;
    }

    private void loadNodes(String nodeFile) {

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(nodeFile)
                        );
                Session session = session()
        ) {

            String line;
            boolean firstLine = true;

            List<Map<String, Object>> batch =
                    new ArrayList<>(BATCH_SIZE);

            long loaded = 0;

            while ((line = reader.readLine()) != null) {

                if (firstLine) {

                    firstLine = false;

                    if (line.toLowerCase()
                            .contains("id")) {

                        continue;
                    }
                }

                String[] parts =
                        line.split(",", -1);

                if (parts.length < 2) {
                    continue;
                }

                long id =
                        Long.parseLong(
                                parts[0].trim()
                        );

                String name =
                        parts[1].trim();

                batch.add(
                        Map.of(
                                "id",
                                id,
                                "name",
                                name
                        )
                );

                if (batch.size() >= BATCH_SIZE) {

                    insertNodeBatch(
                            session,
                            batch
                    );

                    loaded += batch.size();

                    System.out.println(
                            "Nodes loaded: "
                                    + loaded
                    );

                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {

                insertNodeBatch(
                        session,
                        batch
                );

                loaded += batch.size();

                System.out.println(
                        "Nodes loaded: "
                                + loaded
                );
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load Neo4j nodes: "
                            + nodeFile,
                    e
            );
        }
    }

    private void insertNodeBatch(
            Session session,
            List<Map<String, Object>> batch) {

        session.executeWrite(tx -> {

            tx.run(
                    """
                    UNWIND $rows AS row
                    CREATE (n:Person {
                        id: row.id,
                        name: row.name
                    })
                    """,
                    Values.parameters(
                            "rows",
                            batch
                    )
            ).consume();

            return null;
        });
    }

    private void loadRelationships(
            String relationshipFile) {

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(
                                        relationshipFile
                                )
                        );
                Session session = session()
        ) {

            String line;
            boolean firstLine = true;

            List<Map<String, Object>> batch =
                    new ArrayList<>(BATCH_SIZE);

            long loaded = 0;

            while ((line = reader.readLine()) != null) {

                if (firstLine) {

                    firstLine = false;

                    String lower =
                            line.toLowerCase();

                    if (lower.contains("source")
                            || lower.contains("target")) {

                        continue;
                    }
                }

                String[] parts =
                        line.split(",", -1);

                if (parts.length < 2) {
                    continue;
                }

                long sourceId =
                        Long.parseLong(
                                parts[0].trim()
                        );

                long targetId =
                        Long.parseLong(
                                parts[1].trim()
                        );

                batch.add(
                        Map.of(
                                "sourceId",
                                sourceId,
                                "targetId",
                                targetId
                        )
                );

                if (batch.size() >= BATCH_SIZE) {

                    insertRelationshipBatch(
                            session,
                            batch
                    );

                    loaded += batch.size();

                    System.out.println(
                            "Relationships loaded: "
                                    + loaded
                    );

                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {

                insertRelationshipBatch(
                        session,
                        batch
                );

                loaded += batch.size();

                System.out.println(
                        "Relationships loaded: "
                                + loaded
                );
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load Neo4j relationships: "
                            + relationshipFile,
                    e
            );
        }
    }

    private void insertRelationshipBatch(
            Session session,
            List<Map<String, Object>> batch) {

        session.executeWrite(tx -> {

            tx.run(
                    """
                    UNWIND $rows AS row

                    MATCH (source:Person {
                        id: row.sourceId
                    })

                    MATCH (target:Person {
                        id: row.targetId
                    })

                    CREATE (source)-[:KNOWS]->(target)
                    """,
                    Values.parameters(
                            "rows",
                            batch
                    )
            ).consume();

            return null;
        });
    }

    @Override
    public long countNodes() {

        try (Session session = session()) {

            return session.executeRead(tx ->
                    tx.run(
                            "MATCH (n) RETURN count(n) AS count"
                    )
                    .single()
                    .get("count")
                    .asLong()
            );
        }
    }

    @Override
    public long countRelationships() {

        try (Session session = session()) {

            return session.executeRead(tx ->
                    tx.run(
                            "MATCH ()-[r]->() RETURN count(r) AS count"
                    )
                    .single()
                    .get("count")
                    .asLong()
            );
        }
    }

    @Override
    public void createIndexes() {

        try (Session session = session()) {

            session.executeWrite(tx -> {

                tx.run(
                        """
                        CREATE INDEX person_id_index
                        IF NOT EXISTS
                        FOR (n:Person)
                        ON (n.id)
                        """
                ).consume();

                return null;
            });
        }
    }

    @Override
    public void pointLookup(long nodeId) {

        try (Session session = session()) {

            session.executeRead(tx -> {

                tx.run(
                        """
                        MATCH (n:Person {id: $id})
                        RETURN n
                        LIMIT 1
                        """,
                        Values.parameters(
                                "id",
                                nodeId
                        )
                ).consume();

                return null;
            });
        }
    }

    @Override
    public void filteredLookup(String name) {

        try (Session session = session()) {

            session.executeRead(tx -> {

                tx.run(
                        """
                        MATCH (n:Person)
                        WHERE n.name = $name
                        RETURN n
                        LIMIT 10
                        """,
                        Values.parameters(
                                "name",
                                name
                        )
                ).consume();

                return null;
            });
        }
    }

    @Override
    public void traverse(
            int hops,
            long startNodeId) {

        String query;

        if (hops == 1) {

            query =
                    """
                    MATCH (start:Person {id: $id})
                    MATCH (start)-[:KNOWS]->(target)
                    RETURN target
                    LIMIT 100
                    """;

        } else if (hops == 2) {

            query =
                    """
                    MATCH (start:Person {id: $id})
                    MATCH (start)-[:KNOWS]->()
                          -[:KNOWS]->(target)
                    RETURN target
                    LIMIT 100
                    """;

        } else if (hops == 3) {

            query =
                    """
                    MATCH (start:Person {id: $id})
                    MATCH (start)-[:KNOWS]->()
                          -[:KNOWS]->()
                          -[:KNOWS]->(target)
                    RETURN target
                    LIMIT 100
                    """;

        } else {

            throw new IllegalArgumentException(
                    "Unsupported hop depth: " + hops
            );
        }

        try (Session session = session()) {

            session.executeRead(tx -> {

                tx.run(
                        query,
                        Values.parameters(
                                "id",
                                startNodeId
                        )
                ).consume();

                return null;
            });
        }
    }

    @Override
    public void aggregation() {

        try (Session session = session()) {

            session.executeRead(tx -> {

                tx.run(
                        """
                        MATCH (n:Person)
                        RETURN count(n) AS total
                        """
                ).consume();

                return null;
            });
        }
    }

    @Override
    public void writeOperation(long nodeId) {

        try (Session session = session()) {

            session.executeWrite(tx -> {

                tx.run(
                        """
                        MATCH (n:Person {id: $id})
                        SET n.benchmarkWrite = true
                        """,
                        Values.parameters(
                                "id",
                                nodeId
                        )
                ).consume();

                return null;
            });
        }
    }

    @Override
    public void close() {

        if (driver != null) {

            driver.close();
            driver = null;
        }
    }
}