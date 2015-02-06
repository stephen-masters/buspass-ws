package com.sctrcd.buspassws.facts;

public class ChildBusPass extends BusPass {

    public ChildBusPass(Person person) {
        super(person);
    }

    @Override
    public String toString() {
        return "ChildBusPass: { person=" + getPerson() + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ChildBusPass that = (ChildBusPass) o;
        return getPerson().equals(that.getPerson());
    }

    @Override
    public int hashCode() {
        return getPerson().hashCode();
    }
    
}