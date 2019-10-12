package com.fordexplorer.clique.data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "Group")
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "currentGroup", cascade = CascadeType.ALL)
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

        this.members.clear();
        for (Person p : members) {
            this.members.put(p.getId(), p);
        }
        this.members.putAll(pendingMemberMap);
    }

    public void addMember(Person person){
        members.put(person.getId(), person);
        this.pendingMembers.remove(person.getId());
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
