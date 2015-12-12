package de.twimbee.vbparser.next;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VisualBasicTranslator {

    private static final String INDENT = "    ";
    private static final String LINE_BREAK = System.getProperty("line.separator");

    public JpaPojoDescriptor readPovbo(Path povbo) throws IOException {
        JpaPojoDescriptor result = new JpaPojoDescriptor();
        String vbContent = new String(Files.readAllBytes(povbo));

        Pattern classPattern = Pattern.compile("Public Class ([a-zA-Z0-9]+)");
        Matcher classMatcher = classPattern.matcher(vbContent);
        classMatcher.find();
        String className = classMatcher.group(1);
        result.setName(className);

        Pattern propPattern = Pattern.compile("Public Property ([a-zA-Z0-9]+) As ([A-Za-z0-9\\(\\)]+)");
        Matcher propMatcher = propPattern.matcher(vbContent);
        while (propMatcher.find()) {
            String propertyName = propMatcher.group(1);
            String propertyType = propMatcher.group(2);

            if ("Id".equals(propertyName) && "Integer".equals(propertyType)) {
                propertyType = "Long";
            }

            result.getProps().add(new PropertyDescriptor(propertyName, propertyType));
        }

        return result;
    }

    public Map<String, String> readConvertsFromService(Path service) throws IOException {
        Map<String, String> converts = new HashMap<>();
        String vbContent = new String(Files.readAllBytes(service));

        Pattern convertPattern = Pattern
                .compile("result\\.([A-Za-z0-9]+) = C[A-Za-z]+\\(dataRow.Item\\(\"([a-z0-9_]+)\"\\)\\)");
        Matcher convertMatcher = convertPattern.matcher(vbContent);

        while (convertMatcher.find()) {
            String propertyName = convertMatcher.group(1);
            String propertyColumnName = convertMatcher.group(2);
            converts.put(propertyName, propertyColumnName);
        }

        return converts;
    }

    public void addColumnNamesFromService(JpaPojoDescriptor pojoDescriptor, Map<String, String> converts) {
        for (PropertyDescriptor prop : pojoDescriptor.getProps()) {
            if (converts.containsKey(prop.getName())) {
                prop.setColumnName(converts.get(prop.getName()));
            }
        }
    }

    public String buildJpaPojo(JpaPojoDescriptor pojoDescriptor) throws IOException {
        StringBuilder jpaPojoContent = new StringBuilder();

        jpaPojoContent.append("import javax.persistence.Column;").append(LINE_BREAK)
                .append("import javax.persistence.Entity;").append(LINE_BREAK)
                .append("import javax.persistence.GeneratedValue;").append(LINE_BREAK)
                .append("import javax.persistence.GenerationType;").append(LINE_BREAK)
                .append("import javax.persistence.Id;").append(LINE_BREAK).append("import javax.persistence.Table;")
                .append(LINE_BREAK).append(LINE_BREAK);

        String tableName = "tbl" + pojoDescriptor.getName();
        jpaPojoContent.append("@Entity").append(LINE_BREAK);
        jpaPojoContent.append("@Table(name = \"").append(tableName).append("\")").append(LINE_BREAK);

        jpaPojoContent.append("public class " + pojoDescriptor.getName() + " {" + LINE_BREAK);

        for (PropertyDescriptor prop : pojoDescriptor.getProps()) {
            jpaPojoContent.append(generateFieldWithJpaAnnotations(prop));
        }

        for (PropertyDescriptor prop : pojoDescriptor.getProps()) {
            jpaPojoContent.append(generateGetterAndSetter(prop));
        }

        jpaPojoContent.append("}");

        return jpaPojoContent.toString();
    }

    public String buildSpringDataRepository(JpaPojoDescriptor jpaPojoDescriptor) {
        StringBuilder repoBuilder = new StringBuilder();

        repoBuilder.append("import org.springframework.data.jpa.repository.JpaRepository;").append(LINE_BREAK)
                .append(LINE_BREAK);
        repoBuilder.append("public interface ").append(jpaPojoDescriptor.getName())
                .append("Repository extends JpaRepository<").append(jpaPojoDescriptor.getName()).append(",Long> {")
                .append(LINE_BREAK);
        repoBuilder.append(LINE_BREAK).append("}");

        return repoBuilder.toString();
    }

    private String generateFieldWithJpaAnnotations(PropertyDescriptor prop) {
        StringBuilder jpaPojoContent = new StringBuilder();
        if ("Id".equals(prop.getName())) {
            jpaPojoContent.append(INDENT).append("@Id").append(LINE_BREAK);
            jpaPojoContent.append(INDENT).append("@GeneratedValue(strategy = GenerationType.AUTO)").append(LINE_BREAK);
        }
        jpaPojoContent.append(INDENT).append("@Column");
        if (prop.getColumnName() != null && !prop.getColumnName().isEmpty()) {
            jpaPojoContent.append("(name = \"").append(prop.getColumnName()).append("\")");
        }
        jpaPojoContent.append(LINE_BREAK);

        jpaPojoContent.append(INDENT).append("private ").append(prop.getType()).append(" ").append(prop.getCamelName())
                .append(";").append(LINE_BREAK);
        return jpaPojoContent.toString();
    }

    private String generateGetterAndSetter(PropertyDescriptor prop) {
        StringBuilder getSetBuilder = new StringBuilder();
        getSetBuilder.append(LINE_BREAK);
        // getter
        getSetBuilder.append(INDENT).append("public ").append(prop.getType()).append(" get").append(prop.getName())
                .append("() {").append(LINE_BREAK);
        getSetBuilder.append(INDENT).append(INDENT).append("return ").append(prop.getCamelName()).append(";")
                .append(LINE_BREAK);
        getSetBuilder.append(INDENT).append("}").append(LINE_BREAK).append(LINE_BREAK);
        // setter
        getSetBuilder.append(INDENT).append("public void set").append(prop.getName()).append("(").append(prop.getType())
                .append(" ").append(prop.getCamelName()).append(") {").append(LINE_BREAK);
        getSetBuilder.append(INDENT).append(INDENT).append("this.").append(prop.getCamelName()).append(" = ")
                .append(prop.getCamelName()).append(";").append(LINE_BREAK);
        getSetBuilder.append(INDENT).append("}").append(LINE_BREAK);
        return getSetBuilder.toString();
    }
}
