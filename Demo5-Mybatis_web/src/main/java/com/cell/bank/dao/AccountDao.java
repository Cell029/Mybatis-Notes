package com.cell.bank.dao;

import com.cell.bank.pojo.Account;

public interface AccountDao {
    Account selectByActno(String actno);

    int update(Account act);
}


