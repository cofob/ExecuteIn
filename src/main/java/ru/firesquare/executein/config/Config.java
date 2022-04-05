package ru.firesquare.executein.config;

import redempt.redlib.config.annotations.Comment;
import redempt.redlib.config.annotations.ConfigMappable;

@ConfigMappable
public class Config {
    @Comment("DB config")
    public static String database = "jdbc:sqlite:plugins/executein/database.db";
}
