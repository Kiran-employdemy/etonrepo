package com.eaton.platform.core.bean.attributetable;

import com.eaton.platform.core.bean.Attribute;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AttributeTable extends Attribute {

    private String id;
    private String headline;
    private Set<String> headers;
    private List<AttributeTableRow> rows;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public Set<String> getHeaders() {
        return headers;
    }

    public void setHeaders(Set<String> headers) {
        this.headers = headers;
    }

    public List<AttributeTableRow> getRows() {
        return rows;
    }

    public void setRows(List<AttributeTableRow> rows) {
        this.rows = rows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttributeTable that = (AttributeTable) o;
        return Objects.equals(id, that.id) && Objects.equals(headline, that.headline) && Objects.equals(headers, that.headers) && Objects.equals(rows, that.rows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, headline, headers, rows);
    }
}
