import csv
import gzip
import os
import sys

INPUT_FILE = os.path.join(
    "data",
    "soc-pokec-relationships.txt.gz"
)

OUTPUT_NODES = os.path.join(
    "data",
    "nodes.csv"
)

OUTPUT_RELATIONSHIPS = os.path.join(
    "data",
    "relationships.csv"
)

MAX_RELATIONSHIPS = 200_000


def generate_dataset():

    if not os.path.exists(INPUT_FILE):

        print("ERROR: Dataset file not found:")
        print(INPUT_FILE)
        print()

        print("Download it first using:")
        print(
            "Invoke-WebRequest "
            "-Uri "
            '"https://snap.stanford.edu/data/soc-pokec-relationships.txt.gz" '
            "-OutFile "
            '"data\\soc-pokec-relationships.txt.gz"'
        )

        sys.exit(1)

    print("Reading SNAP soc-Pokec dataset...")
    print(
        f"Selecting first "
        f"{MAX_RELATIONSHIPS:,} relationships..."
    )

    nodes = set()
    relationships = []

    with gzip.open(
        INPUT_FILE,
        "rt",
        encoding="utf-8"
    ) as file:

        for line in file:

            line = line.strip()

            if not line or line.startswith("#"):
                continue

            parts = line.split()

            if len(parts) < 2:
                continue

            source = int(parts[0])
            target = int(parts[1])

            relationships.append(
                (source, target)
            )

            nodes.add(source)
            nodes.add(target)

            if len(relationships) >= MAX_RELATIONSHIPS:
                break

    print(
        f"Relationships selected: "
        f"{len(relationships):,}"
    )

    print(
        f"Unique nodes found:      "
        f"{len(nodes):,}"
    )

    print("Writing nodes.csv...")

    with open(
        OUTPUT_NODES,
        "w",
        newline="",
        encoding="utf-8"
    ) as file:

        writer = csv.writer(file)

        writer.writerow(
            ["id", "name"]
        )

        for node_id in sorted(nodes):

            writer.writerow(
                [
                    node_id,
                    f"User-{node_id}"
                ]
            )

    print("Writing relationships.csv...")

    with open(
        OUTPUT_RELATIONSHIPS,
        "w",
        newline="",
        encoding="utf-8"
    ) as file:

        writer = csv.writer(file)

        writer.writerow(
            ["source", "target"]
        )

        for source, target in relationships:

            writer.writerow(
                [
                    source,
                    target
                ]
            )

    print()
    print("========================================")
    print("Dataset generation complete")
    print("========================================")

    print(
        f"Nodes:         {len(nodes):,}"
    )

    print(
        f"Relationships: {len(relationships):,}"
    )

    print(
        f"Nodes file:    {OUTPUT_NODES}"
    )

    print(
        f"Edges file:    {OUTPUT_RELATIONSHIPS}"
    )

    print("========================================")


if __name__ == "__main__":
    generate_dataset()
