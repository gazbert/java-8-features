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

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.gazbert.java8.common.Order;
import com.gazbert.java8.common.Order.Market;
import com.gazbert.java8.common.Order.Type;

/**
 * Test class for demonstrating use of reduction aggregate operations in Java 8.
 * <p>
 * Use case is for querying orders in a trading exchange order book.
 * <p>
 *
 * @author gazbert
 */
public class TestReductionAggregateOperations {

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
     * Nice example of aggregate ops in action.
     * <p>
     * JDK contains many terminal operations (such as average, sum, min, max, and count) that return one value by
     * combining the contents of a stream. These operations are called <em>reduction operations</em>
     * <p>
     * This example uses the average reduction operation.
     */
    @Test
    public void getAverageNumberOfTradesToFillASellOrderUsingJava8PipelineAggregateOperation() {

        order1.setTradeCountToFill(3);
        order2.setTradeCountToFill(4);
        order3.setTradeCountToFill(2);

        final double averageTradesToFillSellOrder =

                orderBook
                        .stream()
                        .filter(e -> e.getType() == Type.SELL) // intermediate operation #1 - filter
                        .mapToInt(Order::getTradeCountToFill)  // intermediate operation #2 - creates new int stream for all values from getTradeCountToFill
                        .average() // terminal operation  - averages them (reduction operation)
                        .getAsDouble(); // returns result as double

        assertEquals(3, averageTradesToFillSellOrder, 0);
    }

    /**
     * Sums the average number of trades it took to fill all orders of any type.
     * <p>
     * Uses <code>Stream.reduce<code> operation to sum up the number of trades.
     * <p>
     * The reduce operation in this example takes two arguments:
     * <ol>
     * <li>identity - the initial value of the reduction and the default result if there are no elements in the stream</li>
     * <li>accumulator function - The accumulator function takes two parameters: a partial result of the reduction
     * (the sum of all processed trades so far) and the next element of the stream (next trade integer). It returns
     * a new partial result. This accumulator function is a lambda expression that adds two integer values and
     * returns an integer value</li>
     * </ol>
     */
    @Test
    public void getSumOfTradesUsingJava8StreamReduce() {

        order1.setTradeCountToFill(3);
        order2.setTradeCountToFill(4);
        order3.setTradeCountToFill(2);

        // First way uses JDK sum() reduction op
        int sumOfAllTrades =

                orderBook
                        .stream()
                        .mapToInt(Order::getTradeCountToFill)
                        .sum();

        assertEquals(9, sumOfAllTrades, 0);

        // Second way uses Stream.reduce
        sumOfAllTrades =

                orderBook
                        .stream()
                        .map(Order::getTradeCountToFill)
                        .reduce(
                                0, // identity arg
                                (a, b) -> a + b); // lambda accumulator function. args: a is partial sum so far, b is next trade to add,
        // actual function adds them up.

        assertEquals(9, sumOfAllTrades, 0);
    }

    /**
     * Sums the average number of trades it took to fill SELL orders.
     * <p>
     * The <code>Stream.reduce<code> operation always returns a new value. The accumulator function in the previous
     * example also returns a new int every time it processes an element of a stream. This is not very efficient if you
     * wanted to reduce the elements of a stream to a more complex object like a collection. If your reduce operation
     * involves adding elements to a collection, then every time your accumulator function processes an element, it
     * creates a new collection that includes the element... not good.
     * <p>
     * It would be more efficient to update an existing collection instead. You can do this with the
     * <code>Stream.collect</code> method; the collect method modifies, or mutates, an existing value. Also knwon as
     * mutable reduction.
     * <p>
     * Example below uses an Averager helper class to do this. The collect line can be broen down as follows:
     * <ul>
     * <li>supplier - factory function. For our collect operation, it creates instances of the result container.
     * In this example, it is a new instance of the Averager class.</li>
     * <li>accumulator - accumulator function incorporates a stream element into a result container. In this example,
     * accept() modifies the Averager result container by incrementing the count variable by one and
     * adding to the total member variable the value of the stream element - the trade count.
     * <li>combiner - combiner function takes two result containers and merges their contents. In this example, it
     * modifies an Averager result container by incrementing the count variable by the count member
     * variable of the other Averager instance and adding to the total member variable the value of
     * the other Averager instance's total member variable.</li>
     * </ul>
     * I could have used the JDK average reduction method here (see earlier demo), but I wanted a simple demo of using
     * a collector. You would use the collect operation and a custom class if you needed to calculate several values
     * from the elements of a stream.
     */
    @Test
    public void getAverageNumberOfTradesToFillASellOrderUsingJava8StreamCollect() {

        order1.setTradeCountToFill(3);
        order2.setTradeCountToFill(4);
        order3.setTradeCountToFill(2);

        final Averager averagerHelper = orderBook

                .stream()
                .filter(p -> p.getType() == Type.SELL)
                .map(Order::getTradeCountToFill) // the thing we want to call to get the average
                .collect(Averager::new, Averager::accept, Averager::combine); // collect syntax

        assertEquals(3, averagerHelper.average(), 0);
    }

