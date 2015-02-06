package com.sctrcd.buspassws.facts;

public class BusPass {

    private Person person;

    public BusPass(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getBusPassType() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public String toString() {
        return "BusPass: { person=" + person + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        BusPass that = (BusPass) o;
        return this.person.equals(that.person);
    }

    @Override
    public int hashCode() {
        return person.hashCode();
    }
}
