package com.slfinance.shanlincaifu.utils;

/**
 * 判断密码强度类
 * @author zhangyx 20150511
 *
 */
public class PasswordUtil {
	public static Safelevel getPwdSecurityLevel(String pPasswordStr) {
        Safelevel safelevel = Safelevel.VERY_WEAK;
        if (pPasswordStr == null) {
            return safelevel;
        }
        int grade = 0;
        int index = 0;
        char[] pPsdChars = pPasswordStr.toCharArray();
 
        int numIndex = 0; //数字
        int sLetterIndex = 0; //大写字母
        int lLetterIndex = 0; //小写字母
        int symbolIndex = 0; //特殊符号
 
        for (char pPsdChar : pPsdChars) {
            int ascll = pPsdChar;
            /*
             * 数字 48-57 A-Z 65 - 90 a-z 97 - 122 !"#$%&'()*+,-./ (ASCII码：33~47)
             * :;<=>?@ (ASCII码：58~64) [\]^_` (ASCII码：91~96) {|}~
             * (ASCII码：123~126)
             */
            if (ascll >= 48 && ascll <= 57) {
                numIndex++;
            } else if (ascll >= 65 && ascll <= 90) {
                lLetterIndex++;
            } else if (ascll >= 97 && ascll <= 122) {
                sLetterIndex++;
            } else if ((ascll >= 33 && ascll <= 47)
                    || (ascll >= 58 && ascll <= 64)
                    || (ascll >= 91 && ascll <= 96)
                    || (ascll >= 123 && ascll <= 126)) {
                symbolIndex++;
            }
        }
        /*
         * 一、密码长度: 5 分: 小于等于 4 个字符 10 分: 5 到 7 字符 25 分: 大于等于 8 个字符
         */
        if (pPsdChars.length <= 8) {
            index = 5;
        } else if (pPsdChars.length <= 12) {
            index = 10;
        } else {
            index = 25;
        }
        grade += index;
 
        /*
         * 二、字母: 0 分: 没有字母 10 分: 全都是小（大）写字母 20 分: 大小写混合字母
         */
        if (lLetterIndex == 0 && sLetterIndex == 0) {
            index = 0;
        } else if (lLetterIndex != 0 && sLetterIndex != 0) {
            index = 20;
        } else {
            index = 10;
        }
        grade += index;
        /*
         * 三、数字: 0 分: 没有数字 10 分: 1 个数字 20 分: 大于 1 个数字
         */
        if (numIndex == 0) {
            index = 0;
        } else if (numIndex == 1) {
            index = 10;
        } else {
            index = 20;
        }
        grade += index;
 
        /*
         * 四、符号: 0 分: 没有符号 10 分: 1 个符号 25 分: 大于 1 个符号
         */
        if (symbolIndex == 0) {
            index = 0;
        } else if (symbolIndex == 1) {
            index = 10;
        } else {
            index = 25;
        }
        grade += index;
        /*
         * 五、奖励: 2 分: 字母和数字 3 分: 字母、数字和符号 5 分: 大小写字母、数字和符号
         */
        if ((sLetterIndex != 0 || lLetterIndex != 0) && numIndex != 0) {
            index = 2;
        } else if ((sLetterIndex != 0 || lLetterIndex != 0) && numIndex != 0
                && symbolIndex != 0) {
            index = 3;
        } else if (sLetterIndex != 0 && lLetterIndex != 0 && numIndex != 0
                && symbolIndex != 0) {
            index = 5;
        } else {
        	index = 0;
        }
        grade += index;
        
        /*
         * 最后的评分标准: >= 90: 非常安全 >= 80: 安全（Secure） >= 70: 非常强 >= 60: 强（Strong） >=
         * 50: 一般（Average） >= 25: 弱（Weak） >= 0: 非常弱
         */    
        if(grade >=90){
            safelevel = Safelevel.VERY_SECURE;
        }else if(grade >= 80){
            safelevel = Safelevel.SECURE;
        }else if(grade >= 70){
            safelevel = Safelevel.VERY_STRONG;
        }else if(grade >= 60){
            safelevel = Safelevel.STRONG;
        }else if(grade >= 50){
            safelevel = Safelevel.AVERAGE;
        }else if(grade >= 25){
            safelevel = Safelevel.WEAK;
        }else if(grade >= 0){
            safelevel = Safelevel.VERY_WEAK;
        }
        return safelevel;
    }
     
    public enum Safelevel {
        VERY_WEAK, /* 非常弱 */
        WEAK, /* 弱 */
        AVERAGE, /* 一般 */
        STRONG, /* 强 */
        VERY_STRONG, /* 非常强 */
        SECURE, /* 安全 */
        VERY_SECURE /* 非常安全 */
    }
    
	/**
	 * 获取密码强度等级
	 * @param safelevel
	 * @return
	 */
	public static String getPwdLevel(Safelevel safelevel) {
		switch(safelevel) {
		case VERY_WEAK:
		case WEAK:
		case AVERAGE:
			return Constant.PWD_LEVEL_LOW;
		case STRONG:
		case VERY_STRONG:
			return Constant.PWD_LEVEL_MIDDLE;
		case SECURE:
		case VERY_SECURE:
			return Constant.PWD_LEVEL_HIGH;
		default:
			return Constant.PWD_LEVEL_LOW;
		}
	}
    
//    public static void main(String[] args) {
//    	Safelevel safelevel = PasswordUtil.getPwdSecurityLevel("shanlin！@#");
//    	System.out.println(safelevel);
//	}
}
