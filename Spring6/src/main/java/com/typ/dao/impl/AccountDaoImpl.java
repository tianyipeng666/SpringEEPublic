package com.typ.dao.impl;

import com.typ.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDaoImpl implements AccountDao {

    // 注入JdbcTemplate模板对象：
    @Autowired
    JdbcTemplate jt;

    @Override
    public int inAccount(Integer accountId, Double money) {
        // 定义sql:
        String sql = "update tb_account set money = money + ? where account_id = ?";
        // 参数：
        Object[] params = {money,accountId};
        // 使用模板：
        int i = jt.update(sql, params);
        return i;
    }

    @Override
    public int outAccount(Integer accountId, Double money) {
        // 定义sql:
        String sql = "update tb_account set money = money - ? where account_id = ?";
        // 参数：
        Object[] params = {money,accountId};
        // 使用模板：
        int i = jt.update(sql, params);
        return i;
    }
}
