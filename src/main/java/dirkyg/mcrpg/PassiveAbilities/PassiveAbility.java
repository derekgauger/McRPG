package dirkyg.mcrpg.PassiveAbilities;

import java.util.UUID;

public abstract class PassiveAbility {

    UUID uuid;
    boolean active = false;
    int level = 1;

    public void start() {
        active = true;
    }

    public void stop() {
        active = false;
    }

    public boolean isCurrentlyActive() {
        return active;
    }
}
