package com.fordexplorer.clique;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private List<Person> members;
    private List<Person> wannabeMembers;
    private int memberTurnover;

    public Group(){
        members = new ArrayList<>();
        wannabeMembers = new ArrayList<>();
    }

    public List<Person> getMembers() {
        return members;
    }

    public void setMembers(List<Person> members) {
        this.members = members;
    }

    public void addMember(Person person){
        members.add(person);
    }

    public int getMemberTurnover() {
        return memberTurnover;
    }

    public void setMemberTurnover(int memberTurnover) {
        this.memberTurnover = memberTurnover;
    }

    public List<Person> getWannabeMembers() {
        return wannabeMembers;
    }

    public void addWannabeMember(Person person){
        wannabeMembers.add(person);
    }

    public void setWannabeMembers(List<Person> wannabeMembers) {
        this.wannabeMembers = wannabeMembers;
    }
}
