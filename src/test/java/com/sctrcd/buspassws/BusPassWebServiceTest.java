package com.sctrcd.buspassws;

import static org.assertj.core.api.Assertions.assertThat;

import com.sctrcd.buspassws.facts.AdultBusPass;
import com.sctrcd.buspassws.facts.BusPass;
import com.sctrcd.buspassws.facts.ChildBusPass;
import com.sctrcd.buspassws.facts.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The {@link BusPassService} wired by Spring, with the {@code KieContainer}
 * bean from {@link BusPassApp}.
 */
@SpringBootTest
class BusPassWebServiceTest {

    @Autowired
    private BusPassService busPassService;

    @Test
    void shouldIssueAdultBusPass() {
        Person person = new Person("Steve", 16);
        BusPass busPass = busPassService.getBusPass(person);

        assertThat(busPass).isExactlyInstanceOf(AdultBusPass.class);
        assertThat(busPass.getPerson()).isEqualTo(person);
    }

    @Test
    void shouldIssueChildBusPass() {
        Person person = new Person("Steve", 15);
        BusPass busPass = busPassService.getBusPass(person);

        assertThat(busPass).isExactlyInstanceOf(ChildBusPass.class);
        assertThat(busPass.getPerson()).isEqualTo(person);
    }
}
