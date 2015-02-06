package com.sctrcd.buspassws.facts;

public class AdultBusPass extends BusPass {

    public AdultBusPass(Person person) {
        super(person);
    }
    
    @Override
    public String toString() {
        return "AdultBusPass: { person=" + getPerson() + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        AdultBusPass that = (AdultBusPass) o;
        return getPerson().equals(that.getPerson());
    }

    @Override
    public int hashCode() {
        return getPerson().hashCode();
    }

}
