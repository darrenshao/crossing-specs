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
 *
 */
public class CrossException extends Exception {
	private int errorCode;
	private String errorInfo;
	private static final long serialVersionUID = 3492482886485598737L;

	public CrossException(String message) {
		super(message);
	}

	public CrossException(Exception e) {
		super(e);
	}

	public CrossException() {
		super();
	}

	public CrossException(Throwable cause) {
		super(cause);
	}

	public CrossException(String message, Throwable cause) {
		super(message, cause);
	}	
	
	public CrossException(int errorCode, String errorInfo){
		//super(errorInfo);
		this.errorCode = errorCode;
		this.errorInfo = errorInfo;
	}
	
	public int getErrorCode(){
		return errorCode;
	}
	
	public String getErrorInfo(){
		return errorInfo;
	}
	
	public String toString(){
		return getMessage()+"[errorCode=" + errorCode + ",errorInfo="+ errorInfo + "]";
	}
}
