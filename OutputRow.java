package solutionCode;

public class OutputRow {

	private int mFrame;
	private String mPhone;
	private int mState;
	private double mLogProb;
	private double mLogTotalProb;
	
	public OutputRow(int pFrame, String pPhone, int pState, double pLogProb) {
		setFrame(pFrame);
		setPhone(pPhone);
		setState(pState);
		setLogProb(pLogProb);
	}
	
	private void setFrame(int pFrame) {
		this.mFrame = pFrame;
	}
	
	private void setPhone(String pPhone) {
		this.mPhone = pPhone;
	}
	
	private void setState(int pState) {
		this.mState = pState;
	}
	
	private void setLogProb(double pLogProb) {
		this.mLogProb = pLogProb;
	}
	
	public void setLogTotalProb(double pLogTotalProb) {
		this.mLogTotalProb = pLogTotalProb;
	}
	
	public int getFrame() {
		return this.mFrame;
	}
	
	public String getPhone() {
		return this.mPhone;
	}
	
	public int getState() {
		return this.mState;
	}
	
	public double getLogProb() {
		return this.mLogProb;
	}
	
	public double getLogTotalProb() {
		return this.mLogTotalProb;
	}
	
	
}
