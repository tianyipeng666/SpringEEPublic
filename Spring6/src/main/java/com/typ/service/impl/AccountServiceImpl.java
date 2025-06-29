package com.typ.service.impl;

import com.typ.dao.AccountDao;
import com.typ.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao ad;

    // 定义业务方法：转账操作：返回值为int类型，在方法内部可以定义一个状态码：成功返回1，失败返回0
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int updateAccount(Integer outid,Integer inid,Double money){
        // 定义状态码：成功1，失败0,初始化状态下 code是0
        int code = 0;
        // 账户A（id为6）给账户B（id为10）转100 ：
        // 账户A支出100：
        int outRow = ad.outAccount(outid, money);
        // 制造异常：
        //int a = 1 / 0;
        // 账户B收入100：
        int inRow = ad.inAccount(inid, money);
        // 如果收入和支出两个操作都执行成功的话，表示转账成功：
        if (outRow == 1 && inRow == 1){
            code = 1;// 转账成功
        }


        // 状态码作为方法的返回值：
        return code;
    }


}
