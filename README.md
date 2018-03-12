# JGraphT-with-OSM-Data
A program that loads in OSM Data from files, and constructs a Graph data structure using JGraphT.

The data is pulled and parsed from OSM data files that are generated from larger OSM files using the command line tool osmconvert.
This data is then stored in a postgres database. The data is then used to create a Graph structure using custom Node and Edge classes.

This program was simply a test to see how OSMData can be abstracted to a Graph structure. It is a part of my 4th year project, and I wanted to see how it would work in a standalone Java program before incorporating it into Android.

The libraries required are:
*JGraphT (core)
*dom7j
*opencsv
*postgresql
*jaxen
*commons-lang
