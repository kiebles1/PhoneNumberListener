package solutionCode;

import java.util.ArrayList;
import java.util.List;

public class LexiconEntry {
	
	private String mEnglishName;
	private List<String> mPhones;
	
	public LexiconEntry(String pEnglishName, List<String> pPhones) {
		setEnglishName(pEnglishName);
		setPhones(pPhones);
	}
	
	private void setEnglishName(String pEnglishName) {
		this.mEnglishName = pEnglishName;
	}
	
	public String getEnglishName() {
		assert (this.mEnglishName != null) : this;
		return this.mEnglishName;
	}
	
	private void setPhones(List<String> pPhones) {
		this.mPhones = new ArrayList<String>();
		if(this.mPhones.isEmpty()) {
			this.mPhones.addAll(pPhones);
		}
		else {
			System.err.println("The list of phones is already populated");
		}	
	}
	
	public List<String> getPhones() {
		assert (this.mPhones != null & !this.mPhones.isEmpty()) : this;
		List<String> lPhones = new ArrayList<String>();
		lPhones.addAll(this.mPhones);
		return lPhones;
	}

}
