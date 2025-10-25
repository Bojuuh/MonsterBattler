package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "monster")
public class Monster implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monster_id", nullable = false, unique = true)
    private Integer id;

    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "level", nullable = false)
    private Integer level;

    @Setter
    @Column(name = "hp", nullable = false)
    private Integer hp;

    @Setter
    @Column(name = "attack", nullable = false)
    private Integer attack;

    @Setter
    @Column(name = "defense", nullable = false)
    private Integer defense;

    @Setter
    @Column(name = "area")
    private String area;

    @Setter
    @Column(name = "spawn_weight")
    private Integer spawnWeight;

    public Monster(String name, Integer level, Integer hp, Integer attack, Integer defense, String area, Integer spawnWeight) {
        this.name = name;
        this.level = level;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.area = area;
        this.spawnWeight = spawnWeight;
    }
}

