package app.dtos;

public record BattleLogDTO(
        Integer turnNumber,
        String attacker,
        String defender,
        Integer damage,
        Integer defenderHpAfter
) {}