    /**
     * The collect operation is best suited for collections.
     * <p>
     * The following example puts the amount of all SELL order in a collection with the collect operation.
     * <p>
     * This version of the collect operation takes one parameter of type Collector.
     * This class encapsulates the functions used as arguments in the collect operation that requires three arguments
     * (supplier, accumulator, and combiner functions).
     * <p>
     * The {@link Collectors} class contains many useful reduction operations, such as accumulating elements into collections
     * and summarizing elements according to various criteria. These reduction operations return instances of the
     * class Collector, so you can use them as a parameter for the collect operation.
     * <p>
     * This example uses the Collectors.toList operation, which accumulates the stream elements into a new instance
     * of List. As with most operations in the Collectors class, the toList operator returns an instance of Collector,
     * not a collection.
     */
    @Test
    public void getListOfAllSellOrderAmountsUsingJava8StreamCollectors() {

        final List<BigDecimal> listOfSellOrderAmounts = orderBook
                .stream()
                .filter(p -> p.getType() == Type.SELL)
                .map(p -> p.getAmount())
                .collect(Collectors.toList());

        assertEquals(2, listOfSellOrderAmounts.size());
    }


    /**
     * Demonstrates use of {@link Collectors} groupingBy functionality to aggregate total trade counts
     * for buy and sell orders.
     * <p>
     * The groupingBy takes 3 params:
     * <ol>
     * <li>identity: Like the <code>Stream.reduce</code> operation, the identity element is both the initial value of
     * the reduction and the default result if there are no elements in the stream.</li>
     * <li>mapper: The reducing operation applies this mapper function to all stream elements. In this example,
     * the mapper retrieves the trade count of each member.</li>
     * <li>operation: The operation function is used to reduce the mapped values. In this example, the operation
     * function adds Integer values.</li>
     * </ol>
     */
    @Test
    public void getMapOfSellAndBuyTradeCountsUsingJava8CollectorsGroupingBy() {

        order1.setTradeCountToFill(3);
        order2.setTradeCountToFill(4);
        order3.setTradeCountToFill(2);

        final Map<Order.Type, Integer> totalTradeCountMap = orderBook
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Order::getType,
                                Collectors.reducing(
                                        0,
                                        Order::getTradeCountToFill,
                                        Integer::sum)));

        final int totalNumberOfSellTrades = totalTradeCountMap.get(Type.SELL);
        assertEquals(6, totalNumberOfSellTrades);

        final int totalNumberOfBuyTrades = totalTradeCountMap.get(Type.BUY);
        assertEquals(3, totalNumberOfBuyTrades);
    }


    /**
     * Another example of using groupingBy returns average trade count for Buy and Sell orders.
     */
    @Test
    public void getMapOfSellAndBuyTradeAveragesUsingJava8CollectorsGroupingBy() {

        order1.setTradeCountToFill(3);
        order2.setTradeCountToFill(4);
        order3.setTradeCountToFill(2);

        final Map<Order.Type, Double> averageTradeCountByOrderType = orderBook
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Order::getType,
                                Collectors.averagingInt(Order::getTradeCountToFill)));

        final double averageNumberOfSellTrades = averageTradeCountByOrderType.get(Type.SELL);
        assertEquals(3, averageNumberOfSellTrades, 0);

        final double averageNumberOfBuyTrades = averageTradeCountByOrderType.get(Type.BUY);
        assertEquals(3, averageNumberOfBuyTrades, 0);
    }


    /**
     * Helper class used in demoing use of the <code>Stream.collect</code> method.
     *
     * @author gazbert
     */
    private class Averager implements IntConsumer {
        private int total = 0;
        private int count = 0;

        public double average() {
            return count > 0 ? ((double) total) / count : 0;
        }

        /*
         * We override this to get next stream element so we can update the running totals. 
         */
        public void accept(int i) {
            total += i;
            count++;
        }

        /*
         * Combines 
         */
        public void combine(Averager other) {
            total += other.total;
            count += other.count;
        }
    }
}
