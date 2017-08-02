/** 
 * @(#)JsonUtil.java 1.0.0 2014年10月29日 上午10:26:04  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.utils;

import com.slfinance.exception.SLException;
import com.slfinance.util.Json;



/**   
 * Json工具类
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2014年10月29日 上午10:26:04 $ 
 */
public class JsonUtil {

	public static String jsonToString( Object object ) throws SLException {
		return Json.ObjectMapper.writeValue(object);
	}
	
}
