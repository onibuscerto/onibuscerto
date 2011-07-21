#!/bin/sh
rm -rfi target/db
mvn exec:java -e -Dexec.mainClass="com.onibuscerto.importer.ImporterMain"
