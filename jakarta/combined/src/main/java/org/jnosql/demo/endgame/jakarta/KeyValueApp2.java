package org.jnosql.demo.endgame.jakarta;

import jakarta.nosql.mapping.keyvalue.KeyValueTemplate;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class KeyValueApp2 {

    public static void main(String[] args) throws InterruptedException {

        try (SeContainer container = SeContainerInitializer
                .newInstance().initialize()) {

            God hunter = new God(1L, "Ullr", "Hunting");

            KeyValueTemplate template =
                    container.select(KeyValueTemplate.class)
                            .get();

            template.put(hunter, Duration.ofSeconds(1L));

            final Optional<God> god = template.get(1L, God.class);
            System.out.println("query : " + god);
            TimeUnit.SECONDS.sleep(2L);
            System.out.println("query again: " +
                    template.get(1L, God.class));

        }

        System.exit(0);

    }
}
