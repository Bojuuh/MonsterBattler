package app.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
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
    private Hero hero;

    @ManyToOne
    @JoinColumn(name = "monster_id", nullable = false)
    private Monster monster;

    @Column(name = "result")
    private String result; // VICTORY / DEFEAT / DRAW

    @Column(name = "xp_gained")
    private Integer xpGained;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<BattleLog> logs = new HashSet<>();

    public void addLog(BattleLog log) {
        if (log != null) {
            logs.add(log);
            log.setBattle(this);
        }
    }
}
