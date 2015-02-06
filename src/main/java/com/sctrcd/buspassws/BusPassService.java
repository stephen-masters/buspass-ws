package com.sctrcd.buspassws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sctrcd.buspassws.facts.BusPass;
import com.sctrcd.buspassws.facts.Person;

@Service
public class BusPassService {

    private static Logger log = LoggerFactory.getLogger(BusPassService.class);

    private final KieContainer kieContainer;

    @Autowired
    public BusPassService(KieContainer kieContainer) {
        log.info("Initialising a new bus pass session.");
        this.kieContainer = kieContainer;
    }

    /**
     * Create a new session, insert a person's details and fire rules to
     * determine what kind of bus pass is to be issued.
     */
    public BusPass getBusPass(Person person) {
        KieSession kieSession = kieContainer.newKieSession("BusPassSession");
        kieSession.insert(person);
        kieSession.fireAllRules();
        BusPass busPass = findBusPass(kieSession);
        kieSession.dispose();
        return busPass;
    }
    
    /**
     * Search the {@link KieSession} for bus passes.
     */
    private BusPass findBusPass(KieSession kieSession) {
        
        // Find all BusPass facts and 1st generation child classes of BusPass.
        ObjectFilter busPassFilter = new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                if (BusPass.class.equals(object.getClass())) return true;
                if (BusPass.class.equals(object.getClass().getSuperclass())) return true;
                return false;
            }
        };

        // printFactsMessage(kieSession);
        
        List<BusPass> facts = new ArrayList<BusPass>();
        for (FactHandle handle : kieSession.getFactHandles(busPassFilter)) {
            facts.add((BusPass) kieSession.getObject(handle));
        }
        if (facts.size() == 0) {
            return null;
        }
        // Assumes that the rules will always be generating a single bus pass. 
        return facts.get(0);
    }
    
    /**
     * Print out details of all facts in working memory.
     * Handy for debugging.
     */
    @SuppressWarnings("unused")
    private void printFactsMessage(KieSession kieSession) {
        Collection<FactHandle> allHandles = kieSession.getFactHandles();
        
        String msg = "\nAll facts:\n";
        for (FactHandle handle : allHandles) {
            msg += "    " + kieSession.getObject(handle) + "\n";
        }
        System.out.println(msg);
    }

}
