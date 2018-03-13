package ru.proshik.applepricebot.repository.model;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq")
    private Long id;

    @Column(name = "created_date", updatable = false, insertable = false,
            columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private ZonedDateTime createdDate;

    @Column(name = "chat_id")
    private String chatId;

    @OneToMany(mappedBy = "user")
    private List<Subscription> subscriptions;

    public User() {
    }

    public User(String chatId, List<Subscription> subscriptions) {
        this.chatId = chatId;
        this.subscriptions = subscriptions;
    }

    public Long getId() {
        return id;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public String getChatId() {
        return chatId;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }
}
