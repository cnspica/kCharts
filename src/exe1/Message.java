package exe1;

public class Message {
  
	private String from;
	private String toWho;
	private String message;
	
	
	public Message(String from,String toWho,String message){
		super();
		this.from=from;
		this.toWho=toWho;
		this.message=message;
		
	 }
	
	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}


	public String getToWho() {
		return toWho;
	}


	public void setToWho(String toWho) {
		this.toWho = toWho;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
}
