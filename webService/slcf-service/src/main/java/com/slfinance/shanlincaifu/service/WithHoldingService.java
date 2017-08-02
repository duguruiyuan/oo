package com.slfinance.shanlincaifu.service;/**
 * <描述：<b>TODO</b>
 *
 * @author:张祥
 * @date 2017/7/13
 */


import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

import java.util.List;
import java.util.Map;

/**
 * 代扣业务
 * @author 张祥
 * @create 2017-07-13 10:48
 **/
public interface WithHoldingService {

    /**
     * 自动代扣（正常还款）
     * @return
     */
    public ResultVo autoWithHoldingRepayment(String expectRepaymentDate) throws SLException;


    public ResultVo limitWithHolding(String loanNo,String currentTerm);

    /***
     * 代扣成功逻辑处理
     * @param paramMap
     */
    public void withHoldingSuccess(Map<String,Object> paramMap) throws SLException;

    /***
     * 代扣失败逻辑处理
     * @param paramMap
     */
    public void withHoldingFailed(Map<String,Object> paramMap) throws SLException;
    
    
    /***
     * 逾期代扣失败逻辑处理
     * @param paramMap
     */
    public void timeLimitWithHoldingFailed(Map<String,Object> paramMap) throws SLException;
    

    /***
     * 逾期代扣
     * @param paramMap
     * @return
     * @throws SLException
     */
    public ResultVo timeLimitWithHold(Map<String,Object> paramMap,List<Map<String,Object>> repaymentList) throws  SLException;

}
