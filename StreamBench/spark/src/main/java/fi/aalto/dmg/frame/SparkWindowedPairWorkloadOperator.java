package fi.aalto.dmg.frame;

import fi.aalto.dmg.exceptions.UnsupportOperatorException;
import fi.aalto.dmg.frame.functions.*;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import scala.Tuple2;

import java.util.Iterator;

/**
 * Created by jun on 11/3/15.
 */
public class SparkWindowedPairWorkloadOperator<K, V> extends WindowedPairWorkloadOperator<K, V> {

    private static final long serialVersionUID = 7216177060503270778L;
    private JavaPairDStream<K, V> pairDStream;

    public SparkWindowedPairWorkloadOperator(JavaPairDStream<K, V> stream, int parallelism) {
        super(parallelism);
        this.pairDStream = stream;
    }

    @Override
    public PairWorkloadOperator<K, V> reduceByKey(ReduceFunction<V> fun,
                                                  String componentId) {
        JavaPairDStream<K, V> newStream = this.pairDStream.reduceByKey(new ReduceFunctionImpl<>(fun));
        return new SparkPairWorkloadOperator<>(newStream, parallelism);
    }

    @Override
    public PairWorkloadOperator<K, V> updateStateByKey(ReduceFunction<V> fun,
                                                       String componentId) {
        JavaPairDStream<K, V> cumulateStream = this.pairDStream.updateStateByKey(new UpdateStateFunctionImpl<>(fun));
        return new SparkPairWorkloadOperator<>(cumulateStream, parallelism);
    }

    @Override
    public <R> PairWorkloadOperator<K, R> mapPartition(MapPartitionFunction<Tuple2<K, V>, Tuple2<K, R>> fun,
                                                       String componentId) {
        JavaPairDStream<K, R> newStream = pairDStream.mapPartitionsToPair(new PairMapPartitionFunctionImpl<>(fun));
        return new SparkPairWorkloadOperator<>(newStream, parallelism);
    }

    @Override
    public <R> PairWorkloadOperator<K, R> mapValue(MapFunction<Tuple2<K, V>, Tuple2<K, R>> fun,
                                                   String componentId) {
        return null;
    }

    @Override
    public PairWorkloadOperator<K, V> filter(FilterFunction<Tuple2<K, V>> fun,
                                             String componentId) {
        return null;
    }

    @Override
    public PairWorkloadOperator<K, V> reduce(ReduceFunction<Tuple2<K, V>> fun,
                                             String componentId) {
        return null;
    }

    @Override
    public void closeWith(OperatorBase stream, boolean broadcast) throws UnsupportOperatorException {
        throw new UnsupportOperatorException("don't support operator");
    }

    @Override
    public void print() {
        pairDStream.print();
    }
}
