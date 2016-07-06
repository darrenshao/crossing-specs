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
class ECBuilder{
	final public static int EC_MAIN_HEADER = 0x01;	//applied from Crossing Administrator
	final public static int EC_SUB_HEADER = 0x00;	//applied from Crossing Administrator
	public static int ERRORCODE(int ec){
		int errcode = EC_MAIN_HEADER;
		int subheader = EC_SUB_HEADER;
		errcode = errcode << 24;
		subheader = subheader << 16;
		return (errcode | subheader | ec);
	}
	public static int ERRORCODE(int sub_header, int ec){
		int errcode = EC_MAIN_HEADER;
		int subheader = sub_header;
		errcode = errcode << 24;
		subheader = subheader << 16;
		return (errcode | subheader | ec);
	}
	public static int ERRORCODE(int main_header, int sub_header, int ec){
		int errcode = main_header;
		int subheader = sub_header;
		errcode = errcode << 24;
		subheader = subheader << 16;
		return (errcode | subheader | ec);
	}	
};

/**
 * Define standardized error code
 * @author shc
 *
 */
public enum ErrorCode {
	/**
	 * All runtime error codes defined here,
	 * 0x00 00 0000 - 0xFF FF FFFF
	 * first 2 digits: used for server type
	 * following 2 digits: used for service/module/subsystem 
	 * last 4 digits: used for detailed errors
	 */
	//Success
	SUCCESS(0x00000000,"Success"),

	//Crossing ErrorCode： 0x01 00 0000 - 0x01 FF FFFF
	CROSSING_ERR_UNAUTHORIZED_IP(ECBuilder.ERRORCODE(0x0001), "Unauthorized client IP."),
	CROSSING_ERR_UNAUTHORIZED_INF(ECBuilder.ERRORCODE(0x0002), "Attempt to access unauthorized interface."),
	CROSSING_ERR_SERVER_NOT_FOUND(ECBuilder.ERRORCODE(0x0003), "Server not found."),
	CROSSING_ERR_CLASS_NOT_FOUND(ECBuilder.ERRORCODE(0x0004), "Class not found."),
	CROSSING_ERR_INTERFACE_NOT_FOUND(ECBuilder.ERRORCODE(0x0005), "Interface not found."),
	CROSSING_ERR_CLIENT_STARTUP(ECBuilder.ERRORCODE(0x0006), "Client startup failed."),
	CROSSING_ERR_MALFORMED_CALL(ECBuilder.ERRORCODE(0x0007), "Malfromed call."),
	CROSSING_ERR_CORRUPTED_FRAME(ECBuilder.ERRORCODE(0x0008), "Corrupted frame."),
	CROSSING_ERR_IO(ECBuilder.ERRORCODE(0x0009), "IO error."),
	
	CROSSING_ERR_INTERNAL(ECBuilder.ERRORCODE(0xffff), "Server internal error."),

	//Common ErrorCodes： 0xFF 00 0000 - 0xFF FF FFFE
	COMMON_ERR_PARAM_MALFORMED(ECBuilder.ERRORCODE(0xff, 0x00, 0x0011), "Parameter malformed."),
	COMMON_ERR_PARAM_MISSING(ECBuilder.ERRORCODE(0xff, 0x00, 0x0012), "Parameter missed."),

	COMMON_ERR_SIGN_BAD(ECBuilder.ERRORCODE(0xff, 0x00, 0x0021), "Bad signature."),
	COMMON_ERR_SIGN_MISSING(ECBuilder.ERRORCODE(0xff, 0x00, 0x0022), "Missing signature."),

	COMMON_ERR_ENCRYPTION(ECBuilder.ERRORCODE(0xff, 0x00, 0x0031), "Encryption error."),
	COMMON_ERR_DECRYPTION(ECBuilder.ERRORCODE(0xff, 0x00, 0x0032), "Decryption error."),

	//Always Unkown
	COMMON_ERR_UNKOWN(0xffffffff, "Unkown error.");

	//DO NOT CHANGE BELLOW.
	private int code;
	private String info;

	private ErrorCode(int code, String info){
		this.code = code;
		this.info = info;
	}

	public int getCode() {
		return code;
	}

	public String getInfo() {
		return info;
	}
}
