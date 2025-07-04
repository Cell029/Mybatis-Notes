package com.cell.bank.dao.impl;

import com.cell.bank.dao.AccountDao;
import com.cell.bank.pojo.Account;
import com.cell.bank.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

public class AccountDaoImpl implements AccountDao {
    @Override
    public Account selectByActno(String actno) {
        // 与 service 层中获取的 sqlSession 为同一个
        SqlSession sqlSession = SqlSessionUtil.openSession();
        return (Account)sqlSession.selectOne("selectByActno", actno);
    }

    @Override
    public int update(Account act) {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        return sqlSession.update("update", act);
    }
}
