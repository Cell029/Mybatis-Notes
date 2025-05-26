package com.cell.bank.service.impl;

import com.cell.bank.dao.AccountDao;
import com.cell.bank.dao.impl.AccountDaoImpl;
import com.cell.bank.exception.AppException;
import com.cell.bank.exception.MoneyNotEnoughException;
import com.cell.bank.pojo.Account;
import com.cell.bank.service.AccountService;
import com.cell.bank.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

public class AccountServiceImpl implements AccountService {
    private AccountDao accountDao = new AccountDaoImpl();
    @Override
    public void transfer(String fromActno, String toActno, double money) throws MoneyNotEnoughException, AppException {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        // 查询转出账户的余额
        Account fromAct = accountDao.selectByActno(fromActno);
        if (fromAct.getBalance() < money) {
            throw new MoneyNotEnoughException("对不起，您的余额不足。");
        }
        try {
            // 程序如果执行到这里说明余额充足
            // 修改账户余额
            Account toAct = accountDao.selectByActno(toActno);
            fromAct.setBalance(fromAct.getBalance() - money);
            toAct.setBalance(toAct.getBalance() + money);

            // 模拟异常
            String s = null;
            s.toString();

            // 更新数据库
            accountDao.update(fromAct);
            accountDao.update(toAct);

            sqlSession.commit();
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            throw new AppException("转账失败，未知原因！");
        } finally {
            SqlSessionUtil.close(sqlSession);
        }
    }
}
