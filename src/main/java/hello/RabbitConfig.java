package hello;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

@Configuration
@Profile("Rabbit")
public class RabbitConfig extends BatchConfiguration {

	FlatFileItemReader<Rabbit> reader() {
		FlatFileItemReader<Rabbit> reader = new FlatFileItemReader<Rabbit>();
		reader.setResource(new ClassPathResource("rabbit-data.csv"));
		reader.setLineMapper(new DefaultLineMapper<Rabbit>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "firstName", "lastName" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<Rabbit>() {
					{
						setTargetType(Rabbit.class);
					}
				});
			}
		});
		return reader;
	}

	RabbitItemProcessor processor() {
		return new RabbitItemProcessor();
	}

	JdbcBatchItemWriter<Rabbit> writer() {
		JdbcBatchItemWriter<Rabbit> writer = new JdbcBatchItemWriter<Rabbit>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Rabbit>());
		writer.setSql("INSERT INTO rabbit (first_name, last_name) VALUES (:firstName, :lastName)");
		writer.setDataSource(dataSource);
		return writer;
	}

	Step step1() {
		return stepBuilderFactory.get("step1").<Rabbit, Rabbit>chunk(10).reader(reader()).processor(processor())
				.writer(writer()).build();
	}
}
