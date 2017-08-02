package com.slfinance.shanlincaifu.utils;

import java.util.Comparator;

import com.slfinance.shanlincaifu.vo.RepaymentPlanVo;

public class RepaymentPlanSortUtil implements Comparator<RepaymentPlanVo>{

	@Override
	public int compare(RepaymentPlanVo oneVo, RepaymentPlanVo twoVo) {
		return oneVo.getCurrentTerm()-twoVo.getCurrentTerm();
	}
}
