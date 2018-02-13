package solutionCode;

public class HMMState {

	private double mStateProb;
	private String mStateLexEntry;
	private String mStatePhone;
	private int mStateSubphone;
	private int mPreviousMaxState;
	private boolean mEndOfWord;
	
	public HMMState(String pStateLexEntry, String pStatePhone,
			int pStateSubphone, double pStateProb) {
		
		setStateLexEntry(pStateLexEntry);
		setStatePhone(pStatePhone);
		setStateSubphone(pStateSubphone);
		setStateProb(pStateProb);
		this.mEndOfWord = false;
	}
	
	private void setStateProb(double pStateProb) {
		this.mStateProb = pStateProb;
	}
	
	private void setStateLexEntry(String pLexEntry) {
		this.mStateLexEntry = pLexEntry;
	}
	
	private void setStatePhone(String pStatePhone) {
		this.mStatePhone = pStatePhone;
	}
	
	private void setStateSubphone(int pStateSubphone) {
		this.mStateSubphone = pStateSubphone;
	}
	
	public void setPreviousMaxState(int pPreviousMaxState) {
		this.mPreviousMaxState = pPreviousMaxState;
	}
	
	public void setEndOfWord(boolean pEndOfWord) {
		this.mEndOfWord = pEndOfWord;
	}
	
	public double getStateProb() {
		return this.mStateProb;
	}
	
	public String getStateLexEntry() {
		return this.mStateLexEntry;
	}
	
	public String getStatePhone() {
		return this.mStatePhone;
	}
	
	public int getStateSubphone() {
		return this.mStateSubphone;
	}
	
	public int getPreviousMaxState() {
		return this.mPreviousMaxState;
	}
	
	public boolean getEndOfWord() {
		return this.mEndOfWord;
	}
}
