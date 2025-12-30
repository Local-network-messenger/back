package com.local_messenger.back.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RuntimeData {
    @Builder.Default
    private Boolean isRegistered = false;
    private String filePath;
    private String name;
    private String id;
}
