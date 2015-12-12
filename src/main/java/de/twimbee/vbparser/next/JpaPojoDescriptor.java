package de.twimbee.vbparser.next;

import java.util.ArrayList;
import java.util.List;

public class JpaPojoDescriptor {

    private String name;
    private final List<PropertyDescriptor> props = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PropertyDescriptor> getProps() {
        return props;
    }

}
