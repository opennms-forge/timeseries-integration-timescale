# Timeseries Integration Timescale Plugin [![CircleCI](https://circleci.com/gh/opennms-forge/timeseries-integration-timescale.svg?style=svg)](https://circleci.com/gh/opennms-forge/timeseries-integration-timescale)

This plugin exposes an implementation of the TimeSeriesStorage interface.
It stores the data in a timescale database.
It can be used in OpenNMS to store and retrieve timeseries data.

## Prerequisite
* The timescale plugin must be installed in the postgres database of opennms.
* For testing purposes you can run: ``sudo docker run -p 5432:5432 -e POSTGRES_PASSWORD=password timescale/timescaledb:latest-pg12``

## Usage
### enable the Time Series Storage layer
* In opennms deploy root folder: ``echo "org.opennms.timeseries.strategy=integration" >> etc/opennms.properties.d/timescale.properties``
### Compile from source
* compile: ``mvn install``
* copy the timeseries-timescale-plugin.kar from the ./assembly/kar/target folder ot $OPENNMS_HOME/deploy
###
Download and install Release
* download the latest release and put it in the deploy folder of your OpenNMS installation, e.g. sudo wget https://github.com/opennms-forge/timeseries-integration-timescale/releases/download/v0.2.0/timeseries-timescale-plugin.kar -P $OPENNMS_HOME/deploy/
### activate in Karaf shell:
  * ``ssh -p 8101 admin@localhost``
  * ``feature:install opennms-plugins-timeseries-timescale-plugin``
  * The plugin will automatically create the necessary tables if they don't already exist.

## Links:
* Introduction to the Time Series Storage Layer: https://docs.opennms.com/horizon/28.1.0/operation/operation/timeseries/introduction.html
* Timescale: https://www.timescale.com/




