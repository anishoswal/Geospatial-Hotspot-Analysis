# Geospatial Hotspot Analysis on Apache Sedona


In this project, we do spatial hotspot analysis. 

### Hot zone analysis
We perform a range join operation on a rectangle datasets and a point dataset. For each rectangle, the number of points located within the rectangle are obtained. The hotter rectangle means that it included more points. So here we calculate the hotness of all the rectangles. 

### Hot cell analysis

#### Description
This task focuses on applying spatial statistics to spatio-temporal big data in order to identify statistically significant spatial hot spots using Apache Spark. The topic of this task is from ACM SIGSPATIAL GISCUP 2016.

The Problem Definition page is here: [http://sigspatial2016.sigspatial.org/giscup2016/problem](http://sigspatial2016.sigspatial.org/giscup2016/problem) 

#### Special requirement (different from GIS CUP)
As stated in the Problem Definition page, in this task, we were asked to implement a Spark program to calculate the Getis-Ord statistic of NYC Taxi Trip datasets. We call it "**Hot cell analysis**"

## Code Specification

### Input parameters

1. Output path (Mandatory)
2. Task name: "hotzoneanalysis" or "hotcellanalysis"
3. Task parameters: (1) Hot zone (2 parameters): nyc taxi data path, zone path(2) Hot cell (1 parameter): nyc taxi data path

Example
```
test/output hotzoneanalysis src/resources/point-hotzone.csv src/resources/zone-hotzone.csv hotcellanalysis src/resources/yellow_trip_sample_100000.csv
```


### Input data format
The main function/entrace is "cse512.Entrance" scala file.

1. Point data: the input point dataset is the pickup point of New York Taxi trip datasets. The data format of this phase is the original format of NYC taxi trip.

2. Zone data (only for hot zone analysis): at "src/resources/zone-hotzone" of the template

#### Hot zone analysis
The input point data can be any small subset of NYC taxi dataset.

#### Hot cell analysis
The input point data is a monthly NYC taxi trip dataset (2009-2012) like "yellow\_tripdata\_2009-01\_point.csv"

### Output data format

#### Hot zone analysis
All zones with their count, sorted by "rectangle" string in an ascending order. 

```
"-73.795658,40.743334,-73.753772,40.779114",1
"-73.797297,40.738291,-73.775740,40.770411",1
"-73.832707,40.620010,-73.746541,40.665414",20
```


#### Hot cell analysis
The coordinates of top 50 hotest cells sorted by their G score in a descending order. Note, WE DO NOT OUTPUT G score.

```
-7399,4075
-7399,4075
-7399,4075
```
### Example answers
An example input and answer can be found in "testcase" folder.

1. hotcell-example-answer-withZscore-sample.csv -- answer of the sampled input data with score.
2. hotcell-example-answer-withZscore.csv -- answer with score of the full input data from Google Drive shared folder.
3. hotcell-example-answer.csv -- the expected output result of using the yellow_tripdata_2009-01_point.csv as the input.
4. hotcell-example-input.txt -- the input for running the jar file.
