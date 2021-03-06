# Timeseries Integration Timescale Plugin [![CircleCI](https://circleci.com/gh/opennms-forge/timeseries-integration-timescale.svg?style=svg)](https://circleci.com/gh/opennms-forge/timeseries-integration-timescale)

This plugin exposes an implementation of the TimeSeriesStorage interface.
It stores the data in a timescale database.
It can be used in OpenNMS to store and retrieve timeseries data.

## Prerequisite
* The timescale plugin must be installed in the postgres database of opennms.
* For testing purposes you can run: ``sudo docker run -p 5432:5432 -e POSTGRES_PASSWORD=password timescale/timescaledb:latest-pg12``

## Usage
### Compile from source
* compile: ``mvn install``
* enable the Time Series Storage layer: http://docs.opennms.org/opennms/releases/26.1.0/guide-admin/guide-admin.html#_configuration_4
* activate in Karaf shell:
  * ``ssh -p 8101 admin@localhost``
  * ``bundle:install -s mvn:org.opennms.plugins.timeseries.timescale/timeseries-timescale-plugin/1.0.0-SNAPSHOT``
  * The plugin will automatically create the necessary tables if they don't already exist.

###
Download and install Release
* download a [release](./releases) and put it in the deploy folder of your OpenNMS installation, e.g. sudo wget https://github.com/opennms-forge/timeseries-integration-timescale/releases/download/v0.1.0/timeseries-timescale-plugin.kar -P /opt/opennms/deploy/
* activate in Karaf shell:
  * ``ssh -p 8101 admin@localhost``
  * ``feature:install opennms-plugins-timeseries-timescale-plugin``
  * The plugin will automatically create the necessary tables if they don't already exist.

## Links:
* Introduction to the Time Series Storage Layer: http://docs.opennms.org/opennms/releases/26.1.0/guide-admin/guide-admin.html#ga-opennms-operation-timeseries
* Timescale: https://www.timescale.com/




