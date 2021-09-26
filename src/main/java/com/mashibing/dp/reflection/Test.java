package com.mashibing.dp.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<CTank> cTankClass = CTank.class;

        CTank cTank = cTankClass.newInstance();
        Method[] methods = cTankClass.getDeclaredMethods();
        //Method move = cTankClass.getMethod("move", int.class);
        //methods[0].setAccessible(true);

        methods[0].invoke(cTank, 123);
    }

}

