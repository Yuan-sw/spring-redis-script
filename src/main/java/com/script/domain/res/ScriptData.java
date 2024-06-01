package com.script.domain.res;

import lombok.Data;

@Data
public class ScriptData {

    /**
     * 此次库存变动之前的原始可售库存值
     */
    private Integer beforeChangeSaleStock;

    /*
     * 此次库存变动的可售库存值
     */
    private Integer changeSaleStock;

    /**
     * 此次库存变动后的可售库存值
     */
    private Integer afterChangeSaleStock;

    /**
     * 此次库存变动前的冻结库存值
     */
    private Integer beforeChangeFrozenStock;

    /*
     * 此次库存变动的冻结库存值
     */
    private Integer changeFrozenStock;

    /**
     * 此次库存变动后的冻结库存值
     */
    private Integer afterChangeFrozenStock;

    /**
     * 变动的key，公用key，不含冻结/可售后缀
     */
    private String key;
}
