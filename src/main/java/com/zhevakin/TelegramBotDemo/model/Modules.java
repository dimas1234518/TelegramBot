package com.zhevakin.TelegramBotDemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Modules")
public class Modules {

    public Modules() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Name")
    private String Name;

    @ManyToMany(mappedBy = "modules", fetch = FetchType.LAZY)
    Set<Users> users = new HashSet<>();

    @Override
    public String toString() {
        return getName();
    }
}
