GammaByte <- SparkR:::callJStatic("GammaSpark", "Gamma", sc, "hdfs://node1:54310/KDDn100Kd38_Y.csv")
Gamma <- matrix(unlist(Gamma), ncol=sqrt(length(Gamma)))
