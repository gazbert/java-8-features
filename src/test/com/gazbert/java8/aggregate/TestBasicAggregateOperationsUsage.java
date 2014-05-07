package com.gazbert.java8.aggregate;

/*The MIT License (MIT)

Copyright (c) 2014 Gazbert

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.*/

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.gazbert.java8.common.Order;
import com.gazbert.java8.common.Order.Market;
import com.gazbert.java8.common.Order.Type;

/**
 * Test class for demonstrating use of basic aggregate operation functionality in Java 8.
 * <p>
 * Use case is for querying orders in a trading exchange order book.
 * <p>
 * 
 * @author gazbert
 *
 */
public class TestBasicAggregateOperationsUsage 
{  
    private Order order1;
    private Order order2;
    private Order order3;
    
    private List<Order> orderBook;
    
    /**
     * Builds the order book up for each test.
     */
    @Before
    public void setupForEachTest()
    {
        order1 = new Order(
                Market.EUR, Type.BUY, new BigDecimal(100.00), new BigDecimal(1.69), new BigDecimal(0.01));
        order2 = new Order(
                Market.USD, Type.SELL, new BigDecimal(201.00), new BigDecimal(1.70), new BigDecimal(0.01));
        order3 = new Order(
                Market.CNY, Type.SELL, new BigDecimal(250.00), new BigDecimal(10.58), new BigDecimal(0.01));
  
        orderBook = new ArrayList<Order>();
        orderBook.add(order1);
        orderBook.add(order2);
        orderBook.add(order3);        
    }    
    
    /**
     * Displays audit details using Java 7 'for-each loop' approach.
     */
    @Test
    public void displayAuditDetailsUsingJava7ForEachLoop() {

        for (final Order order : orderBook)
        {
            System.out.println("java7: " + order.provideAuditDetails());
        }
    }    
    
    /**
     * Displays audit details using Java 8 aggregate operation: stream and <em>forEach</em> approach.
     * <p>
     * It removes the need for the loop.
     */
    @Test
    public void displayAuditDetailsUsingJava8StreamForEachAggregateOperation() {
        
        orderBook.
        stream().
        forEach(e -> System.out.println("java 8 stream: " + e.provideAuditDetails()));
        // e is the lambda expression arg
    }    
        
    /**
     * Displays audit details for all SELL order amounts over 200 using Java 7 'for-each loop' approach.
     */
    @Test
    public void displayAuditDetailsForOrderAmountsOver200UsingJava7ForEachLoop() {

        int orderCount = 0;
        for (final Order order : orderBook)
        {
            if (order.getType() == Type.SELL && order.getAmount().compareTo(new BigDecimal(200.00)) >= 0)
            {
                System.out.println("java7: " + order.provideAuditDetails());
                orderCount++;
            }
        }
        
        assertEquals(2, orderCount);
    }    
        
    /**
     * Displays audit details for all SELL order amounts over 200 using Java 8 <em>pipeline</em> aggregate operation:
     * stream, filter, forEach approach.
     * <p>
     * 
     * A pipeline is a sequence of aggregate operations. The <em>filter</em> contains the condition we execute on
     * each <em>forEach</em> cycle.
     * 
     * Pipeline made up of:
     * <ul>
     * <li>source - e.g collection</li>
     * <li>zero or more intermediate operations - e.g. a filter: It executes a condition that returns a new stream</li>
     * <li>terminal operation - returns the non-stream end result</li>
     * 
     * </ul>
     */
    @Test
    public void displayAuditDetailsForSellOrderAmountsOver200UsingJava8PipelineAggregateOperation() {
        
        orderBook
        .stream()
        .filter(e -> e.getType() == Type.SELL && e.getAmount().compareTo(new BigDecimal(200.00)) >= 0)
        .forEach(e -> System.out.println("java8 pipeline: " + e.provideAuditDetails()));
        // e is the lambda expression arg
    }       
}
