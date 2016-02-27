/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Gareth Jon Lynch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gazbert.java8.aggregate;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.gazbert.java8.common.Order;
import com.gazbert.java8.common.Order.Market;
import com.gazbert.java8.common.Order.Type;

/**
 * Test class for demonstrating use of parallel aggregate operation functionality in Java 8.
 * <p>
 * I recommend going the samples in {@link TestReductionAggregateOperations} first as this test class builds on previous
 * examples.
 * <p>
 * Aggregate operations and parallel streams enable you to implement parallelism with non-thread-safe collections
 * as long as you do not modify the collection while you are operating on it.
 * <p>
 * Parallelism is not automatically faster than performing operations serially; it can be if you have enough data and
 * the processor has enough cores.
 * <p>
 * Use case is for querying orders in a trading exchange order book.
 * <p>
 *
 * @author gazbert
 */
public class TestParallelismAggregateOperationsUsage {
    private Order order1;
    private Order order2;
    private Order order3;

    private List<Order> orderBook;

    /**
     * Builds the order book up for each test.
     */
    @Before
    public void setupForEachTest() {
        order1 = new Order(
                Market.EUR, Type.BUY, new BigDecimal("100.00"), new BigDecimal("1.69"), new BigDecimal("0.01"));
        order2 = new Order(
                Market.USD, Type.SELL, new BigDecimal("201.00"), new BigDecimal("1.70"), new BigDecimal("0.01"));
        order3 = new Order(
                Market.CNY, Type.SELL, new BigDecimal("250.00"), new BigDecimal("10.58"), new BigDecimal("0.01"));

        orderBook = new ArrayList<>();
        orderBook.add(order1);
        orderBook.add(order2);
        orderBook.add(order3);
    }

    /**
     * Gets the average number of trades it took to fill all SELL orders.
     * <p>
     * Streams can be executed serially or in parallel. Parallel means Java runtime partitions the stream into
     * multiple substreams. Aggregate operations iterate over and process these substreams in parallel and then combine
     * the results.
     * <p>
     * By default, streams are serial. To create a parallel stream, invoke the operation Collection.parallelStream as
     * shown in the code below.
     * <p>
     */
    @Test
    public void getAverageNumberOfTradesToFillASellOrderUsingJava8ParallelStream() {

        order1.setTradeCountToFill(3);
        order2.setTradeCountToFill(4);
        order3.setTradeCountToFill(2);

        final double averageTradesToFillSellOrder =

                orderBook
                        .parallelStream() // go parallel
                        .filter(e -> e.getType() == Type.SELL)
                        .mapToInt(Order::getTradeCountToFill)
                        .average()
                        .getAsDouble();

        assertEquals(3, averageTradesToFillSellOrder, 0);
    }

    /**
     * Demonstrates parallel use of {@link Collectors} groupingBy functionality to aggregate avetage trade counts
     * for buy and sell orders.
     * <p>
     * It uses <em>concurrent reduction</em>.
     * <p>
     * Java runtime performs concurrent reduction if all of the the following are true for a particular pipeline that
     * contains the collect operation:
     * <ol>
     * <li>The stream is parallel</li>
     * <li>The parameter of the collect operation, the collector, has the characteristic
     * Collector.Characteristics.CONCURRENT. To determine the characteristics of a collector, invoke the
     * Collector.characteristics method.</li>
     * <li>Either the stream is unordered, or the collector has the characteristic Collector.Characteristics.UNORDERED.
     * To ensure that the stream is unordered, invoke the BaseStream.unordered operation.</li>
     * </ol>
     */
    @Test
    public void getMapOfSellAndBuyTradeCountsUsingJava8ParallelStreamAndGroupingBy() {

        order1.setTradeCountToFill(3);
        order2.setTradeCountToFill(4);
        order3.setTradeCountToFill(2);

        final ConcurrentMap<Order.Type, Double> averageTradeCountByOrderType = orderBook
                .parallelStream()
                .collect(
                        Collectors.groupingByConcurrent(
                                Order::getType,
                                Collectors.averagingInt(Order::getTradeCountToFill)));

        final double averageNumberOfSellTrades = averageTradeCountByOrderType.get(Type.SELL);
        assertEquals(3, averageNumberOfSellTrades, 0);

        final double averageNumberOfBuyTrades = averageTradeCountByOrderType.get(Type.BUY);
        assertEquals(3, averageNumberOfBuyTrades, 0);
    }

    /**
     * Ordering when using parallelism.
     * <p>
     * Be careful. The Java runtime will order the  stream's elements to maximize the benefits of parallel computing
     * unless otherwise specified by the stream operation.
     * <p>
     * The order a pipeline processes the elements of a stream depends on whether:
     * <ol>
     * <li>the stream is executed serially or in parallel</li>
     * <li>the source of the stream</li>
     * <li>the intermediate operations</li>
     * </ol>
     * Code sample below based on the Java 8 tutorial.
     * <p>
     * Note forEach changes the ordering, whereas forEachOrdered maintains it (but we subsequently lose benefit of
     * parallelism).
     */
    @Test
    public void showAffectOfParallelismOnOrdering() {

        final Integer[] intArray = {1, 2, 3, 4, 5, 6, 7, 8};
        final List<Integer> listOfIntegers = new ArrayList<>(Arrays.asList(intArray));

        System.out.println("listOfIntegers:");

        listOfIntegers
                .stream()
                .forEach(e -> System.out.print(e + " "));
        System.out.println("");

        System.out.println("listOfIntegers sorted in reverse order:");
        final Comparator<Integer> normal = Integer::compare;
        final Comparator<Integer> reversed = normal.reversed();
        Collections.sort(listOfIntegers, reversed);
        listOfIntegers
                .stream()
                .forEach(e -> System.out.print(e + " "));
        System.out.println("");

        System.out.println("Parallel stream");
        listOfIntegers
                .parallelStream()
                .forEach(e -> System.out.print(e + " "));
        System.out.println("");

        System.out.println("Another parallel stream:");
        listOfIntegers
                .parallelStream()
                .forEach(e -> System.out.print(e + " "));
        System.out.println("");

        System.out.println("With forEachOrdered:");
        listOfIntegers
                .parallelStream()
                .forEachOrdered(e -> System.out.print(e + " "));
        System.out.println("");
    }
}
