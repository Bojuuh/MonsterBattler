package app.dtos;

import app.entities.Monster;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MonsterDTO {
    private Integer id;
    private String name;
    private Integer level;
    private Integer hp;
    private Integer attack;
    private Integer defense;
    private String area;
    private Integer spawn_weight;

    public MonsterDTO(Monster m) {
        this.id = m.getId();
        this.name = m.getName();
        this.level = m.getLevel();
        this.hp = m.getHp();
        this.attack = m.getAttack();
        this.defense = m.getDefense();
        this.area = m.getArea();
        this.spawn_weight = m.getSpawnWeight();
    }
}

