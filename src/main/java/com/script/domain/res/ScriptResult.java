package com.script.domain.res;

import java.util.List;

import lombok.Data;

@Data
public class ScriptResult {

    private Boolean success;

    private List<ScriptData> data;

    public boolean ok() {
        return this.success == null ? false : this.success;
    }
}
