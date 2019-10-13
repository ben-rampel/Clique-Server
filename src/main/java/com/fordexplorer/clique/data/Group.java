package com.fordexplorer.clique.data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "PersonGroup")
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    private String name;

    @Basic
    private String description;

    @OneToMany(mappedBy = "currentGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @MapKey(name = "id")
    private Map<Long, Person> members;

    @ElementCollection
    @CollectionTable(name = "PendingMember", joinColumns = @JoinColumn(name = "GroupId"))
    @Column(name = "PersonId")
    private Set<Long> pendingMembers;

    @Basic
    private int memberTurnover;

    @Embedded
    private Location location;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "GroupId")
    private List<Message> groupMessages;

    public List<Message> getGroupMessages() {
        return groupMessages;
    }

    public Group(){
        members = new HashMap<>();
        pendingMembers = new HashSet<>();
        groupMessages = new LinkedList<>();
    }

    public List<Person> getMembers() {
        List<Person> result = new ArrayList<>();
        for (Map.Entry<Long, Person> e : this.members.entrySet()) {
            if (!this.pendingMembers.contains(e.getKey())) {
                result.add(e.getValue());
            }
        }
        return result;
    }

    public void addMessage(Message m){
        groupMessages.add(m);
    }

    public void setMembers(List<Person> members) {
        Map<Long, Person> pendingMemberMap = new HashMap<>();
        for (Long id : this.pendingMembers) {
            if (this.members.containsKey(id)) {
                pendingMemberMap.put(id, this.members.get(id));
            }
        }
        int newSize = this.members.size();
        this.memberTurnover += newSize - this.memberTurnover;

        this.members.clear();
        for (Person p : members) {
            this.members.put(p.getId(), p);
        }
        this.members.putAll(pendingMemberMap);
    }

    public void addMember(Person person){
        members.put(person.getId(), person);
        this.pendingMembers.remove(person.getId());
        this.memberTurnover += 1;
    }

    public void removeMember(Person person){
        for(Map.Entry<Long, Person> entry : members.entrySet()){
            if(entry.getValue().equals(person)){
                members.remove(entry.getKey());
                this.memberTurnover -= 1;
                break;
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMemberTurnover() {
        return memberTurnover;
    }

    public void setMemberTurnover(int memberTurnover) {
        this.memberTurnover = memberTurnover;
    }

    public List<Person> getWannabeMembers() {
        List<Person> wannabeMembers = new ArrayList<>();
        for (Long id : pendingMembers) {
            if (members.containsKey(id)) {
                wannabeMembers.add(members.get(id));
            }
        }
        return wannabeMembers;
    }

    public List<String> getInterests(){
        Map<String, Integer> interests = new LinkedHashMap<>();
        for(Person p : getMembers()){
            for(String s : p.getInterests()) {
                if (interests.containsKey(s)) {
                    interests.put(s, interests.get(s) + 1);
                } else {
                    interests.put(s, 1);
                }
            }
        }
        Stack<String> stack = new Stack<>();
        interests.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEachOrdered(entry -> stack.push(entry.getKey()));

        List<String> result = new LinkedList<>();
        for(int i = 0; i < 5; i++){
            result.add(stack.pop());
        }
        return result;
    }

    public void setWannabeMembers(List<Person> wannabeMembers) {
        for (Person p : wannabeMembers) {
            this.members.put(p.getId(), p);
            this.pendingMembers.add(p.getId());
        }
    }

    public void addWannabeMember(Person person) {
        this.members.put(person.getId(), person);
        this.pendingMembers.add(person.getId());
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

}
