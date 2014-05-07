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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.gazbert.java8.common.Order;
import com.gazbert.java8.common.Order.Market;
import com.gazbert.java8.common.Order.Type;

/**
 * Test class for demonstrating use of parallel aggregate operation functionality in Java 8.
 * <p>
 * Use case is for querying orders in a trading exchange order book.
 * <p>
 * 
 * @author gazbert
 */
public class TestParallelismAggregateOperationsUsage
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
     * Work in progress...
     */
    @Test
    public void workInProgress() {

  
    }    

}
