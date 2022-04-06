package com.zhevakin.TelegramBotDemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Tasks")
@Getter
@Setter
@AllArgsConstructor
public class Tasks {

    public Tasks() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "done")
    boolean done = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    Users users;

    @Column(name = "dateDone")
    Date dateDone;

    @Column(name = "text")
    String text;

    @Column(name = "topic")
    String topic;

}
