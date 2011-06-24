#!/bin/sh

# This file will start the Osm Extraction UI with custom memory settings for
# the JVM. With the below settings the heap size (Available memory for the application)
# will range from 64 megabyte up to 720 megabyte.

java -Djava.util.logging.config.file=logging.properties -Xms64M -Xmx720M -cp "./OsmAndMapCreator.jar:./lib/*.jar" net.osmand.swing.OsmExtractionUI
