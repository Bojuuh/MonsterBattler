package app.dtos;

import app.entities.Battle;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BattleDTO {
    private Integer id;
    private String heroName;
    private String monsterName;
    private String result;
    private Integer xp_gained;

    public BattleDTO(Battle b) {
        this.id = b.getId();
        this.heroName = b.getHero() != null ? b.getHero().getName() : null;
        this.monsterName = b.getMonster() != null ? b.getMonster().getName() : null;
        this.result = b.getResult();
        this.xp_gained = b.getXpGained();
    }
}
