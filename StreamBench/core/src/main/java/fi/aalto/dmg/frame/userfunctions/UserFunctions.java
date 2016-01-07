package fi.aalto.dmg.frame.userfunctions;

import com.google.common.base.Optional;
import fi.aalto.dmg.frame.functions.*;
import fi.aalto.dmg.util.WithTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.*;

/**
 * Created by yangjun.wang on 21/10/15.
 */
public class UserFunctions {

    public static Iterable<String> split(String str){
        // TODO: trim()
        return Arrays.asList(str.split("\\W+"));
    }

    public static <T extends Number> Double sum(T t1, T t2){
        return t1.doubleValue() + t2.doubleValue();
    }

    public static MapFunction<String, String> mapToSelf = new MapFunction<String, String>(){
        public String map(String var1) {
            return var1;
        }
    };

    public static FlatMapFunction<String, String> splitFlatMap
            = new FlatMapFunction<String, String>() {
        public Iterable<String> flatMap(String var1) throws Exception {
            return Arrays.asList(var1.toLowerCase().split("\\W+"));
        }
    };

    public static  MapPairFunction<String, String, Integer> mapToStringIntegerPair = new MapPairFunction<String, String, Integer>() {
        public Tuple2<String, Integer> mapToPair(String s) {
            return new Tuple2<String, Integer>(s, 1);
        }
    };

    public static ReduceFunction<Integer> sumReduce = new ReduceFunction<Integer>() {
        public Integer reduce(Integer var1, Integer var2) throws Exception {
            return var1 + var2;
        }
    };

    public static FlatMapFunction<WithTime<String>, WithTime<String>> splitFlatMapWithTime
            = new FlatMapFunction<WithTime<String>, WithTime<String>>() {
        public Iterable<WithTime<String>> flatMap(WithTime<String> var1) throws Exception {
            List<WithTime<String>> list = new ArrayList<>();
            for(String str : var1.getValue().toLowerCase().split("\\W+")) {
                list.add(new WithTime<>(str, var1.getTime()));
            }
            return list;
        }
    };

    public static  MapPairFunction<WithTime<String>, String, WithTime<Integer>> mapToStrIntPairWithTime
            = new MapPairFunction<WithTime<String>, String, WithTime<Integer>>() {
        public Tuple2<String, WithTime<Integer>> mapToPair(WithTime<String> s) {
            return new Tuple2<>(s.getValue(), new WithTime<>(1, s.getTime()));
        }
    };

    public static ReduceFunction<WithTime<Integer>> sumReduceWithTime = new ReduceFunction<WithTime<Integer>>() {
        public WithTime<Integer> reduce(WithTime<Integer> var1, WithTime<Integer> var2) throws Exception {
            return new WithTime<>(var1.getValue()+var2.getValue(), Math.max(var1.getTime(), var2.getTime()));
        }
    };

    public static UpdateStateFunction<Integer> updateStateCount = new UpdateStateFunction<Integer>() {
        public Optional<Integer> update(List<Integer> values, Optional<Integer> cumulateValue) {
            Integer sum = cumulateValue.or(0);
            for (Integer i : values) {
                sum += i;
            }
            return Optional.of(sum);
        }
    };

    public static MapPartitionFunction<Tuple2<String, Integer>, Tuple2<String, Integer>> localCount = new MapPartitionFunction<Tuple2<String, Integer>, Tuple2<String, Integer>>() {
        @Override
        public Iterable<Tuple2<String, Integer>> mapPartition(Iterable<Tuple2<String, Integer>> tuple2s) {
            Map<String, Tuple2<String, Integer>> map = new HashMap<>();
            for(Tuple2<String, Integer> tuple2 : tuple2s){
                String word = tuple2._1();
                Tuple2<String, Integer> count = map.get(word);
                if (count == null){
                    map.put(word, tuple2);
                } else {
                    map.put(word, new Tuple2<>(word, count._2() + tuple2._2()));
                }
            }
            return map.values();
        }
    };

    public static MapFunction<WithTime<Integer>, Integer> removeTimeMap = new MapFunction<WithTime<Integer>, Integer>() {
        private Logger logger = LoggerFactory.getLogger(this.getClass());

        @Override
        public Integer map(WithTime<Integer> var1) {
            logger.warn(var1.toString());
            return var1.getValue();
        }
    };

    public static FlatMapPairFunction<WithTime<String>, String, WithTime<Integer>> flatMapToPairWithTime
            =  new FlatMapPairFunction<WithTime<String>, String, WithTime<Integer>>() {
        @Override
        public Iterable<Tuple2<String, WithTime<Integer>>> flatMapToPair(WithTime<String> var1) throws Exception {
            List<Tuple2<String, WithTime<Integer>>> results = new ArrayList<>();
            for(String str : var1.getValue().toLowerCase().split("\\W+")){
                results.add(new Tuple2<>(str, new WithTime<>(1, var1.getTime())));
            }
            return results;
        }
    };

    public static FlatMapPairFunction<String, String, WithTime<Integer>> flatMapToPairAddTime
            =  new FlatMapPairFunction<String, String, WithTime<Integer>>() {
        @Override
        public Iterable<Tuple2<String, WithTime<Integer>>> flatMapToPair(String var1) throws Exception {
            List<Tuple2<String, WithTime<Integer>>> results = new ArrayList<>();
            for(String str : var1.toLowerCase().split("\\W+")){
                results.add(new Tuple2<>(str, new WithTime<>(1, System.currentTimeMillis())));
            }
            return results;
        }
    };
}
