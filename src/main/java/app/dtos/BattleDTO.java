package app.dtos;

import app.entities.Battle;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class BattleDTO {
    private Integer id;
    private Integer heroId;
    private Integer monsterId;
    private String result;
    private Integer xp_gained;
    private Instant battle_date;

    public BattleDTO(Battle b) {
        this.id = b.getId();
        this.heroId = b.getHero() != null ? b.getHero().getId() : null;
        this.monsterId = b.getMonster() != null ? b.getMonster().getId() : null;
        this.result = b.getResult();
        this.xp_gained = b.getXpGained();
        this.battle_date = b.getBattleDate();
    }
}
