package shared.domain.engine;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * enum class for gamephases
 */
public enum GamePhase {
    @JsonProperty("PREPARATION")
    PREPARATION,
    @JsonProperty("ONGOING")
    ONGOING,
    @JsonProperty("OVER")
    OVER
}
