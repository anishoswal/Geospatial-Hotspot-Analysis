package cse512

object HotzoneUtils {

  def ST_Contains(queryRectangle: String, pointString: String ): Boolean =
  {

    var r = new Array[String](4)
    r = queryRectangle.split(",")
    var rx1 = r(0).trim.toDouble
    var ry1 = r(1).trim.toDouble
    var rx2 = r(2).trim.toDouble
    var ry2 = r(3).trim.toDouble
    var pt = new Array[String](2)
    pt= pointString.split(",")
    var px=pt(0).trim.toDouble
    var py=pt(1).trim.toDouble
    var lowx = math.min(rx1, rx2)
    var highx = math.max(rx1, rx2)
    var lowy = math.min(ry1, ry2)
    var highy = math.max(ry1, ry2)
    if(py > highy || px < lowx || px > highx || py < lowy)
    {return false}
    else
    {return true}
  }
}