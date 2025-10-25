package app.mappers;

import app.dtos.BattleLogDTO;
import app.dtos.BattleResultDTO;
import app.entities.Battle;
import app.entities.BattleLog;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class BattleMapper {

    private BattleMapper() {}

    public static BattleLogDTO toDto(BattleLog log) {
        if (log == null) return null;
        return new BattleLogDTO(
                log.getTurnNumber(),
                log.getAttacker(),
                log.getDefender(),
                log.getDamage(),
                log.getDefenderHpAfter()
        );
    }

    public static BattleResultDTO toDto(Battle battle) {
        Objects.requireNonNull(battle, "battle must not be null");

        List<BattleLogDTO> sortedLogs = battle.getLogs() == null ? List.of()
                : battle.getLogs().stream()
                .sorted(Comparator.comparing(l -> l.getTurnNumber() == null ? Integer.MAX_VALUE : l.getTurnNumber()))
                .map(BattleMapper::toDto)
                .collect(Collectors.toList());

        return new BattleResultDTO(
                battle.getId(),
                battle.getHero() != null ? battle.getHero().getId() : null,
                battle.getMonster() != null ? battle.getMonster().getId() : null,
                battle.getResult(),
                battle.getXpGained(),
                battle.getBattleDate(),
                sortedLogs.size(),
                sortedLogs
        );
    }
}
