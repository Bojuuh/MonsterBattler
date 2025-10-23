package app.dtos;

import app.entities.Hero;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HeroDTO {
    private Integer id;
    private String name;
    private Integer level;
    private Integer hp;
    private Integer attack;
    private Integer defense;
    private Integer xp;

    public HeroDTO(Hero h) {
        this.id = h.getId();
        this.name = h.getName();
        this.level = h.getLevel();
        this.hp = h.getHp();
        this.attack = h.getAttack();
        this.defense = h.getDefense();
        this.xp = h.getXp();
    }
}
