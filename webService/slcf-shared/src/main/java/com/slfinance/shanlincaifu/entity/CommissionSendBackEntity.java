package com.slfinance.shanlincaifu.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by Laurance on 2017/4/20.
 */
@Getter
@Setter
@Table(name = "BAO_T_COMMISSION_SENDBACK")
public class CommissionSendBackEntity {
    @Column(name = "INVEST_ID")
    private String investId;
    @Column(name = "MANAGER_NAME")
    private String managerName;
    @Column(name = "JOB_NO")
    private String jobNo;
    @Column(name = "RANKING_NAME")
    private String rankingName;
    @Column(name = "RANKING")
    private String ranking;
    @Column(name = "INVEST_AMOUNT")
    private BigDecimal investAmount;
    @Column(name = "HOLD_AMOUNT")
    private BigDecimal holdAmount;
    @Column(name = "EFFECT_DATE")
    private String effectDate;
    @Column(name = "EXPIRE_DATE")
    private String expireDate;
    @Column(name = "SHOP_NAME")
    private String shopName;// 门店
    @Column(name = "TRANS_DATE")
    private String transDate;
    @Column(name = "TRANS_SCALE")
    private String transScale;
    @Column(name = "RANK_02_JOBNO")
    private String rank_02_JobNo;
    @Column(name = "RANK_02_NAME")
    private String rank_02_Name;
    @Column(name = "RANK_03_JOBNO")
    private String rank_03_JobNo;
    @Column(name = "RANK_03_NAME")
    private String rank_03_Name;
    @Column(name = "RANK_04_JOBNO")
    private String rank_04_JobNo;
    @Column(name = "RANK_04_NAME")
    private String rank_04_Name;
    @Column(name = "RANK_05_JOBNO")
    private String rank_05_JobNo;
    @Column(name = "RANK_05_NAME")
    private String rank_05_Name;
    @Column(name = "RANK_06_JOBNO")
    private String rank_06_JobNo;
    @Column(name = "RANK_06_NAME")
    private String rank_06_Name;
    @Column(name = "RANK_07_JOBNO")
    private String rank_07_JobNo;
    @Column(name = "RANK_07_NAME")
    private String rank_07_Name;
    @Column(name = "RANK_08_JOBNO")
    private String rank_08_JobNo;
    @Column(name = "RANK_08_NAME")
    private String rank_08_Name;
    @Column(name = "RANK_09_JOBNO")
    private String rank_09_JobNo;
    @Column(name = "RANK_09_NAME")
    private String rank_09_Name;
    @Column(name = "MEMO")
    private String memo;
    @Column(name = "MAKE_UP")
    private String makeUp;

}
