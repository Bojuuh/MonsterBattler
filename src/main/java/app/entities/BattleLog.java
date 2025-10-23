package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "battle_log")
public class BattleLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false, unique = true)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "battle_id", nullable = false)
    @Setter
    private Battle battle;

    @Setter
    @Column(name = "turn_number")
    private Integer turnNumber;

    @Setter
    @Column(name = "attacker")
    private String attacker;

    @Setter
    @Column(name = "defender")
    private String defender;

    @Setter
    @Column(name = "damage")
    private Integer damage;

    @Setter
    @Column(name = "defender_hp_after")
    private Integer defenderHpAfter;

    public BattleLog(Integer turnNumber, String attacker, String defender, Integer damage, Integer defenderHpAfter) {
        this.turnNumber = turnNumber;
        this.attacker = attacker;
        this.defender = defender;
        this.damage = damage;
        this.defenderHpAfter = defenderHpAfter;
    }
}
