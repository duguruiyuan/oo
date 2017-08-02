package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Laurance on 2017/4/27.
 */
public interface CalcCommService {

    /**
     * 计算业绩
     */
    ResultVo calcPerformance(Map<String, Object> param) throws SLException;

    /**
     * 计算佣金
     */
    ResultVo calcCommission(Map<String, Object> param) throws SLException;

    /**
     * 查询业绩
     *
     * @param param
     * @return
     */
    @Rules(rules = {@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字")
            , @Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
    })
    ResultVo queryPerformanceList(Map<String, Object> param);

    /**
     * 批量保存组织架构
     *
     * @param param
     * @return
     */
    ResultVo saveConstruct(Map<String, Object> param) throws SLException;

    /**
     * 批量保存提点
     *
     * @param param
     * @return
     */
    ResultVo saveCommissionRate(Map<String, Object> param);

    /**
     * 查询最终发放金额
     *
     * @param param
     * @return
     */
    @Rules(rules = {@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字")
            , @Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
    })
    ResultVo queryCommissionAmountList(Map<String, Object> param);

    ResultVo updateRank();

}
