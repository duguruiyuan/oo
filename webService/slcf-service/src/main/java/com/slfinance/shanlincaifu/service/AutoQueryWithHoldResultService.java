package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;

/**
 * <p><b>标题：</b>webService</p>
 * <p><b>描述：</b>定时查询代扣结果Service </p>
 * <p><b>公司：</b>善林财富 </p>
 * <p><b>版权声明：</b>Copyright (c) 2017</p>
 * @author 张祥
 * @date 2017/7/17 16:07
 */
public interface AutoQueryWithHoldResultService {

    public void queryTppWithholdingResult() throws SLException;

}
