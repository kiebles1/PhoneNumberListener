package solutionCode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solution {

	public static void main(String[] args) {
		List<String> myPhones = new ArrayList<String>();
		myPhones = getPhones();
		List<LexiconEntry> myLexicon = new ArrayList<LexiconEntry>();
		myLexicon = getLexicon();
		List<List<OutputRow>> myOutput = new ArrayList<List<OutputRow>>();
		myOutput = getOutput(myPhones.size() * 3);
		ViterbiDecode(myLexicon, myOutput, myPhones);
	}

	private static List<String> getPhones() {
		String fileName = "./InputFiles/phonemes.txt";
		List<String> allPhones = new ArrayList<String>();
		String currentString = new String();
		FileReader readPhones;
		try {
			readPhones = new FileReader(fileName);
			BufferedReader myReader = new BufferedReader(readPhones);
			while((currentString = myReader.readLine()) != null) {
				allPhones.add(currentString);
			}
			readPhones.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}		
		return allPhones;
	}

	private static List<LexiconEntry> getLexicon() {
		String fileName = "./InputFiles/lexicon.txt";
		List<LexiconEntry> myLexicon = new ArrayList<LexiconEntry>();
		String currentLine;
		FileReader readLexicon;
		try {
			readLexicon = new FileReader(fileName);
			BufferedReader myReader = new BufferedReader(readLexicon);
			Pattern lexiconRegex = Pattern.compile("(\\w+)([\\t\\x0B\\f\\r\\W]+)");
			while((currentLine = myReader.readLine()) != null) {
				String englishWord;
				List<String> myPhones = new ArrayList<String>();
				Matcher lexiconMatcher = lexiconRegex.matcher(currentLine);
				lexiconMatcher.find();
				englishWord = lexiconMatcher.group(1);
//				System.out.println(englishWord);
				while(lexiconMatcher.find()) {
					myPhones.add(lexiconMatcher.group(1));
				}
				myLexicon.add(new LexiconEntry(englishWord, myPhones));
			}
			readLexicon.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}		

		return myLexicon;
	}

	private static List<List<OutputRow>> getOutput(int pPossibleStates) {
		String fileName = "./InputFiles/output.txt";
		List<List<OutputRow>> myOutput = new ArrayList<List<OutputRow>>();
		FileReader readOutput;
		try {
			readOutput = new FileReader(fileName);
			BufferedReader myReader = new BufferedReader(readOutput);
			Pattern lexiconRegex = Pattern.compile(
					"(\\w+)(\\s+)(\\w+)(\\s+)(\\w+)(\\s+)([\\w\\W]+)");
			for(int i = 0; i<558; i++) {
				List<OutputRow> currentObservation = new ArrayList<OutputRow>();
				for(int j = 0; j<pPossibleStates; j++) {
					Matcher lexiconMatcher = lexiconRegex.matcher(myReader.readLine());
					while(lexiconMatcher.find()) {
						currentObservation.add(new OutputRow(
								Integer.parseInt(lexiconMatcher.group(1)), 
								lexiconMatcher.group(3),
								Integer.parseInt(lexiconMatcher.group(5)),
								Double.parseDouble(lexiconMatcher.group(7))));
					}
				}
				myOutput.add(currentObservation);
			}
			readOutput.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}		

		return myOutput;

	}

	private static void ViterbiDecode(List<LexiconEntry> myLexicon, 
			List<List<OutputRow>> myOutput, List<String> myPhones) {

		List<List<HMMState>> myViterbiLatice = new ArrayList<List<HMMState>>();
		int observationLoopSize = myPhones.size()*3;
		HMMState currentHMMState = null;
		OutputRow currentRow;

		for(int i=0; i<myOutput.size(); i++) {
			myViterbiLatice.add(new ArrayList<HMMState>());
			//Set the total probabilities of each state for the first
			//observation equal to the the observation probabilities
			//for that state. If the state is not a possible starting state,
			//set the total probability to 0. 
			for(int j=0; j<observationLoopSize; j++) {
				currentRow = myOutput.get(i).get(j);
				for(int k=0; k<myLexicon.size(); k++) {
					if(i == 0) {
						if((currentRow.getPhone().equals(myLexicon.get(k).getPhones().get(0)))
								&& currentRow.getState() == 0) {
							currentHMMState = new HMMState(
									myLexicon.get(k).getEnglishName(),
									myLexicon.get(k).getPhones().get(0),
									0, currentRow.getLogProb());
							myViterbiLatice.get(i).add(currentHMMState);
						}
					}
					else {
						//Loop through the phones within the current word of the lexicon
						//to see if one or more of them match the current output phone
						for(int x=0; x<myLexicon.get(k).getPhones().size(); x++) {
							double runningProb = 0;
							int previousMaxIndex = 0;
							boolean validState;
							if(currentRow.getPhone().equals(myLexicon.get(k).getPhones().get(x))) {
								if(currentRow.getState() == 0) {
									validState = false;
									//Loop through the possible states associated with the 
									//observation of the previous column in the viterbi 
									//lattice
									for(int y=0; y<myViterbiLatice.get(i-1).size(); y++) {
										double wordChangePenalty = 0;
										boolean validPhone = true;
										if(myViterbiLatice.get(i-1).get(y).getStateSubphone() == 0 &&
												myViterbiLatice.get(i-1).get(y).getStatePhone().equals(
														currentRow.getPhone())
														&& myViterbiLatice.get(i-1).get(y).getStateLexEntry().equals(
																myLexicon.get(k).getEnglishName())) {
											
											validState = true;

											if(runningProb == 0) {
												runningProb = myViterbiLatice.get(i-1).get(y).getStateProb();
												previousMaxIndex = y;;
											}

											else {
												if(myViterbiLatice.get(i-1).get(y).getStateProb() > runningProb) {
													runningProb = myViterbiLatice.get(i-1).get(y).getStateProb();
													previousMaxIndex = y;
												}
											}

											currentHMMState = new HMMState(
													myLexicon.get(k).getEnglishName(),
													myLexicon.get(k).getPhones().get(x),
													currentRow.getState(), runningProb + currentRow.getLogProb());
											currentHMMState.setPreviousMaxState(previousMaxIndex);
											

										}
										else if(myViterbiLatice.get(i-1).get(y).getStateSubphone() == 2) {
//											if(currentRow.getPhone().equals("OW")) {
//												System.out.println("STOP HERE");
//											}
											if(x == 0) {
												if(myViterbiLatice.get(i-1).get(y).getEndOfWord()) {
													wordChangePenalty = -50;
												}
												else {
													validPhone = false;
												}
											}
											else {
												if(!(myViterbiLatice.get(i-1).get(y).getStatePhone().equals(
														myLexicon.get(k).getPhones().get(x-1)) && 
														myViterbiLatice.get(i-1).get(y).getStateLexEntry().equals(
																myLexicon.get(k).getEnglishName()))) {
													validPhone = false;
												}
											}
											if(validPhone) {
												validState = true;
												if(runningProb == 0) {
													runningProb = myViterbiLatice.get(i-1).get(y).getStateProb() + wordChangePenalty;
													previousMaxIndex = y;
												}
												else {
													if(myViterbiLatice.get(i-1).get(y).getStateProb() + wordChangePenalty > runningProb) {
														runningProb = myViterbiLatice.get(i-1).get(y).getStateProb() + wordChangePenalty;
														previousMaxIndex = y;
													}
												}
												currentHMMState = new HMMState(
														myLexicon.get(k).getEnglishName(),
														myLexicon.get(k).getPhones().get(x),
														currentRow.getState(), runningProb + currentRow.getLogProb());
												currentHMMState.setPreviousMaxState(previousMaxIndex);
											}
										}
									}
									if(validState) {
										myViterbiLatice.get(i).add(currentHMMState);
									}
								}
								else if(currentRow.getState() == 1) {
									validState = false;

									//Loop through the possible states associated with the 
									//observation of the previous column in the viterbi 
									//lattice
									for(int y=0; y<myViterbiLatice.get(i-1).size(); y++) {

										if(myViterbiLatice.get(i-1).get(y).getStateSubphone() == 1 &&
												myViterbiLatice.get(i-1).get(y).getStatePhone().equals(
														currentRow.getPhone())
														&& myViterbiLatice.get(i-1).get(y).getStateLexEntry().equals(
																myLexicon.get(k).getEnglishName())) {

											validState = true;
											
											if(runningProb == 0) {
												runningProb = myViterbiLatice.get(i-1).get(y).getStateProb();
												previousMaxIndex = y;
											}

											else {
												if(myViterbiLatice.get(i-1).get(y).getStateProb() > runningProb) {
													runningProb = myViterbiLatice.get(i-1).get(y).getStateProb();
													previousMaxIndex = y;
												}
											}
											currentHMMState = new HMMState(
													myLexicon.get(k).getEnglishName(),
													myLexicon.get(k).getPhones().get(x),
													currentRow.getState(), runningProb + currentRow.getLogProb());
											currentHMMState.setPreviousMaxState(previousMaxIndex);
										}
										else if(myViterbiLatice.get(i-1).get(y).getStateSubphone() == 0 &&
												myViterbiLatice.get(i-1).get(y).getStatePhone().equals(
														currentRow.getPhone()) &&
														myViterbiLatice.get(i-1).get(y).getStateLexEntry().equals(
																myLexicon.get(k).getEnglishName())) {

											validState = true;
											
											if(runningProb == 0) {
												runningProb = myViterbiLatice.get(i-1).get(y).getStateProb();
												previousMaxIndex = y;
											}
											else {
												if(myViterbiLatice.get(i-1).get(y).getStateProb() > runningProb) {
													runningProb = myViterbiLatice.get(i-1).get(y).getStateProb();
													previousMaxIndex = y;
												}
											}
											currentHMMState = new HMMState(
													myLexicon.get(k).getEnglishName(),
													myLexicon.get(k).getPhones().get(x),
													currentRow.getState(), runningProb + currentRow.getLogProb());
											currentHMMState.setPreviousMaxState(previousMaxIndex);
										}
									}
									if(validState) {
										myViterbiLatice.get(i).add(currentHMMState);
									}
								}
								else if(currentRow.getState() == 2) {
									validState = false;
									//Loop through the possible states associated with the 
									//observation of the previous column in the viterbi 
									//lattice
									for(int y=0; y<myViterbiLatice.get(i-1).size(); y++) {

										if(myViterbiLatice.get(i-1).get(y).getStateSubphone() == 2 &&
												myViterbiLatice.get(i-1).get(y).getStatePhone().equals(
														currentRow.getPhone())
														&& myViterbiLatice.get(i-1).get(y).getStateLexEntry().equals(
																myLexicon.get(k).getEnglishName())) {

											validState = true;
											
											if(runningProb == 0) {
												runningProb = myViterbiLatice.get(i-1).get(y).getStateProb();
												previousMaxIndex = y;
											}

											else {
												if(myViterbiLatice.get(i-1).get(y).getStateProb() > runningProb) {
													runningProb = myViterbiLatice.get(i-1).get(y).getStateProb();
													previousMaxIndex = y;
												}
											}
											currentHMMState = new HMMState(
													myLexicon.get(k).getEnglishName(),
													myLexicon.get(k).getPhones().get(x),
													currentRow.getState(), runningProb + currentRow.getLogProb());
											currentHMMState.setPreviousMaxState(previousMaxIndex);
											if(x == myLexicon.get(k).getPhones().size() - 1) {
												currentHMMState.setEndOfWord(true);
											}
										}
										else if(myViterbiLatice.get(i-1).get(y).getStateSubphone() == 1 &&
												myViterbiLatice.get(i-1).get(y).getStatePhone().equals(
														currentRow.getPhone()) &&
														myViterbiLatice.get(i-1).get(y).getStateLexEntry().equals(
																myLexicon.get(k).getEnglishName())) {

											validState = true;
											
											if(runningProb == 0) {
												runningProb = myViterbiLatice.get(i-1).get(y).getStateProb();
												previousMaxIndex = y;
											}
											else {
												if(myViterbiLatice.get(i-1).get(y).getStateProb() > runningProb) {
													runningProb = myViterbiLatice.get(i-1).get(y).getStateProb();
													previousMaxIndex = y;
												}
											}
											currentHMMState = new HMMState(
													myLexicon.get(k).getEnglishName(),
													myLexicon.get(k).getPhones().get(x),
													currentRow.getState(), runningProb + currentRow.getLogProb());
											currentHMMState.setPreviousMaxState(previousMaxIndex);
											if(x == myLexicon.get(k).getPhones().size() - 1) {
												currentHMMState.setEndOfWord(true);
											}
										}
									}	
									if(validState) {
										myViterbiLatice.get(i).add(currentHMMState);
									}
								}
							}
						}
					}
				}
			}
		}		
		TraverseLatice(myViterbiLatice);


	}

	private static void TraverseLatice(List<List<HMMState>> myViterbiLatice) {
		double runningMaxProb = 0;
		double runningTotalProb = 0;
		int runningMaxIndex = 0;
		int previousMaxIndex = 0;
		int currentBestIndex = 0;
		List<String> runningWordsList = new ArrayList<String>();
		for(int i=0;i<myViterbiLatice.get(myViterbiLatice.size()-1).size(); i++) {
			if(runningMaxProb == 0) {
				runningMaxProb = myViterbiLatice.get(myViterbiLatice.size()-1).get(i).getStateProb();
				runningMaxIndex = i;
			}
			else if(myViterbiLatice.get(myViterbiLatice.size()-1).get(i).getStateProb() > runningMaxProb) {
				runningMaxProb = myViterbiLatice.get(myViterbiLatice.size()-1).get(i).getStateProb();
				runningMaxIndex = i;
			}
		}
		previousMaxIndex = runningMaxIndex;
		runningTotalProb += runningMaxProb;
		for(int i=(myViterbiLatice.size()-2); i>=0; i--) {
			currentBestIndex = myViterbiLatice.get(i+1).get(previousMaxIndex).getPreviousMaxState();
			if(!myViterbiLatice.get(i+1).get(previousMaxIndex).getStatePhone().equals(
					myViterbiLatice.get(i).get(currentBestIndex).getStatePhone()) && 
						myViterbiLatice.get(i).get(currentBestIndex).getEndOfWord() &&
							!myViterbiLatice.get(i+1).get(previousMaxIndex).getStateLexEntry().equals("silence")) {
				runningWordsList.add(myViterbiLatice.get(i+1).get(previousMaxIndex).getStateLexEntry());
			}
			runningTotalProb += myViterbiLatice.get(i).get(currentBestIndex).getStateProb();
			previousMaxIndex = currentBestIndex;
		}
		if(!myViterbiLatice.get(0).get(currentBestIndex).getStateLexEntry().equals("silence")) {
			runningWordsList.add(myViterbiLatice.get(0).get(currentBestIndex).getStateLexEntry());
		}
		System.out.print("The numbers are:");
		for(int i=runningWordsList.size()-1; i>=0; i--) {
			System.out.print(" " + runningWordsList.get(i));
		}
		System.out.print("\nThe probability of this sequence is:");
		System.out.print(" " + runningTotalProb + "\n");
	}

}
