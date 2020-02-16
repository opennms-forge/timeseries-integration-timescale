# Timeseries Integration Timescale Plugin

This plugin exposes an implementation of the TimeSeriesStorage interface.
It stores the data in a timescale database.
It can be used in OpenNMS to store and retrieve timeseries data.

## Prerequisite
* The timescale plugin must be installed in the postgres database of opennms.
* For testing purposes you can run: ``sudo docker run -p 5432:5432 -e POSTGRES_PASSWORD=password timescale/timescaledb:latest-pg11``

## Usage
* compile: ``mvn install``
* activation: Enable the timeseries integration layer: TODO: Patrick add link once the documentation is online
* activate in Karaf shell: ``bundle:install -s mvn:org.opennms.plugins.timeseries.timescale/timeseries-timescale-plugin/1.0.0-SNAPSHOT``
* run command to set up database tables: 

## Links:
* Timescale: https://www.timescale.com/



