package classicMod.library;

import arc.util.Nullable;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.net.Packets;

public class EventTypeExtended extends EventType {
    public static class UpdateModInformation {
        public final boolean overBuild;

        public final int build;

        public UpdateModInformation(boolean overBuild, int build) {
            this.overBuild = overBuild;
            this.build = build;
        }
    }
}
