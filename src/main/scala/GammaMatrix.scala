/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.mllib.linalg

import java.util.{Arrays, Random}

import scala.collection.mutable.{ArrayBuffer, ArrayBuilder => MArrayBuilder, HashSet => MHashSet}

import breeze.linalg.{CSCMatrix => BSM, DenseMatrix => BDM, Matrix => BM}

import org.apache.spark.annotation.{DeveloperApi, Since}
import org.apache.spark.sql.catalyst.expressions.GenericMutableRow
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.util.GenericArrayData
import org.apache.spark.sql.types._

/**
 * Column-major dense matrix.
 * The entry values are stored in a single array of doubles with columns listed in sequence.
 * For example, the following matrix
 * {{{
 *   1.0 2.0
 *   3.0 4.0
 *   5.0 6.0
 * }}}
 * is stored as `[1.0, 3.0, 5.0, 2.0, 4.0, 6.0]`.
 *
 * @param numRows number of rows
 * @param numCols number of columns
 * @param values matrix entries in column major if not transposed or in row major otherwise
 * @param isTransposed whether the matrix is transposed. If true, `values` stores the matrix in
 *                     row major.
 */

class GammaMatrix (
    override val numRows: Int,
    override val numCols: Int,
    override val values: Array[Double],
    val includesY: Boolean,
    var d: Int) extends DenseMatrix(numRows, numCols, values, false) {

  require(values.length == numRows * numCols, "The number of values supplied doesn't match the " +
    s"size of the matrix! values.length: ${values.length}, numRows * numCols: ${numRows * numCols}")

  def this(numRows: Int, numCols: Int, values: Array[Double], includesY: Boolean) = {
    this(numRows, numCols, values, includesY, if(includesY) numCols-2 else numCols-1)
  }

  def n: Double = values(0)

  def L(i: Int): Double = {
    // require(i>=0 && i<d, "i needs to be greater than zero and less or equal to d ($d).")
    values(i+1)
  }

  def Q(i: Int, j: Int): Double = {
    // require(i>=0 && i<d && j>0 && j<=d, "i and j needs to be greater than zero and less or equal to d ($d).")
    values((j+1) * numRows + i+1)
  }

  def corr: Matrix = {
    val rho = new BDM[Double](d, d)
    val diagQ = new Array[Double](d)

    for (a <- 0 to d-1) {
      diagQ(a) = Q(a,a)
      val La = L(a)
      val LaLa = La*La
      for (b <- 0 to a) {
        val Lb = L(b)
        val denominator = scala.math.sqrt(n*diagQ(a)-LaLa) * scala.math.sqrt(n*diagQ(b)-Lb*Lb)
        if (denominator != 0)
          rho(a,b) = (n*Q(a,b) - La*Lb) / denominator
        else
          rho(a,b) = Double.NaN
        rho(b,a) = rho(a,b)
      }
    }
    Matrices.dense(d, d, rho.data)
  }
}
