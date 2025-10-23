package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "battle")
public class Battle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "battle_id", nullable = false, unique = true)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "hero_id", nullable = false)
    @Setter
    private Hero hero;

    @ManyToOne
    @JoinColumn(name = "monster_id", nullable = false)
    @Setter
    private Monster monster;

    @Setter
    @Column(name = "result")
    private String result; // VICTORY / DEFEAT / DRAW

    @Setter
    @Column(name = "xp_gained")
    private Integer xpGained;

    @Setter
    @Column(name = "battle_date")
    private Instant battleDate = Instant.now();

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<BattleLog> logs = new HashSet<>();

    public void addLog(BattleLog log) {
        if (log != null) {
            logs.add(log);
            log.setBattle(this);
        }
    }
}
