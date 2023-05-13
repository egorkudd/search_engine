package searchengine.dto.statistics;

import lombok.Data;

@Data
public class BoolResponse {
    private boolean result;
    private String error;
}
