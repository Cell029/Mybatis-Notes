package com.cell.bank.service;

import com.cell.bank.exception.AppException;
import com.cell.bank.exception.MoneyNotEnoughException;

public interface AccountService {
    void transfer(String fromActno, String toActno, double money) throws MoneyNotEnoughException, AppException;
}
