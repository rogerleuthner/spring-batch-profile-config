package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class RabbitItemProcessor implements ItemProcessor<Rabbit, Rabbit> {

    private static final Logger log = LoggerFactory.getLogger(RabbitItemProcessor.class);

    @Override
    public Rabbit process(final Rabbit person) throws Exception {
        final String firstName = person.getFirstName().toUpperCase();
        final String lastName = person.getLastName().toUpperCase();

        final Rabbit transformedRabbit = new Rabbit(firstName, lastName);

        log.info("Converting (" + person + ") into (" + transformedRabbit + ")");

        return transformedRabbit;
    }

}
