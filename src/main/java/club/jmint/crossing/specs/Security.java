/*
 * Copyright 2016 The Crossing Project
 *
 * The Crossing Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package club.jmint.crossing.specs;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import club.jmint.crossing.specs.ErrorCode;

/**
 * @author shc
 *
 */
public class Security {
	
	public static String MD5(String str){
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = str.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();

            int j = md.length;
            char mdstr[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                mdstr[k++] = hexDigits[byte0 >>> 4 & 0xf];
                mdstr[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(mdstr);
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
	}
	
	public static String crossingDecrypt(String encrypt, String dekey, String m) throws CrossException{
		String decryptData = desDecrypt(encrypt,dekey);
		return decryptData;
	}

	public static String crossingEncrypt(String plain, String enkey, String m) throws CrossException{
		String encryptData = desEncrypt(plain,enkey);
		return encryptData;
	}
	
	public static String crossingSign(String source, String signKey, String m){
		String signValue = MD5(source+signKey);
		return signValue;
	}
	
	
    //算法名称/加密模式/填充方式 
    //DES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
    public static final String CIPHER_DES_ALGORITHM = "DES/ECB/PKCS5Padding";

    public static String desEncrypt(String data, String key) throws CrossException {
    	String ret = null;
    	try{
	        DESKeySpec desKey = new DESKeySpec(key.getBytes("UTF-8"));
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	        SecretKey securekey = keyFactory.generateSecret(desKey);
	        
	        Cipher cipher = Cipher.getInstance(CIPHER_DES_ALGORITHM);
	        SecureRandom random = new SecureRandom();
	        cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
	        byte[] results = cipher.doFinal(data.getBytes("UTF-8"));
	        ret = Base64encode(results);
    	}catch(Exception e){
    		e.printStackTrace();
    		throw new CrossException(ErrorCode.COMMON_ERR_ENCRYPTION.getCode(),
    				ErrorCode.COMMON_ERR_ENCRYPTION.getInfo());
    	}
        return ret;
    }


    public static String desDecrypt(String data, String key) throws CrossException {
    	String ret = null;
    	try{
	        DESKeySpec desKey = new DESKeySpec(key.getBytes("UTF-8"));
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	        SecretKey securekey = keyFactory.generateSecret(desKey);
	
	        Cipher cipher = Cipher.getInstance(CIPHER_DES_ALGORITHM);
	        cipher.init(Cipher.DECRYPT_MODE, securekey);
	        
	        ret = new String(cipher.doFinal(Base64decode(data)));
    	}catch(Exception e){
    		e.printStackTrace();
    		throw new CrossException(ErrorCode.COMMON_ERR_DECRYPTION.getCode(),
    				ErrorCode.COMMON_ERR_DECRYPTION.getInfo());
    	}
    	return ret;
    }

//    public static void main(String[] args) throws Exception {
//        String source = "amigoxiejkdajfkljnvcxv.alfdajlsfhiqeporueqwjk;dasfjkadjsfkdajfkdajsfkdsajkfjjkj";
//        System.out.println("原文: " + source);
//        String key = "ILOVEYOu";
//        String encryptData = desEncrypt(source, key);
//        System.out.println("加密后: " + encryptData);
//        String decryptData = desDecrypt(encryptData, key);
//        System.out.println("解密后: " + decryptData);
//        System.out.println(source.equals(decryptData));
//    }

	/**
	 * Base64 encode
	 * @param bstr
	 * @return String
	 */
	public static String Base64encode(byte[] bstr) {
		return new sun.misc.BASE64Encoder().encode(bstr);
	}

	/**
	 * Base64 decode
	 * @param str
	 * @return string
	 */
	public static byte[] Base64decode(String str) {
		byte[] bt = null;
		try {
			sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
			bt = decoder.decodeBuffer(str);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bt;
	}
}
