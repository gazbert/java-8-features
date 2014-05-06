package com.gazbert.java8.lambda;

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

import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class for analysing an order book.
 * <p>
 * Not real-world; just here to demo lambda use.
 * 
 * @author gazbert
 *
 */
public final class OrderBookAnalyser
{
    // lockdown
    private OrderBookAnalyser()
    {}

    /**
     * Returns the number of matched orders for a given query.
     * 
     * @param orderBook the order book.
     * @param orderMatcher used to match orders.
     * @return the number of matched orders.
     */
    public static int getNumberOfMatchedOrders(final List<Order> orderBook, final OrderMatcher orderMatcher)
    {
        int orderCount = 0;
        for (final Order order : orderBook)
        {
            if (orderMatcher.executeQuery(order))
            {
                orderCount++;
            }
        }        
        return orderCount;
    }
    
    /**
     * Returns the number of matched orders for a given query using a Java 8 JDK Standard Functional Interface.
     * <p>
     * We use a {@link Predicate} -a boolean-valued function of one argument.
     * 
     * @param orderBook the order book.
     * @param orderMatcher used to match orders - we use standard JDK Predicate functional interface.
     * @return the number of matched orders.
     */
    public static int getNumberOfMatchedOrdersUsingJdkStandardFunctionalInterface(
            final List<Order> orderBook, final Predicate<Order> orderMatcher)
    {
        int orderCount = 0;
        for (final Order order : orderBook)
        {
            if (orderMatcher.test(order)) // test is the functional interface of Predicate
            {
                orderCount++;
            }
        }        
        return orderCount;
    }
    
    /**
     * Returns the number of matched orders for a given query (fees factored in).
     * Only here to demo 2 args being passed to lambda function.
     * 
     * @param orderBook the order book.
     * @param orderMatcher used to match orders.
     * @return the number of matched orders.
     */
    public static int getNumberOfMatchedOrders(final List<Order> orderBook, final OrderMatcherWithFees orderMatcher)
    {
        int orderCount = 0;
        for (final Order order : orderBook)
        {
            if (orderMatcher.executeQuery(order, order.getFee()))
            {
                orderCount++;
            }
        }        
        return orderCount;
    }
}
