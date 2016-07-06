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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Iterator;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import club.jmint.crossing.specs.ErrorCode;

/**
 * @author shc
 *
 */
public class ParamBuilder {
	public static String createErrorParams(int errorcode, String errorinfo){
		JsonObject jo = new JsonObject();
		jo.addProperty("errorCode", errorcode);
		jo.addProperty("errorInfo", errorinfo);
		String p = jo.toString();
		return p;
	}
	
	public static String getErrorInfo(String jsonParams) throws CrossException{
		JsonParser jp = new JsonParser();
		JsonObject jo;
		try{
			jo = (JsonObject)jp.parse(jsonParams);
		}catch(JsonSyntaxException e){
			throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
					ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
		}
		if (jo.has("errorInfo")){
			String value = jo.getAsJsonPrimitive("errorInfo").getAsString();
			return value;
		}
		else{
			throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MISSING.getCode(),
					ErrorCode.COMMON_ERR_PARAM_MISSING.getInfo());
		}
	}
	
	public static int getErrorCode(String jsonParams) throws CrossException{
		JsonParser jp = new JsonParser();
		JsonObject jo;
		try{
			jo = (JsonObject)jp.parse(jsonParams);
		}catch(JsonSyntaxException e){
			throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
					ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
		}
		if (jo.has("errorCode")){
			String value = jo.getAsJsonPrimitive("errorCode").getAsString();
			return Integer.parseInt(value);
		}
		else{
			throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MISSING.getCode(),
					ErrorCode.COMMON_ERR_PARAM_MISSING.getInfo());
		}
	}
	
	public static JsonObject getParams(String jsonParams) throws CrossException{
		JsonParser jp = new JsonParser();
		JsonObject jo;
		try{
			jo = (JsonObject)jp.parse(jsonParams);
		}catch(JsonSyntaxException e){
			throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
					ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
		}
		
		if (jo.has("params")){
			return jo.getAsJsonObject("params");
		}
		else{
			return null;
		}
	}


	public static String buildSignedParams(String p, String signKey) throws CrossException{
		String md5Value = Security.crossingSign(p, signKey, "");
		JsonObject jo = new JsonObject();
		jo.addProperty("sign", md5Value);
		JsonParser jp = new JsonParser();
		JsonElement jop = null;
		try{
			jop = jp.parse(p);
			if (!jop.isJsonObject()){
				throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
						ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
			}
		}catch(JsonSyntaxException jse){
			throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
					ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
		}
		
		jo.add("params", jop);
		return jo.toString();
	}

	public static String buildEncryptedParams(String signedParams, String encryptKey) throws CrossException {
		String enValue = Security.crossingEncrypt(signedParams, encryptKey, "DES");
		String encrypted = "{\"encrypted\":\""+  enValue  +"\"}";
		return encrypted;
	}

	public static String checkSignAndRemove(String p, String signKey) throws CrossException{
		JsonParser jp = new JsonParser();
		JsonElement jop = null;
		try{
			jop = jp.parse(p);
			if (!jop.isJsonObject()){
				throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
						ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
			}
		}catch(JsonSyntaxException jse){
			//jse.printStackTrace();
			throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
					ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
		}		
		
		JsonObject jo = (JsonObject) jop;
		String np = null;
		if (jo.has("sign") || jo.has("params")){
			String sign = jo.getAsJsonPrimitive("sign").getAsString();
			np = jo.getAsJsonObject("params").toString();
			String signValue = Security.crossingSign(np,signKey,"");
			if (!sign.equals(signValue)){
				throw new CrossException(ErrorCode.COMMON_ERR_SIGN_BAD.getCode(),
						ErrorCode.COMMON_ERR_SIGN_BAD.getInfo());
			}
			
			//remove sign field only and return
			Iterator<Entry<String,JsonElement>> it = jo.entrySet().iterator();
			JsonObject rjo = new JsonObject();
			Entry<String, JsonElement> en;
			while(it.hasNext()){
				en = it.next();
				if (!en.getKey().equals("sign")){
					rjo.add(en.getKey(), en.getValue());
				}
			}
			return rjo.toString();
		}
		
		return p;
	}

	//encrypted: {"encrypted":"xxxxxxxxxxxxxxxx","errorCode":"0","errorInfo":"Success"}
	//decrypted: {"params":{"key1":"value1","key2":"value2"},"sign":"signValue","errorCode":"0","erroDesc":"success"}
	public static String buildDecryptedParams(String encryptParams, String decryptKey) throws CrossException{
		JsonParser jp = new JsonParser();
		JsonElement jop = null;
		try{
			jop = jp.parse(encryptParams);
			if (!jop.isJsonObject()){
				throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
						ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
			}
		}catch(JsonSyntaxException jse){
			//jse.printStackTrace();
			throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
					ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
		}		
		
		JsonObject jo = (JsonObject) jop;
		String np = null;
		if (jo.has("encrypted")){
			String encrypted = jo.getAsJsonPrimitive("encrypted").getAsString();
			String paramsValue = Security.crossingDecrypt(encrypted,decryptKey,"DES");
			int ec = jo.getAsJsonPrimitive("errorCode").getAsInt();
			String ed = jo.getAsJsonPrimitive("errorInfo").getAsString();
			JsonParser jpr = new JsonParser();
			JsonObject jor = null;
			try{
				jor = (JsonObject)jpr.parse(paramsValue);
			}catch(JsonSyntaxException je){
				throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
						ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
			}
			jor.addProperty("errorCode", ec);
			jor.addProperty("errorInfo", ed);
			np = jor.toString();
			return np;
		}
		
		return encryptParams;
	}
	
	/**
	 * @param inf, format: xxx@yyy@zzz
	 * @return yyy
	 */
	public static String getServiceNameFromInf(String inf){
		String[] element = inf.split("@");
		return element[1];
	}
	
	public static String getServerNameFromInf(String inf){
		String[] element = inf.split("@");
		return element[0];
	}
	
	public static String getMethodNameFromInf(String inf){
		String[] element = inf.split("@");
		return element[2];
	}
	
	public static boolean checkCallInfFormat(String inf) throws CrossException {
		//implement later
		return true;
	}
	
	public static boolean checkCallParamFormat(String param) throws CrossException {
		//implement later
		return true;
	}
}
