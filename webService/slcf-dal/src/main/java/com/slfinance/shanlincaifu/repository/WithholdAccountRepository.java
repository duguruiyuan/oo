package com.slfinance.shanlincaifu.repository;

import com.slfinance.shanlincaifu.entity.WithholdAccountEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by SLCF-ZX on 2017/7/14.
 */
public interface WithholdAccountRepository extends PagingAndSortingRepository<WithholdAccountEntity,String> {

    /**
     *收益账户名称
     * @param companyName
     * @return
     */
    public WithholdAccountEntity findByCompanyName(@Param("companyName") String companyName);

}
