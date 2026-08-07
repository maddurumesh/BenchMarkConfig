package com.wexa.benchmark.adapters;

import com.wexa.benchmark.DatabaseAdapter;
import com.wexa.benchmark.BenchmarkConfig;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CognoDBAdapter implements DatabaseAdapter {

    private final String uri;
    private final String username;
    private final String password;

    private Driver driver;

    public CognoDBAdapter(BenchmarkConfig config) {
        this.uri = config.getUri();
        this.username = config.getUsername();
        this.password = config.getPassword();
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required environment variable: " + name
            );
        }

        return value;
    }

    @Override
    public String getName() {
        return "CognoDB";
    }

    @Override
    public void connect() {
        driver = GraphDatabase.driver(
                uri,
                org.neo4j.driver.AuthTokens.basic(
                        username,
                        password
                )
        );

        driver.verifyConnectivity();

        System.out.println("Connected.");
    }

    private Session session() {
        if (driver == null) {
            throw new IllegalStateException(
                    "Database is not connected."
            );
        }

        return driver.session();
    }

    @Override
    public void clearDatabase() {
        try (Session session = session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (n) DETACH DELETE n").consume();
                return null;
            });
        }
    }

    @Override
    public long loadDataset(
            String nodeFile,
            String relationshipFile) {

        long startTime = System.currentTimeMillis();

        loadNodes(nodeFile);
        loadRelationships(relationshipFile);

        return System.currentTimeMillis() - startTime;
    }

    private void loadNodes(String nodeFile) {

        String query = """
                CREATE (n:Person {
                    id: $id,
                    name: $name
                })
                """;

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(nodeFile)
                        );
                Session session = session()
        ) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {

                if (firstLine) {
                    firstLine = false;

                    if (line.toLowerCase().contains("id")) {
                        continue;
                    }
                }

                String[] parts = line.split(",", -1);

                if (parts.length < 2) {
                    continue;
                }

                long id =
                        Long.parseLong(parts[0].trim());

                String name =
                        parts[1].trim();

                session.executeWrite(tx -> {

                    tx.run(
                            query,
                            Values.parameters(
                                    "id", id,
                                    "name", name
                            )
                    ).consume();

                    return null;
                });
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load nodes: " + nodeFile,
                    e
            );
        }
    }

    private void loadRelationships(String relationshipFile) {

        String query = """
                MATCH (source:Person {id: $sourceId})
                MATCH (target:Person {id: $targetId})
                CREATE (source)-[:KNOWS]->(target)
                """;

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(relationshipFile)
                        );
                Session session = session()
        ) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {

                if (firstLine) {
                    firstLine = false;

                    String lower = line.toLowerCase();

                    if (lower.contains("source")
                            || lower.contains("target")) {
                        continue;
                    }
                }

                String[] parts = line.split(",", -1);

                if (parts.length < 2) {
                    continue;
                }

                long sourceId =
                        Long.parseLong(parts[0].trim());

                long targetId =
                        Long.parseLong(parts[1].trim());

                session.executeWrite(tx -> {

                    tx.run(
                            query,
                            Values.parameters(
                                    "sourceId", sourceId,
                                    "targetId", targetId
                            )
                    ).consume();

                    return null;
                });
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load relationships: "
                            + relationshipFile,
                    e
            );
        }
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

        String query = """
                MATCH (n:Person {id: $id})
                RETURN n
                LIMIT 1
                """;

        try (Session session = session()) {

            session.executeRead(tx -> {

                tx.run(
                        query,
                        Values.parameters(
                                "id", nodeId
                        )
                ).consume();

                return null;
            });
        }
    }

    @Override
    public void filteredLookup(String name) {

        String query = """
                MATCH (n:Person)
                WHERE n.name = $name
                RETURN n
                LIMIT 10
                """;

        try (Session session = session()) {

            session.executeRead(tx -> {

                tx.run(
                        query,
                        Values.parameters(
                                "name", name
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

            query = """
                    MATCH (start:Person {id: $id})
                    MATCH (start)-[:KNOWS]->(target)
                    RETURN target
                    LIMIT 100
                    """;

        } else if (hops == 2) {

            query = """
                    MATCH (start:Person {id: $id})
                    MATCH (start)-[:KNOWS]->()
                          -[:KNOWS]->(target)
                    RETURN target
                    LIMIT 100
                    """;

        } else if (hops == 3) {

            query = """
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

        String query = """
                MATCH (n:Person)
                RETURN count(n) AS total
                """;

        try (Session session = session()) {

            session.executeRead(tx -> {

                tx.run(query).consume();

                return null;
            });
        }
    }

    @Override
    public void writeOperation(long nodeId) {

        String query = """
                MATCH (n:Person {id: $id})
                SET n.benchmarkWrite = true
                """;

        try (Session session = session()) {

            session.executeWrite(tx -> {

                tx.run(
                        query,
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