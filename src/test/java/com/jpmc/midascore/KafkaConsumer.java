package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Service
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    public void init() {
        logger.info("KafkaConsumer initialized");
    }
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    public KafkaConsumer(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate=restTemplate;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "transaction-consumer-group")
    public void consume(Transaction transaction) {
        logger.info("Received Transaction: {}", transaction);

        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender!=null && recipient!=null) {

            if (sender.getBalance() >= transaction.getAmount()) {
                sender.setBalance(sender.getBalance() - transaction.getAmount());
                Incentive incentive = restTemplate.postForObject("http://localhost:8080/incentive", transaction, Incentive.class);
                float incentiveA=0;
                recipient.setBalance(recipient.getBalance() + transaction.getAmount()+incentiveA);

                
                TransactionRecord record = new TransactionRecord();
                record.setSender(sender);
                record.setRecipient(recipient);
                record.setAmount(transaction.getAmount());
                record.setIncentive(incentiveA);
                record.setTimestamp(LocalDateTime.now());

                transactionRecordRepository.save(record);
                userRepository.save(sender);
                userRepository.save(recipient);
                logger.info("Trans Detail Sender:{}, Reciever:{}",sender.toString(), recipient.toString());
                logger.info("Transaction recorded and balances updated.");
                return;
            }
        }

        logger.warn("Invalid transaction. Sender or recipient missing, or insufficient funds.");
    }
}