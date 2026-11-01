package com.sctrcd.buspassws;

import com.sctrcd.buspassws.facts.BusPass;
import com.sctrcd.buspassws.facts.Person;
import java.util.ArrayList;
import java.util.List;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BusPassService {

    private static final Logger log = LoggerFactory.getLogger(BusPassService.class);

    private final KieContainer kieContainer;

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
        try {
            kieSession.insert(person);
            kieSession.fireAllRules();
            return findBusPass(kieSession);
        } finally {
            kieSession.dispose();
        }
    }

    /**
     * Search the {@link KieSession} for bus passes.
     */
    private BusPass findBusPass(KieSession kieSession) {

        // Find all BusPass facts and 1st generation child classes of BusPass.
        ObjectFilter busPassFilter = object -> BusPass.class.equals(object.getClass())
                || BusPass.class.equals(object.getClass().getSuperclass());

        // printFactsMessage(kieSession);

        List<BusPass> facts = new ArrayList<>();
        for (FactHandle handle : kieSession.getFactHandles(busPassFilter)) {
            facts.add((BusPass) kieSession.getObject(handle));
        }
        if (facts.isEmpty()) {
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
        StringBuilder msg = new StringBuilder("\nAll facts:\n");
        for (FactHandle handle : kieSession.getFactHandles()) {
            msg.append("    ").append(kieSession.getObject(handle)).append('\n');
        }
        log.debug(msg.toString());
    }
}
