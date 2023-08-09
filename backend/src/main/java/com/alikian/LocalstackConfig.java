package com.alikian;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class LocalstackConfig {
    String imageName;
    List<String> activateOnProfiles;
    @Data
    public static class Resources{
        List<Secret> secretsManagers;
        @Data
        public static class Secret{
            String name;
            String value;
            String description;
        }
    };
    Resources resources;
}
