package com.zhevakin.TelegramBotDemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Users")
public class Users {

    public Users() {
    }

    @Id
    private Long id;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "user_name")
    private String userName;

    @ManyToMany (cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "Users_Modules",
    joinColumns = {@JoinColumn(name = "users_id")},
    inverseJoinColumns = {@JoinColumn(name = "modules_id")}
    )
    Set<Modules> modules;

    @OneToMany(mappedBy = "users", fetch = FetchType.LAZY)
    private Set<Logs> logs;

}
