package com.slfinance.shanlincaifu.utils;

import java.util.Comparator;

import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;

public class RepaymentPlanEntitySortUtil implements Comparator<RepaymentPlanInfoEntity>{

	@Override
	public int compare(RepaymentPlanInfoEntity oneEntity, RepaymentPlanInfoEntity twoEntity) {
		return oneEntity.getCurrentTerm()-twoEntity.getCurrentTerm();
	}
}
