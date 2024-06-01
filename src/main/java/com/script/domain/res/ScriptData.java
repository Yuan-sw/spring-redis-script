package com.script.domain.res;

import lombok.Data;

@Data
public class ScriptData {

    private Integer beforeChangeStock;

    private Integer changeStock;

    private Integer afterChangeStock;

    private String key;
}
