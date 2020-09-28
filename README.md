# Timeseries Integration Timescale Plugin [![CircleCI](https://circleci.com/gh/opennms-forge/timeseries-integration-timescale.svg?style=svg)](https://circleci.com/gh/opennms-forge/timeseries-integration-timescale)

This plugin exposes an implementation of the TimeSeriesStorage interface.
It stores the data in a timescale database.
It can be used in OpenNMS to store and retrieve timeseries data.

## Prerequisite
* The timescale plugin must be installed in the postgres database of opennms.
* For testing purposes you can run: ``sudo docker run -p 5432:5432 -e POSTGRES_PASSWORD=password timescale/timescaledb:latest-pg12``

## Usage
* compile: ``mvn install``
* activation: Enable the Time Series Storage layer: http://docs.opennms.org/opennms/releases/26.1.0/guide-admin/guide-admin.html#_configuration_4
* activate in Karaf shell: ``bundle:install -s mvn:org.opennms.plugins.timeseries.timescale/timeseries-timescale-plugin/1.0.0-SNAPSHOT``

## Links:
* Introduction to the Time Series Storage Layer: http://docs.opennms.org/opennms/releases/26.1.0/guide-admin/guide-admin.html#ga-opennms-operation-timeseries
* Timescale: https://www.timescale.com/




