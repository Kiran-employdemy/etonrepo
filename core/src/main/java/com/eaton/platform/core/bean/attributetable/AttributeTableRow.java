package com.eaton.platform.core.bean.attributetable;

import java.util.Map;
import java.util.Objects;

public class AttributeTableRow {

    private Integer id;
    private Map<String, String> cells;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Map<String, String> getCells() {
        return cells;
    }

    public void setCells(Map<String, String> cells) {
        this.cells = cells;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttributeTableRow that = (AttributeTableRow) o;
        return Objects.equals(id, that.id) && Objects.equals(cells, that.cells);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cells);
    }
}
