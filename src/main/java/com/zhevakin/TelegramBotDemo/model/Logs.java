package com.zhevakin.TelegramBotDemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "Logs")
public class Logs {

    public Logs() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private Users users;

    @Column(name = "action")
    private String action;

}
