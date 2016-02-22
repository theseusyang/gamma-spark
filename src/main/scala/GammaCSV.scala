import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.distributed._
import org.apache.spark.mllib.linalg._

object GammaCSV {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("GammaCSV")
    val sc = new SparkContext(conf)
    val filePath: String = ("hdfs://node1:54310/KDDn100Kd38_Y.csv")
    val Z = sc.textFile(filePath).map { line =>
      val values = line.split(',').map(_.toDouble)
      Vectors.dense(Array[Double](1.0) ++ values)
    }
    val matZ = new RowMatrix(Z)
    val Gamma = new GammaMatrix(matZ.numCols.toInt, matZ.numCols.toInt, matZ.computeGramianMatrix().toArray, true)
    println("Gamma:\n" + Gamma)    
    println("n="+Gamma.n)
    println("L[3]="+Gamma.L(3))
    println("Q[3,3]="+Gamma.Q(3,3))
    sc.stop()
  }
}
