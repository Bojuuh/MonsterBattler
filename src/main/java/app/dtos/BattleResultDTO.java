package app.dtos;

import java.util.List;

public record BattleResultDTO(
        Integer id,
        String heroName,
        String monsterName,
        String result,
        Integer xpGained,
        int logCount,
        List<BattleLogDTO> logs
) {}
