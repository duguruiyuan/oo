package com.slfinance.shanlincaifu.repository;

import com.slfinance.shanlincaifu.entity.WithHoldingFlowEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 * 代扣流水数据访问层
 *
 * Created by 张祥 on 2017/7/13.
 */
public interface WithHoldingFlowRepository extends PagingAndSortingRepository<WithHoldingFlowEntity,String>{

    /****
     * 根据批次号以及请求号查询代扣流水
     * @param batchCode
     * @param requestNo
     * @return
     */
    public  WithHoldingFlowEntity findByBatchCodeAndRequestNo(@Param("batchCode") String batchCode,@Param("requestNo") String requestNo);

    /***
     * 是否需要查询
     * @param isNeesQuery
     * @return
     */
    public List<WithHoldingFlowEntity> findByIsNeedQuery(@Param("isNeesQuery") String isNeesQuery);


    

}
