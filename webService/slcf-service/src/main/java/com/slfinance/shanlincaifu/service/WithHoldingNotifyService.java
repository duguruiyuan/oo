package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;

/**
 * 代扣对外通知
 * @author lixx
 * @create 2017-07-17 10:48
 **/
public interface WithHoldingNotifyService {

    /***
     *发送代扣通知
     */
    public void asynNotify() throws SLException;


}
