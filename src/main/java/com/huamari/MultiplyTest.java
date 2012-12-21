package com.huamari;


import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: hhasubra
 * Date: 10/31/12
 * Time: 9:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class MultiplyTest {


    @Test
    public void multiplyTest() {
        Multiply multiply = new Multiply();
        Assert.assertEquals(12, multiply.multiply(3,4));
    }
}
