/*
 * Copyright (c) 2017 Otávio Santana and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 * You may elect to redistribute this code under either of these licenses.
 *
 * Contributors:
 *
 * Otavio Santana
 * Werner Keil
 */
package org.jnosql.demo.endgame.jakarta.neo4j;

import org.eclipse.jnosql.artemis.graph.GraphTemplate;
import org.jnosql.demo.endgame.jakarta.neo4j.model.City;
import org.jnosql.demo.endgame.jakarta.neo4j.model.Traveler;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public final class TravelApp {

    private TravelApp() {
    }

    private static final String GOAL = "type";
    private static final String FUN = "fun";
    private static final String TRAVELS = "travels";
    private static final String WORK = "Work";

    public static void main(String[] args) {

        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            GraphTemplate template = container.select(GraphTemplate.class).get();

            Traveler stark = template.insert(Traveler.of("Stark"));
            Traveler rogers = template.insert(Traveler.of("Rogers"));
            Traveler romanoff = template.insert(Traveler.of("Romanoff"));
            Traveler banner = template.insert(Traveler.of("Banner"));

            City sanFrancisco = template.insert(City.of("San Francisco"));
            City moscow = template.insert(City.of("Moscow"));
            City newYork = template.insert(City.of("New York"));
            City saoPaulo = template.insert(City.of("São Paulo"));
            City casaBlanca = template.insert(City.of("Casa Blanca"));

            template.edge(stark, TRAVELS, sanFrancisco).add(GOAL, FUN);
            template.edge(stark, TRAVELS, moscow).add(GOAL, FUN);
            template.edge(stark, TRAVELS, newYork).add(GOAL, FUN);
            template.edge(stark, TRAVELS, saoPaulo).add(GOAL, FUN);
            template.edge(stark, TRAVELS, casaBlanca).add(GOAL, FUN);

            template.edge(rogers, TRAVELS, newYork).add(GOAL, WORK);

            template.edge(banner, TRAVELS, casaBlanca).add(GOAL, WORK);
            template.edge(banner, TRAVELS, saoPaulo).add(GOAL, WORK);

            template.edge(romanoff, TRAVELS, moscow).add(GOAL, WORK);
            template.edge(romanoff, TRAVELS, newYork).add(GOAL, WORK);
            template.edge(romanoff, TRAVELS, saoPaulo).add(GOAL, WORK);
            template.edge(romanoff, TRAVELS, casaBlanca).add(GOAL, FUN);

            template.edge(stark, "knows", romanoff);
            template.edge(stark, "knows", rogers);
            template.edge(rogers, "knows", romanoff);


            Map<String, Long> mostFunCity = template.getTraversalVertex()
                    .inE(TRAVELS)
                    .has(GOAL, FUN).inV()
                    .<City>getResult()
                    .map(City::getName)
                    .collect((groupingBy(Function.identity(), counting())));

            Map<String, Long> mostBusiness = template.getTraversalVertex()
                    .inE(TRAVELS)
                    .has(GOAL, WORK).inV()
                    .<City>getResult()
                    .map(City::getName)
                    .collect((groupingBy(Function.identity(), counting())));

            Map<String, Long> mostTravelCity = template.getTraversalVertex()
                    .out(TRAVELS)
                    .<City>getResult()
                    .map(City::getName)
                    .collect((groupingBy(Function.identity(), counting())));


            Map<String, Long> personTravelFun = template.getTraversalVertex()
                    .inE(TRAVELS)
                    .has(GOAL, FUN).outV()
                    .<Traveler>getResult()
                    .map(Traveler::getName)
                    .collect((groupingBy(Function.identity(), counting())));

            Map<String, Long> personTravelWork = template.getTraversalVertex()
                    .inE(TRAVELS)
                    .has(GOAL, WORK).outV()
                    .<Traveler>getResult()
                    .map(Traveler::getName)
                    .collect((groupingBy(Function.identity(), counting())));

            Map<String, Long> personTravel = template.getTraversalVertex()
                    .in(TRAVELS)
                    .<Traveler>getResult()
                    .map(Traveler::getName)
                    .collect((groupingBy(Function.identity(), counting())));

            List<String> friendsCasaBlanca = template.getTraversalVertex()
                    .hasLabel("City")
                    .has("name", "Casa Blanca")
                    .in(TRAVELS).<Traveler>getResult().map(Traveler::getName).collect(toList());

            System.out.println("The city most fun: "+ mostFunCity);
            System.out.println("The city most business: "+ mostBusiness);
            System.out.println("The city with more travel: "+ mostTravelCity);

            System.out.println("The person who traveled fun: "+ personTravelFun);
            System.out.println("The person who traveled business: "+ personTravelWork);
            System.out.println("The person who traveled: "+ personTravel);

            System.out.println("Friends because went to Casa Blanca: " + casaBlanca);
        }
    }
}
