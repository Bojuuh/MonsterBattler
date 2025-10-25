package app.dtos;

import java.time.Instant;
import java.util.List;

public record BattleResultDTO(
        Integer id,
        Integer heroId,
        Integer monsterId,
        String result,
        Integer xpGained,
        Instant battleDate,
        int logCount,
        List<BattleLogDTO> logs
) {}
