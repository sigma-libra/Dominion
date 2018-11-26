package shared.domain.engine;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * enum class for turn phases
 * @author Alex
 */
public enum TurnPhase {
    @JsonProperty("ACTION_PHASE")
    ACTION_PHASE,
    @JsonProperty("BUY_PHASE")
    BUY_PHASE
}
