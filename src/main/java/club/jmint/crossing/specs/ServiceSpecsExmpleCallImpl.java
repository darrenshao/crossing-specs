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

import club.jmint.crossing.specs.CrossingService;

/**
 * @author shc
 *
 */
public class ServiceSpecsExmpleCallImpl extends CrossingService implements ServiceSpecsExampleCall {
	
	public ServiceSpecsExmpleCallImpl(){
		setSignType("");
		setSignKey("xxxServer@Howvan.comSignkey");
		setEncryptType("");
		setEnKey("");
		setDeKey("");
	}

	/**
	 * Demo non-encrypted interface with simple input parameters and output response.
	 * @param params wrappered with JSON format: <br/>
	 * 		"{"sign":"XXXXYYYYZZZZAAAABBBBCCCCCFFFFJJJJ","params":{"name":"Shao Huicheng","say":"Hello, World","job":"Software Engineer","skills":"Programming,People Management etc."}}"
	 * @param encrypt params is encrypted or not
	 * @return wrappered with JSON format: <br/>
	 * 		"{"sign":"XXXXCCCCCFFFFJJJJYYYYZZZZAAAABBBB","errorCode":"0","errorInfo":"success",
	 * "params":{"sentence":"Hi, Huicheng. Glad to hear you having finished the Crossing Project. I am deeply impressed with this project."}"
	 */
	public String getSimpleReply(String params, boolean encrypt) {
		//parse parameters and verify signature
		JsonObject ip;
		try{
			ip = parseInputParams(params, encrypt);
		}catch(CrossException ce){
			return buildOutputByCrossException(ce);
		}
		
		//validate parameters on business side
		String name,say,job,skills;
		try{
			name = getParamAsString(ip, "name");
			say = getParamAsString(ip, "say");
			job = getParamAsString(ip, "job");
			skills = getParamAsString(ip, "skills");
		}catch(CrossException ce){
			return buildOutputByCrossException(ce);
		}
		System.out.println("name: " + name);
		System.out.println("say: " + say);
		System.out.println("job: " + job);
		System.out.println("skills: " + skills);
		//do more checks here
		
		
		//do your business logics
		String sentence = "Hi, " + name + "Glad to hear you having finished the Crossing Project. I am deeply impressed with this project.";
		//do more things here.
		
		
		//build response parameters
		JsonObject op = new JsonObject();
		op.addProperty("sentence", sentence);
		String output = null;
		try{
			output = buildOutputParams(op, encrypt);
		}catch(CrossException ce){
			return buildOutputByCrossException(ce);
		}
		return output;
	}

	/**
	 * Demo encrypted interface with simple parameters
	 * @param params <br/>
	 * 		"{"encrypted":"19adkfljj;adfmzfjk;[afakfjkl;adjfqrdkjk;akdfjkjkdajfjkjkadfkjdajf"}" <br/>
	 * 		corresponding plain text:   "{"sign":"DDDDTTTTZZZZAAAABBBBCCCCCFFFFJJJJ","params":{"s1":"Are","s2":"you","s3":"ready?"}}"
	 * @param encrypt
	 * @return		 <br/>
	 * 		{"encrypted":"134780akfd;jadhadleiuqptihjadghlnzvadfjkj/dfa","errorCode":"0","erroDesc":"success"} <br/>
	 * 		corresponding plain text: "{"sign":"EEEESSSSFFFFJJJJYYYYZZZZAAAABBBB","errorCode":"0","errorInfo":"success","params":{"reply":"Yes, I am ready."}}"
	 */
	public String doCopyMe(String params, boolean encrypt) {
		//parse parameters and verify signature
		JsonObject ip;
		try{
			ip = parseInputParams(params, encrypt);
		}catch(CrossException ce){
			return buildOutputByCrossException(ce);
		}
		
		//validate parameters on business side
		String s1,s2,s3;
		try{
			s1 = getParamAsString(ip, "s1");
			s2 = getParamAsString(ip, "s2");
			s3 = getParamAsString(ip, "s3");
			
		}catch(CrossException ce){
			return buildOutputByCrossException(ce);
		}
		System.out.println("s1: " + s1);
		System.out.println("s2: " + s2);
		System.out.println("s3: " + s3);
		
		//do more checks here
		
		
		//do your business logics
		String copyme = s1 + " "+ s2 + " "+ s3;
		//do more things here.
		
		
		//build response parameters
		JsonObject op = new JsonObject();
		op.addProperty("sentence", copyme);
		String output = null;
		try{
			output = buildOutputParams(op, encrypt);
		}catch(CrossException ce){
			return buildOutputByCrossException(ce);
		}
		return output;
	}

}
