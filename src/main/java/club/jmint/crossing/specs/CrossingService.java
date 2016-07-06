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
import com.google.gson.JsonSyntaxException;

/**
 * @author shc
 *
 */
public class CrossingService {
	protected String signType;
	protected String signKey;
	
	protected String encryptType;
	protected String enKey;
	protected String deKey;

	
	/**
	 * @return the signKey
	 */
	protected String getSignKey() {
		return signKey;
	}

	/**
	 * @param signKey the signKey to set
	 */
	protected void setSignKey(String signKey) {
		this.signKey = signKey;
	}

	/**
	 * @return the enKey
	 */
	protected String getEnKey() {
		return enKey;
	}

	/**
	 * @param enKey the enKey to set
	 */
	protected void setEnKey(String enKey) {
		this.enKey = enKey;
	}

	/**
	 * @return the deKey
	 */
	protected String getDeKey() {
		return deKey;
	}

	/**
	 * @param deKey the deKey to set
	 */
	protected void setDeKey(String deKey) {
		this.deKey = deKey;
	}

	/**
	 * @return the signType
	 */
	protected String getSignType() {
		return signType;
	}

	/**
	 * @param signType the signType to set
	 */
	protected void setSignType(String signType) {
		this.signType = signType;
	}

	/**
	 * @return the encryptType
	 */
	protected String getEncryptType() {
		return encryptType;
	}

	/**
	 * @param encryptType the encryptType to set
	 */
	protected void setEncryptType(String encryptType) {
		this.encryptType = encryptType;
	}

	protected String buildSign(String source, String signKey){
		 return Security.crossingSign(source, signKey, "MD5");
	}
	
	protected String buildEncrypt(String plain) throws CrossException{
		return Security.crossingEncrypt(plain,enKey,"");
	}
	
	protected String buildDecrypt(String encrypted) throws CrossException{
		return Security.crossingDecrypt(encrypted,deKey,"");
	}
	
