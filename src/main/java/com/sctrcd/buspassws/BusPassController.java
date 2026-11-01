package com.sctrcd.buspassws;

import com.sctrcd.buspassws.facts.BusPass;
import com.sctrcd.buspassws.facts.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BusPassController {

    private static final Logger log = LoggerFactory.getLogger(BusPassController.class);

    private final BusPassService busPassService;

    public BusPassController(BusPassService busPassService) {
        this.busPassService = busPassService;
    }

    @GetMapping(value = "/buspass", produces = MediaType.APPLICATION_JSON_VALUE)
    public BusPass getBusPass(@RequestParam String name, @RequestParam int age) {

        Person person = new Person(name, age);

        log.debug("Bus pass request received for: {}", person);

        return busPassService.getBusPass(person);
    }
}
