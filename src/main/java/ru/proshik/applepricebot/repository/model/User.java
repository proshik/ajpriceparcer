package ru.proshik.applepricebot.repository.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@GenericGenerator(name = "users_id_generator", strategy = "sequence", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence", value = "users_id_seq")})
public class User {

    @Id
    @GeneratedValue(generator = "users_id_generator")
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
