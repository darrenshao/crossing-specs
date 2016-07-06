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

/**
 * @author shc
 *	Demonstration for service interface definition and implementation
 * All service interface definition should be according with the ICall interface standards.
 * params - is the only input parameter, with all other sub-parameters wrappered with JSON format.
 * String - is the only output parameter by the return way, with all other sub-parameters wrappered with JSON format also.
 * encrypt - indicates that the input params is encrypted or not.
 */
public interface ServiceSpecsExampleCall {

	/**
	 * Demo non-encrypted interface with simple input parameters and output response.
	 * @param params wrappered with JSON format: <br/>
	 * 		"{"sign":"XXXXYYYYZZZZAAAABBBBCCCCCFFFFJJJJ","params":{"name":"Shao Huicheng","say":"Hello, World","Job":"Software Engineer","Skills":"Programming,People Management etc."}}"
	 * @param encrypt params is encrypted or not
	 * @return wrappered with JSON format: <br/>
	 * 		"{"sign":"XXXXCCCCCFFFFJJJJYYYYZZZZAAAABBBB","errorCode":"0","errorInfo":"success",
	 * "params":{"sentence":"Hi, Huicheng. Glad to hear you having finished the Crossing Project. I am deeply impressed with this project."}"
	 */
	public String getSimpleReply(String params, boolean encrypt);
	
	/**
	 * Demo encrypted interface with simple parameters
	 * @param params <br/>
	 * 		"{"encrypted":"19adkfljj;adfmzfjk;[afakfjkl;adjfqrdkjk;akdfjkjkdajfjkjkadfkjdajf"}"
	 * 		corresponding plain text:   "{"sign":"DDDDTTTTZZZZAAAABBBBCCCCCFFFFJJJJ","params":{"s1":"Are","s2":"you","s3":"ready?"}}"
	 * @param encrypt
	 * @return		 <br/>
	 * 		{"encrypted":"134780akfd;jadhadleiuqptihjadghlnzvadfjkj/dfa","errorCode":"0","erroDesc":"success"}
	 * 		corresponding plain text: "{"sign":"EEEESSSSFFFFJJJJYYYYZZZZAAAABBBB","errorCode":"0","errorInfo":"success","params":{"reply":"Yes, I am ready."}}"
	 */
	public String doCopyMe(String params, boolean encrypt);
}
