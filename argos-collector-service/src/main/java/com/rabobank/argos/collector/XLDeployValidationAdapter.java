package com.rabobank.argos.collector;

import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Map;

@RequiredArgsConstructor
public class XLDeployValidationAdapter implements ValidationAdapter {
    private static final String CHARACTERS_ARE_NOT_ALLOWED = "(no `/`, `\\`, `:`, `[`, `]`, `|`, `,` or `*`) characters are allowed";
    private final Map<String, String> collectorSpecification;

    @NotNull
    @Pattern(regexp = "^[^/\\\\:\\[\\]|,*]]*$", message = CHARACTERS_ARE_NOT_ALLOWED)
    @Size(max = 255)
    public String getVersion() {
        return collectorSpecification
                .getOrDefault("version", null);
    }

    @NotNull
    @Pattern(regexp = "^[^/\\\\:\\[\\]|,*]]*$", message = CHARACTERS_ARE_NOT_ALLOWED)
    @Size(max = 255)
    public String getApplicationName() {
        return collectorSpecification
                .getOrDefault("applicationName", null);
    }

    @NotNull
    public String getUser() {
        return collectorSpecification
                .getOrDefault("user", null);
    }

    @NotNull
    public String getPassword() {
        return collectorSpecification
                .getOrDefault("password", null);
    }
}
