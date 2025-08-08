package com.iafenvoy.throwable.config;

public class ThrowableConfig {
    public static final String PATH = "./config/throwable.json";
    public static final ThrowableConfig INSTANCE = ConfigLoader.load(ThrowableConfig.class, PATH, new ThrowableConfig());
    public boolean ownerPickUpOnly = false, autoPick = false, dropWhenDespawn = true;
    public int despawnDelayTicks = 20 * 60 * 5;
}
