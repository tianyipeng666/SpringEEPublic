package com.typ.dao;

public interface AccountDao {
    // 收钱：
    public abstract int inAccount(Integer accountId,Double money);
    // 出钱：
    public abstract int outAccount(Integer accountId,Double money);
}
