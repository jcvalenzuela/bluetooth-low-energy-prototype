package com.example.myapplication;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Test
    public void add() {
        Integer input1 = 5;
        Integer input2 = 5;
        Integer output;
        Integer expected = 10;

        LoginViewModel myClass = new LoginViewModel();
        output = myClass.add(input1, input2);

        assertEquals(expected, output);
    }

    @Test
    public void multiply() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Integer input1 = 5;
        Integer input2 = 5;
        Integer output;
        Integer expected = 25;

        LoginViewModel myClass = new LoginViewModel();
        Method method = LoginViewModel.class.getDeclaredMethod("multiply", Integer.class, Integer.class);
        method.setAccessible(true);
        output = (Integer) method.invoke(myClass, input1, input2);
        assertEquals(expected, output);
    }
}






