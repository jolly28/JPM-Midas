package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.jpmc.midascore.entity.UserRecord;


@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float amount;
    private Float incentive;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;

    // Getters and setters

    public void setSender(UserRecord sender){
        this.sender=sender;
    }
    public void setRecipient(UserRecord recipient){
        this.recipient=recipient;
    }
    public void setAmount(Float amount){
        this.amount=amount;
    }
    public void setIncentive(Float incentive){
        this.incentive=incentive;
    }
    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp=timestamp;
    }

}