	protected JsonObject parseInputParams(String params, boolean encrypt) throws CrossException{
		//parsing
		JsonParser jp = new JsonParser();
		JsonObject jo,jode,joparams;
		try{
			jo = (JsonObject)jp.parse(params);
		}catch(JsonSyntaxException e){
			throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
					ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
		}
		
		String encrypted,jsonParams;
		if (encrypt){
			if (!jo.has("encrypted")){
				throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MISSING.getCode(),
						ErrorCode.COMMON_ERR_PARAM_MISSING.getInfo());
			}
			encrypted = jo.getAsJsonPrimitive("encrypted").getAsString();
			jsonParams = buildDecrypt(encrypted);
			if (jsonParams==null){
				throw new CrossException(ErrorCode.COMMON_ERR_DECRYPTION.getCode(),
						ErrorCode.COMMON_ERR_DECRYPTION.getInfo());
			}
			try{
				jode = (JsonObject)jp.parse(jsonParams);
			}catch(JsonSyntaxException e){
				throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
						ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
			}
			jo = jode;
		}
		
		
		//Check signature
		if (!jo.has("sign")){
			throw new CrossException(ErrorCode.COMMON_ERR_SIGN_MISSING.getCode(),
					ErrorCode.COMMON_ERR_SIGN_MISSING.getInfo());
		}
		String signValue = jo.getAsJsonPrimitive("sign").getAsString();
		if (!jo.has("params")){
			throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MISSING.getCode(),
					ErrorCode.COMMON_ERR_PARAM_MISSING.getInfo());
		}
		String inputparams = jo.getAsJsonObject("params").toString();
		
		String signCheck = buildSign(inputparams, signKey);
		if (!signCheck.equals(signValue)){
			throw new CrossException(ErrorCode.COMMON_ERR_SIGN_BAD.getCode(),
					ErrorCode.COMMON_ERR_SIGN_BAD.getInfo());
		}
		
		try{
			joparams = (JsonObject)jp.parse(inputparams);
		}catch(JsonSyntaxException e){
			throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MALFORMED.getCode(),
					ErrorCode.COMMON_ERR_PARAM_MALFORMED.getInfo());
		}
		
		return joparams;
	}
	
	/**
	 * 
	 * @param errorCode
	 * @param errorInfo
	 * @return
	 */
	protected String buildOutputError(int errorCode,String errorInfo){
		JsonObject jo = new JsonObject();
		jo.addProperty("errorCode", errorCode);
		jo.addProperty("errorInfo", errorInfo);
		return jo.toString();
	}
	
	protected String buildOutputError(CrossException ce){
		JsonObject jo = new JsonObject();
		jo.addProperty("errorCode", ce.getErrorCode());
		jo.addProperty("errorInfo", ce.getErrorInfo());
		return jo.toString();
	}
	
	/**
	 * 
	 * @param obj
	 * @param encrypt
	 * @return
	 */
	protected String buildOutputParams(JsonObject obj, boolean encrypt) throws CrossException{
		if (obj==null || obj.toString().isEmpty() ){
			//we do resume that it's successful without any response parameters.
			JsonObject jo = new JsonObject();
			jo.addProperty("errorCode", ErrorCode.SUCCESS.getCode());
			jo.addProperty("errorInfo", ErrorCode.SUCCESS.getInfo());
			return jo.toString();
		}
		
		//create signature
		String op = obj.toString();
		String signValue = buildSign(op, signKey);
		
		JsonObject jo = new JsonObject();
		jo.addProperty("sign", signValue);
		jo.add("params", obj);
		
		String encrypted;
		if (encrypt){
			//encrypt output
			encrypted = buildEncrypt(jo.toString());
			JsonObject joen = new JsonObject();
			joen.addProperty("encrypted", encrypted);
			joen.addProperty("errorCode", ErrorCode.SUCCESS.getCode());
			joen.addProperty("errorInfo", ErrorCode.SUCCESS.getInfo());	
			return joen.toString();
		}
		else {
			//non-encrypt output
			jo.addProperty("errorCode", ErrorCode.SUCCESS.getCode());
			jo.addProperty("errorInfo", ErrorCode.SUCCESS.getInfo());		
			return jo.toString();
		}

	}
	
	/**
	 * There exist these kinds of circumstances that we need return by given Errorcode with some output parameters
	 * @param obj
	 * @param encrypt
	 * @param errorCode
	 * @param errorInfo
	 * @return
	 */
	protected String buildOutputParamsWithGivenErrorCode(JsonObject obj, boolean encrypt, 
			int errorCode, String errorInfo) throws CrossException{
		if (obj==null){
			return buildOutputError(errorCode, errorInfo);
		}
		
		//create signature
		String op = obj.toString();
		String signValue = buildSign(op, signKey);
		
		JsonObject jo = new JsonObject();
		jo.addProperty("sign", signValue);
		jo.addProperty("params", op);
		
		String encrypted;
		if (encrypt){
			//encrypt output
			encrypted = buildEncrypt(jo.toString());
			JsonObject joen = new JsonObject();
			joen.addProperty("encrypted", encrypted);
			joen.addProperty("errorCode", ErrorCode.SUCCESS.getCode());
			joen.addProperty("errorInfo", ErrorCode.SUCCESS.getInfo());	
			return joen.toString();
		}
		else {
			//non-encrypt output
			jo.addProperty("errorCode", ErrorCode.SUCCESS.getCode());
			jo.addProperty("errorInfo", ErrorCode.SUCCESS.getInfo());		
			return jo.toString();
		}		
	}
	
	protected String buildOutputByCrossException(CrossException e){
		return buildOutputError(e);
	}
	
	protected String getParamAsString(JsonObject params, String key) throws CrossException{
		if (params!=null){
			if (!params.has(key)){
				throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MISSING.getCode(),
						ErrorCode.COMMON_ERR_PARAM_MISSING.getInfo());
			}
			return params.getAsJsonPrimitive(key).getAsString();
		}
		return null;
	}
	
	protected int getParamAsInt(JsonObject params, String key) throws CrossException{
		if (params!=null){
			if (!params.has(key)){
				throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MISSING.getCode(),
						ErrorCode.COMMON_ERR_PARAM_MISSING.getInfo());
			}
			return params.getAsJsonPrimitive(key).getAsInt();
		}
		return -1;		
	}
	
	protected boolean getParamAsBoolean(JsonObject params, String key) throws CrossException{
		if (params!=null){
			if (!params.has(key)){
				throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MISSING.getCode(),
						ErrorCode.COMMON_ERR_PARAM_MISSING.getInfo());
			}
			return params.getAsJsonPrimitive(key).getAsBoolean();
		}
		return false;			
	}
	
	protected long getParamAsLong(JsonObject params, String key) throws CrossException{
		if (params!=null){
			if (!params.has(key)){
				throw new CrossException(ErrorCode.COMMON_ERR_PARAM_MISSING.getCode(),
						ErrorCode.COMMON_ERR_PARAM_MISSING.getInfo());
			}
			return params.getAsJsonPrimitive(key).getAsLong();
		}
		return -1;			
	}
	
	
}
