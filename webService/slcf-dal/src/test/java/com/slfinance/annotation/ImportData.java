/** 
 * @(#)ImportData.java 1.0.0 2014年10月16日 上午9:48:15  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */ 

package com.slfinance.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**   
 * 
 *  
 * @author  geliang
 * @version $Revision:1.0.0, $Date: 2014年10月16日 上午9:48:15 $ 
 */
@Target({ElementType.TYPE,ElementType.METHOD})  
@Retention(RetentionPolicy.RUNTIME)  
public @interface ImportData {
	public String[] value() default {"import-data.sql"}; 
}
