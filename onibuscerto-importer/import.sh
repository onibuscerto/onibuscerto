#!/bin/sh
rm -rfi target/db
mvn exec:java -Dexec.mainClass="com.onibuscerto.importer.ImporterMain"
