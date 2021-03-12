package cse512

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions._
import org.apache.spark.sql.DataFrame

object HotcellAnalysis {
  Logger.getLogger("org.spark_project").setLevel(Level.WARN)
  Logger.getLogger("org.apache").setLevel(Level.WARN)
  Logger.getLogger("akka").setLevel(Level.WARN)
  Logger.getLogger("com").setLevel(Level.WARN)

def runHotcellAnalysis(spark: SparkSession, pointPath: String): DataFrame =
{
  // Load the original data from a data source
  var pickupInfo = spark.read.format("csv").option("delimiter",";").option("header","false").load(pointPath);
  pickupInfo.createOrReplaceTempView("nyctaxitrips")
  pickupInfo.show()

  // Assign cell coordinates based on pickup points
  spark.udf.register("CalculateX",(pickupPoint: String)=>((
    HotcellUtils.CalculateCoordinate(pickupPoint, 0)
    )))
  spark.udf.register("CalculateY",(pickupPoint: String)=>((
    HotcellUtils.CalculateCoordinate(pickupPoint, 1)
    )))
  spark.udf.register("CalculateZ",(pickupTime: String)=>((
    HotcellUtils.CalculateCoordinate(pickupTime, 2)
    )))
  pickupInfo = spark.sql("select CalculateX(nyctaxitrips._c5),CalculateY(nyctaxitrips._c5), CalculateZ(nyctaxitrips._c1) from nyctaxitrips")
  var newCoordinateName = Seq("x", "y", "z")
  pickupInfo = pickupInfo.toDF(newCoordinateName:_*)
  pickupInfo.show()

  // Define the min and max of x, y, z
  val minX = -74.50/HotcellUtils.coordinateStep
  val maxX = -73.70/HotcellUtils.coordinateStep
  val minY = 40.50/HotcellUtils.coordinateStep
  val maxY = 40.90/HotcellUtils.coordinateStep
  val minZ = 1
  val maxZ = 31
  val numCells = (maxX - minX + 1)*(maxY - minY + 1)*(maxZ - minZ + 1)

  // YOU NEED TO CHANGE THIS PART
  pickupInfo = pickupInfo.groupBy("x", "y", "z").count().withColumnRenamed("count", "pointsCount")
  pickupInfo.createOrReplaceTempView("pickupInfo")
  val d = numCells.toDouble
  val n = pickupInfo.agg(sum("pointsCount")).first().getLong(0).toDouble
  val mx = n/d

  val std = Math.sqrt((pickupInfo.select(pow("pointsCount",2) as "squarePointsCount").agg(sum("squarePointsCount")).first().getDouble(0) / numCells.toDouble) - Math.pow(mx, 2))
  pickupInfo = spark.sql("SELECT PI1.x, PI1.y, PI1.z, SUM(PI2.pointsCount) AS sumNPt FROM pickupInfo AS PI1, pickupInfo AS PI2 WHERE PI2.x BETWEEN PI1.x-1 AND PI1.x+1 AND PI2.y BETWEEN PI1.y-1 AND PI1.y+1 AND PI2.z BETWEEN PI1.z-1 AND PI1.z+1 GROUP BY PI1.x, PI1.y, PI1.z")
  pickupInfo.createOrReplaceTempView("pickupInfo")
  spark.udf.register("totalNeighbours", (x: Int, y: Int, z:Int,xMin: Int, yMin: Int, zMin: Int,xMax: Int, yMax: Int, zMax: Int)=>(HotcellUtils.totalNeighbours( x, y, z, xMin, yMin, zMin, xMax, yMax, zMax)))
  pickupInfo = spark.sql("SELECT x, y, z, totalNeighbours(x, y, z, "+minX+","+minY+","+minZ+","+maxX+","+maxY+","+maxY+") as noN, sumNPt FROM pickupInfo")
  pickupInfo.createOrReplaceTempView("pickupInfo")
  spark.udf.register("calcZ", (sumNPt: Int, mx: Double, noN: Int,std: Double, numCells: Int)=>(HotcellUtils.calcZ(sumNPt, mx, noN, std, numCells)))
  pickupInfo = spark.sql("SELECT x, y, z, calcZ(pickupInfo.sumNPt, "+mx+", pickupInfo.noN, "+std+", "+numCells+") AS zscore "+"FROM pickupInfo").orderBy(desc("zscore")).select("x", "y").limit(50)
  pickupInfo.show(50)
  return pickupInfo
}
}
