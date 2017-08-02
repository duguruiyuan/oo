/** 
 * @(#)InjectBeanSelfProcessor.java 1.0.0 2016年4月6日 上午11:35:16  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

/**   
* @title InjectBeanSelfProcessor.java 
* @package com.slfinance.shanlinbao.utils 
* @description: TODO(用一句话描述该文件做什么) 
* @author richard.xie  
* @date 2016年4月6日 上午11:35:16 
* @version V1.0   
*/
package com.slfinance.shanlincaifu.utils;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.slfinance.shanlincaifu.service.BeanSelfAware;


/** 
 * @className: InjectBeanSelfProcessor 
 * @description: 支持类方法间调用的代理
 * @author richard.xie
 * @date 2016年4月6日 上午11:35:16 
 *  
 */
public class InjectBeanSelfProcessor implements BeanPostProcessor, ApplicationContextAware{

	 private ApplicationContext context;  
	 //① 注入ApplicationContext  
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {  
		this.context = applicationContext;  
	}  
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(!(bean instanceof BeanSelfAware)) { //如果Bean没有实现BeanSelfAware标识接口 跳过  
            return bean;  
        }  
        if(AopUtils.isAopProxy(bean)) { //③ 如果当前对象是AOP代理对象，直接注入  
            ((BeanSelfAware) bean).setSelf(bean);  
        } else {  
            //如果当前对象不是AOP代理，则通过context.getBean(beanName)获取代理对象并注入  
            //此种方式不适合解决prototype Bean的代理对象注入  
            ((BeanSelfAware)bean).setSelf(context.getBean(beanName));  
        }  
        return bean; 
	}

}
