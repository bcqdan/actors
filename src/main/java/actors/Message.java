package actors;

import java.util.Map;

public record Message(String event, Map<String, Object> payload) {
}
