package org.taskflow.example.endpoint;

import org.taskflow.core.operator.IOperator;

/**
 * Created by ytyht226 on 2022/6/23.
 */
public class Operator1 implements IOperator<Integer, Integer> {
    @Override
    public Integer execute(Integer param) throws Exception {
        //业务逻辑部分
        System.out.println("1...");
        return null;
    }
}