package com.mashibing.dp.proxy.v08;


import org.omg.CORBA.portable.InvokeHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;

/**
 * 问题：我想记录坦克的移动时间
 * 最简单的办法：修改代码，记录时间
 * 问题2：如果无法改变方法源码呢？
 * 用继承？
 * v05:使用代理
 * v06:代理有各种类型
 * 问题：如何实现代理的各种组合？继承？Decorator?
 * v07:代理的对象改成Movable类型-越来越像decorator了
 * v08:如果有stop方法需要代理...
 * 如果想让LogProxy可以重用，不仅可以代理Tank，还可以代理任何其他可以代理的类型 Object
 * （毕竟日志记录，时间计算是很多方法都需要的东西），这时该怎么做呢？
 * 分离代理行为与被代理对象
 * 使用jdk的动态代理
 */
public class Tank implements Movable {

    /**
     * 模拟坦克移动了一段儿时间
     */
    @Override
    public void move() {
        System.out.println("Tank moving claclacla...");
        try {
            Thread.sleep(new Random().nextInt(10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        //jdk1.8及之前版本使用这个 (我的是1.8,没试过其他版本)
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles","true");

        //新版本jdk使用这个
        //System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");

        Tank tank = new Tank();
//        Class<? extends Tank> aClass = tank.getClass();
//        Method[] declaredMethods = aClass.getDeclaredMethods();

        //reflection 通过二进制字节码分析类的属性和方法

        InvocationHandler timeHandler = new TimeHandler(tank);

        InvocationHandler logHandler = new LogHandler(tank);

        Class<?> proxyClass = Proxy.getProxyClass(Tank.class.getClassLoader(), Movable.class);
        Movable movable = (Movable) proxyClass.getConstructor(InvocationHandler.class).newInstance(timeHandler);
        movable.move();


        Class<?> proxyClass2 = Proxy.getProxyClass(Tank.class.getClassLoader(), Movable.class);
        Movable movable2 = (Movable) proxyClass2.getConstructor(InvocationHandler.class).newInstance(logHandler);
        movable2.move();

//        Movable m = (Movable)Proxy.newProxyInstance(
//                Tank.class.getClassLoader(),
//                new Class[]{Movable.class}, //tank.class.getInterfaces()
//                new LogHandler(tank)
//        );

//        Movable m = (Movable)Proxy.newProxyInstance(
//                Tank.class.getClassLoader(),
//                new Class[]{Movable.class}, //tank.class.getInterfaces()
//                null
//        );

//        m.move();
    }
}

class TimeHandler implements InvocationHandler {

    Movable movable;

    public TimeHandler(Movable movable) {
        this.movable = movable;
    }
    //getClass.getMethods[]
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("TimeHandler: method " + method.getName() + " start..");
        long start = System.currentTimeMillis();

        Object o = method.invoke(movable, args);

        System.out.println("TimeHandler: method " + method.getName() + " end!");
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        return o;
    }
}

class LogHandler implements InvocationHandler {

    Movable movable;

    public LogHandler(Movable movable) {
        this.movable = movable;
    }
    //getClass.getMethods[]
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("LogHandler: method " + method.getName() + " start..");
        Object o = method.invoke(movable, args);
        System.out.println("LogHandler: method " + method.getName() + " end!");
        return o;
    }
}



interface Movable {
    void move();
}