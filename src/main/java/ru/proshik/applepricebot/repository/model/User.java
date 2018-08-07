package ru.proshik.applepricebot.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_gen")
    @SequenceGenerator(name = "users_seq_gen", sequenceName = "users_seq")
    private Long id;

    @Column(name = "created_date", updatable = false, insertable = false,
            columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private ZonedDateTime createdDate;

    @Column(name = "chat_id")
    private String chatId;

    @OneToMany(mappedBy = "user")
    private List<Subscription> subscriptions;

}
