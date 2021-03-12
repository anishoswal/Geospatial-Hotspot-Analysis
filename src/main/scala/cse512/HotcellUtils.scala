package cse512

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar

object HotcellUtils {
  val coordinateStep = 0.01

  def CalculateCoordinate(inputString: String, coordinateOffset: Int): Int =
  {
    // Configuration variable:
    // Coordinate step is the size of each cell on x and y
    var result = 0
    coordinateOffset match
    {
      case 0 => result = Math.floor((inputString.split(",")(0).replace("(","").toDouble/coordinateStep)).toInt
      case 1 => result = Math.floor(inputString.split(",")(1).replace(")","").toDouble/coordinateStep).toInt
      // We only consider the data from 2009 to 2012 inclusively, 4 years in total. Week 0 Day 0 is 2009-01-01
      case 2 => {
        val timestamp = HotcellUtils.timestampParser(inputString)
        result = HotcellUtils.dayOfMonth(timestamp) // Assume every month has 31 days
      }
    }
    return result
  }

  def timestampParser (timestampString: String): Timestamp =
  {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    val parsedDate = dateFormat.parse(timestampString)
    val timeStamp = new Timestamp(parsedDate.getTime)
    return timeStamp
  }

  def dayOfYear (timestamp: Timestamp): Int =
  {
    val calendar = Calendar.getInstance
    calendar.setTimeInMillis(timestamp.getTime)
    return calendar.get(Calendar.DAY_OF_YEAR)
  }

  def dayOfMonth (timestamp: Timestamp): Int =
  {
    val calendar = Calendar.getInstance
    calendar.setTimeInMillis(timestamp.getTime)
    return calendar.get(Calendar.DAY_OF_MONTH)
  }

  // YOU NEED TO CHANGE THIS PART
  def totalNeighbours(x: Int, y: Int, z:Int, xMin: Int, yMin: Int, zMin: Int, xMax: Int, yMax: Int, zMax: Int): Int =
  {
    var noN = 27
    var offI = 0
    val off = List(9, 6, 4)
    if (x==xMin|x==xMax)
    {
      noN-=off(offI)
      offI+=1
    }
    if (y==yMin|y==yMax)
    {
      noN-=off(offI)
      offI+=1
    }
    if (z==zMin|z==zMax)
    {
      noN-=off(offI)
      offI+=1
    }
    noN = noN.toInt
    return (noN)
  }

  def calcZ(sumNPt: Int, xMean: Double, noN: Int, std: Double, numCells: Int): Double =
  {
    val n = (sumNPt - (xMean * noN))
    val d = (std * Math.sqrt(((numCells * noN) - Math.pow(noN,2))/(numCells-1)))
    val zscore = n/d
    return (zscore.toDouble)
  }
}