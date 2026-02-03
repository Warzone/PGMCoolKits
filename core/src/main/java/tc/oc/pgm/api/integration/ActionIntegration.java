package tc.oc.pgm.api.integration;

import tc.oc.pgm.action.Action;

public interface ActionIntegration {
    <T> Action<T> getNativeAction(String id, Class<T> scope);
}
