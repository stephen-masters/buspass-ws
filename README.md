buspass-ws
===

An experiment in creating a minimal Drools web service using Spring Boot.

A little while back, I knocked up Qzr (https://github.com/gratiartis/qzr) to demonstrate using Spring Boot (http://projects.spring.io/spring-boot/) with the Drools rules engine (http://www.drools.org/). However, I also wanted to play around with a few more technologies (AngularJS and Spring HATEOAS), so it's a bit large for just demonstrating exposing Drools rules as an HTTP web service.

A few folks have mentioned that there was a bit too much going on for a beginner to pick out the essentials of running Drools in a Spring Boot application. So I thought I'd have a go at creating a simpler application, which does nothing more than that.

For the rules, I took my cues from the Bus Pass example in the JBPM project:

https://github.com/droolsjbpm/drools/tree/master/drools-examples/src/main/java/org/drools/examples/buspass

I have cut the rules down a little bit and reduced the code by replacing some of the Java fact classes with DRL declared types. I prefer this for facts which are only referenced from within the DRL.

Assuming that you have a reasonably recent install of Maven and the JDK (I have tested with 8, but I think 7 should be okay), you should be able to do the following from the command line.

Build the application:

    mvn clean package

Run the application:

    java -jar target/buspass-ws-1.0.0-SNAPSHOT.jar

Then send a request to the API using curl or your favourite web browser. As described by the rules, if you request a bus pass for a person with age less than 16, you should see a ChildBusPass and for someone 16 or over, you should see an AdultBusPass.

For example, opening http://127.0.0.1:8080/buspass?name=Steve&age=15 gives me:
    
    {"person":{"name":"Steve","age":15},"busPassType":"ChildBusPass"}
    
... and opening http://127.0.0.1:8080/buspass?name=Steve&age=16 gives me:
    
    {"person":{"name":"Steve","age":16},"busPassType":"AdultBusPass"}

