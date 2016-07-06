package club.jmint.crossing.specs;

public class ReqMsg {
	public String ip;
	public long seqId;
	public String inf;
	public String params;
	public boolean isEncrypt;
	
	public ReqMsg(){}
	
	public ReqMsg(String ip, long seqId, String inf, String params, boolean isEncrypt) {
		super();
		this.ip = ip;
		this.seqId = seqId;
		this.inf = inf;
		this.params = params;
		this.isEncrypt = isEncrypt;
	}
	
	
}
