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

}
