package com.slfinance.shanlincaifu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * 代扣账户信息表
 * 张祥
 */
@Entity
@Table(name = "BAO_T_WITHHOLD_ACCOUNT")
public class WithholdAccountEntity extends BaseEntity{

    private static final long serialVersionUID = -4693656026264834406L;

    private String companyName;

    private String accountProceedsId;//ACCOUNT_PROCEEDS_ID 风险金账户id

    private String accountPrepareId;//ACCOUNT_PREPARE_ID  备付金账户Id

    private String loginName;

    private String contractType;

    private String contactWay;

    @Column(name = "COMPANY_NAME",length = 50)
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Column(name = "ACCOUNT_PROCEEDS_ID",length = 150)
    public String getAccountProceedsId() {
        return accountProceedsId;
    }

    public void setAccountProceedsId(String accountProceedsId) {
        this.accountProceedsId = accountProceedsId;
    }

    @Column(name = "ACCOUNT_PREPARE_ID",length = 150)
    public String getAccountPrepareId() {
        return accountPrepareId;
    }

    public void setAccountPrepareId(String accountPrepareId) {
        this.accountPrepareId = accountPrepareId;
    }

    @Column(name = "LOGIN_NAME",length = 150)
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Column(name = "CONTRACT_TYPE",length = 150)
    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    @Column(name = "CONTACT_WAY",length = 150)
    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }
}

