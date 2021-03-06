package com.cmu.bean;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.cmu.constants.Constants.RoomStatus;

//we might have to provide a method to start another round, which means clear results
public class RoomBean {
	private String roomID;
	private int roomSize;
	private RoomStatus status;
	private PersonBean leader;
	private String question;
	private ArrayList<PersonBean> people;
	private ArrayList<Option> options;
	private long startVoteTime;

	public RoomBean(String roomID, int roomSize) {
		super();
		this.setRoomID(roomID);
		this.status = RoomStatus.NEW_ROOM;
		this.roomSize = roomSize;
		this.people = new ArrayList<PersonBean>();
		this.options = new ArrayList<Option>();
	}

	public void addPeople(PersonBean p) throws IllegalStateException {
		if (people.size() > roomSize) {
			throw new IllegalStateException("The room is full");
		}
		if (!this.people.contains(p)) {
			people.add(p);
		}
	}

	public ArrayList<PersonBean> getPeople() {
		return people;
	}

	public int getRoomSize() {
		return roomSize;
	}

	public void setRoomSize() {
		roomSize = people.size();
	}

	public RoomStatus getStatus() {
		if (this.status == RoomStatus.QUESTION_SUBMITTED) {
			long currentVoteTime = System.currentTimeMillis() / 1000;
			if (currentVoteTime - this.startVoteTime > 120) {
				this.status = RoomStatus.VOTED;
			}
		}
		return status;
	}

	public PersonBean getLeader() {
		return leader;
	}

	public void setStatus(RoomStatus i) {
		this.status = i;
	}

	public void setLeader(PersonBean leader) {
		this.leader = leader;
	}

	public String getQuestion() {
		System.out.println("GetQuestion: " + question);
		return question;
	}

	public void setQuestion(String q) {
		this.question = q;
		JSONObject jsObject = (JSONObject) JSONValue.parse(q);
		int optionNum = jsObject.size() - 1;
		for (int i = 0; i < optionNum; i++) {
			String option = "option" + i;
			options.add(new Option((String) jsObject.get(option)));
		}
		this.startVoteTime = System.currentTimeMillis() / 1000;
	}

	public void voteForOption(int option) {
		System.out.println("voteForOption: " + option);
		options.get(option).votedNum++;
		if (getFinished() >= people.size()
				|| getMajority() > (people.size() / 2)) {
			status = RoomStatus.VOTED;
		}
	}

	public ArrayList<Option> getOptions() {
		return options;
	}

	public ArrayList<String> getFinalResult() {
		ArrayList<String> finalResult = new ArrayList<String>();
		int majority = getMajority();
		for (int i = 0; i < options.size(); i++) {
			if (options.get(i).getVotedNum() == majority) {
				finalResult.add(options.get(i).option);
			}
		}
		return finalResult;
	}

	public int getMajority() {
		int max = 0;
		for (int i = 0; i < options.size(); i++) {
			max = Math.max(max, options.get(i).getVotedNum());
		}
		return max;
	}

	public int getFinished() {
		int total = 0;
		for (int i = 0; i < options.size(); i++) {
			total += options.get(i).getVotedNum();
		}
		return total;
	}

	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}

	public boolean startNewRound(PersonBean person) {
		if (this.status != RoomStatus.NEW_ROOM) {
			this.status = RoomStatus.NEW_ROOM;
			this.leader = null;
			this.question = null;
			this.options.clear();
			this.people.clear();
			this.people.add(person);
			this.startVoteTime = System.currentTimeMillis() / 1000;
		} else {
			this.people.add(person);
		}
		return true;
	}

	public boolean exitRoom(PersonBean person) {
		this.people.remove(person);
		return true;
	}

	public long getStartVoteTime() {
		return startVoteTime;
	}

	public void setStartVoteTime(long startVoteTime) {
		this.startVoteTime = startVoteTime;
	}

	public class Option {
		String option;
		int votedNum;

		public Option(String option) {
			this.option = option;
			this.votedNum = 0;
		}

		public int getVotedNum() {
			return votedNum;
		}
	}
}
