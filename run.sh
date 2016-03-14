#!/bin/bash
if [ $# -ne 1 ]; then
  echo "Usage: run.sh [data file path]"
  echo "Example:"
  echo "run.sh \"hdfs://node1:54310/KDDn100Kd38_Y.csv\""
  echo "run.sh \"hdfs://node1:54310/KDDn001Md38_Y.csv\""
  echo "run.sh \"hdfs://node1:54310/KDDn010Md38_Y.csv\""
  echo "run.sh \"hdfs://node1:54310/KDDn100Md38_Y.csv\""
  exit -1;
fi
cd "$( dirname "${BASH_SOURCE[0]}" )"
spark-submit --class GammaSpark target/scala-2.10/gammaspark_2.10-1.0.jar $1
