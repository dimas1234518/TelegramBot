package com.zhevakin.TelegramBotDemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "weather_module")
public class WeatherModule {

    public WeatherModule() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private Users users;
    
    @Column(name = "everyday")
    private boolean everyday;

    @Column(name = "time")
    private Date time;

    @Column(name = "done")
    private boolean done;
    
}
