package com.example.demo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.example.demo.KafkaConfigs.TOPIC_NAME;

@Service
public class JokeService {

    private final Producer<String, String> producer;
    private final KafkaConsumer<String, String> consumer;

    public JokeService(KafkaConfigs configs) {
        producer = new KafkaProducer<>(configs.getProducerConfig());
        consumer = new KafkaConsumer<>(configs.getConsumerConfig());
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
    }


    public List<Joke> getAllJokes() {

        final int giveUp = 3;
        int retryCount = 0;

        ArrayList<Joke> jokes = new ArrayList<>();
        try {
            /*
              It is not guaranteed that results will be received at the first consumer.poll() call.
              Therefore, we need to try few times to make sure the consumer really get results.
             */
            while (true) {
                TopicPartition partition = new TopicPartition(TOPIC_NAME, 0);
                consumer.assign(Collections.singleton(partition));
                consumer.seekToBeginning(Collections.singleton(partition));
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                if (records.count() == 0) {
                    retryCount++;
                    if (giveUp >= retryCount) {
                        continue;
                    } else {
                        break;
                    }
                }
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                    String joke = record.value();
                    Joke realJoke = new Joke(LocalDateTime.now().toString(), joke);
                    jokes.add(realJoke);
                }
                consumer.commitAsync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jokes;
    }

    public void submitJoke(String joke) {
        producer.send(new ProducerRecord<>(TOPIC_NAME, UUID.randomUUID().toString(), joke));
        System.out.println("message sent to kafka");
    }

}
