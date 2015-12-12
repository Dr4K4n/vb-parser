package de.twimbee.vbparser.next;

public class PropertyDescriptor {
    private String name;
    private String type;
    private String columnName;

    public PropertyDescriptor(String name, String type) {
        super();
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getCamelName() {
        return PropertyDescriptor.toCamelCase(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public static String toCamelCase(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
}
