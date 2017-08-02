package com.slfinance.shanlincaifu.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.SpecialUsersUrlEntity;
import com.slfinance.shanlincaifu.repository.SpecialUsersUrlRepository;
import com.slfinance.shanlincaifu.service.SpecialUsersUrlService;
import com.slfinance.vo.ResultVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 特殊用户标的设置有效期
 *
 * @author  mali
 * @version $Revision:1.0.0, $Date: 2017年7月21日 下午11:16:12 $
 */
@Service("specialUsersUrlService")
public class SpecialUsersUrlServiceImpl implements SpecialUsersUrlService {

    @Autowired
    private SpecialUsersUrlRepository specialUsersUrlRepository;

    /**
     * 生成链接
     * @param param
     * @return
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = SLException.class)
    public ResultVo genericExpire(Map<String, Object> param) {
    	
    	//操作人ID
    	String custId = (String) param.get("custId") == null ? "" : (String) param.get("custId");
    	String url = (String) param.get("url") == null ? "" : (String) param.get("url");
       //生成token
        String token = UUID.randomUUID().toString();
        //将token拼接在url后面
        String specialUrl = url + "?token=" + token;
        
        SpecialUsersUrlEntity specialUsersUrlEntity = new SpecialUsersUrlEntity();
        Calendar now=Calendar.getInstance();
        now.add(Calendar.HOUR, 2);
        Date expireDate = new Date(now.getTimeInMillis());
        specialUsersUrlEntity.setStartTime(new Date());
        specialUsersUrlEntity.setExpireDate(expireDate);
        specialUsersUrlEntity.setPurchaseUrl(specialUrl);
        specialUsersUrlEntity.setToken(token);
        specialUsersUrlEntity.setBasicModelProperty(custId, true);
        specialUsersUrlRepository.save(specialUsersUrlEntity);
    	return new ResultVo(true,"生成连接成功",specialUrl);
    }

    /**
     * 查询token是否失效
     * @param param
     * @return
     */
	@Override
	public ResultVo queryByToken(Map<String, Object> param) {
		
		String token = (String) param.get("token") == null ? "" : (String) param.get("token");
		SpecialUsersUrlEntity specialUsersUrlEntity = specialUsersUrlRepository.findBytoken(token);
		if (null == specialUsersUrlEntity) {
			return new ResultVo(false,"token不存在");
		}
		Date expireDate = specialUsersUrlEntity.getExpireDate();
		long expireTime = expireDate.getTime();
		long now = new Date().getTime();
		//当前时间大于过期时间
		if (now > expireTime) {
			return new ResultVo(false,"token已失效");
		}
		return new ResultVo(true,"token未失效");
	}
}
