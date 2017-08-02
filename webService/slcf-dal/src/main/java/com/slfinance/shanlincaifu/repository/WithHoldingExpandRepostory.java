package com.slfinance.shanlincaifu.repository;

import com.slfinance.shanlincaifu.entity.WithHoldingExpandEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 代扣通知扩展表数据访问层
 * Created by 张祥 on 2017/7/12.
 */
public interface  WithHoldingExpandRepostory extends PagingAndSortingRepository<WithHoldingExpandEntity,String>{

    /**
     *
     * 根据处理状态以及通知次数查询需要发送代扣结果通知的数据
     * @date 20170713
     * @author 张祥
     * @param status
     * @param notifyCount
     * @return
     */
    @Query("select a from WithHoldingExpandEntity a where a.interfaceType = ?1 and (a.execStatus is null or a.execStatus != ?2) and a.alreadyNotifyTimes < ?3 order by createDate asc")
    public List<WithHoldingExpandEntity> findNeedSendNotify(String interfaceType,String status,Integer notifyCount);



}
