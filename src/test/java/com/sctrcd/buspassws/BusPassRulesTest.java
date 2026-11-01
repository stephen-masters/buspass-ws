package com.sctrcd.buspassws;

import static org.assertj.core.api.Assertions.assertThat;

import com.sctrcd.buspassws.facts.AdultBusPass;
import com.sctrcd.buspassws.facts.BusPass;
import com.sctrcd.buspassws.facts.ChildBusPass;
import com.sctrcd.buspassws.facts.Person;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

/**
 * Runs the rules in a plain {@link KieSession}, with no Spring involved.
 */
class BusPassRulesTest {

    private KieSession kieSession;

    @BeforeEach
    void initialise() {
        KieContainer kieContainer = KieServices.Factory.get().getKieClasspathContainer();
        this.kieSession = kieContainer.newKieSession("BusPassSession");
    }

    @AfterEach
    void dispose() {
        kieSession.dispose();
    }

    /**
     * If this passes, then kmodule.xml was found and the session it declares
     * could be created.
     */
    @Test
    void shouldConfigureDroolsComponents() {
        assertThat(kieSession).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({"0, ChildBusPass", "15, ChildBusPass", "16, AdultBusPass", "70, AdultBusPass"})
    void shouldIssueOneBusPassByAge(int age, String expectedType) {
        Person person = new Person("Steve", age);
        kieSession.insert(person);
        kieSession.fireAllRules();

        List<BusPass> busPasses = busPasses();
        assertThat(busPasses).hasSize(1);
        assertThat(busPasses.get(0).getBusPassType()).isEqualTo(expectedType);
        assertThat(busPasses.get(0).getPerson()).isEqualTo(person);
    }

    @Test
    void shouldIssueOneBusPassPerPerson() {
        Person child = new Person("Ani", 12);
        Person adult = new Person("Steve", 40);
        kieSession.insert(child);
        kieSession.insert(adult);
        kieSession.fireAllRules();

        assertThat(busPasses()).containsExactlyInAnyOrder(new ChildBusPass(child), new AdultBusPass(adult));
    }

    /**
     * The passes are inserted logically, so when a person turns 16 the child
     * pass is withdrawn by truth maintenance and an adult pass takes its place.
     */
    @Test
    void shouldReplaceChildBusPassWhenPersonComesOfAge() {
        Person person = new Person("Steve", 15);
        FactHandle handle = kieSession.insert(person);
        kieSession.fireAllRules();
        assertThat(busPasses()).containsExactly(new ChildBusPass(person));

        person.setAge(16);
        kieSession.update(handle, person);
        kieSession.fireAllRules();
        assertThat(busPasses()).containsExactly(new AdultBusPass(person));
    }

    @Test
    void shouldWithdrawBusPassWhenPersonIsRemoved() {
        FactHandle handle = kieSession.insert(new Person("Steve", 15));
        kieSession.fireAllRules();
        assertThat(busPasses()).hasSize(1);

        kieSession.delete(handle);
        kieSession.fireAllRules();
        assertThat(busPasses()).isEmpty();
    }

    private List<BusPass> busPasses() {
        return kieSession.getObjects(BusPass.class::isInstance).stream()
                .map(BusPass.class::cast)
                .toList();
    }
}
