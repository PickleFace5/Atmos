package com.pickleface5.atmos.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ClientConfigHandler CLIENT;
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final ServerConfigHandler SERVER;

    static {
        {
            final Pair<ClientConfigHandler, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(ClientConfigHandler::new);
            CLIENT = clientSpecPair.getLeft();
            CLIENT_SPEC = clientSpecPair.getRight();

            final Pair<ServerConfigHandler, ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(ServerConfigHandler::new);
            SERVER = serverSpecPair.getLeft();
            SERVER_SPEC = serverSpecPair.getRight();
        }
    }
}
