package de.twimbee.vbparser.next;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Test;

public class VisualBasicTranslatorTest {

    VisualBasicTranslator translator = new VisualBasicTranslator();

    @Test
    public void testCreateJpaPojo() throws IOException {
        Path expectedJavaClazz = Paths.get("src/test/resources/CountryJpa.java");
        Path visualBasicClazz = Paths.get("src/test/resources/Country.vb");
        JpaPojoDescriptor jpaPojoDescriptor = translator.readPovbo(visualBasicClazz);

        Path visualBasicServiceClazz = Paths.get("src/test/resources/CountryService.vb");
        Map<String, String> converts = translator.readConvertsFromService(visualBasicServiceClazz);
        translator.addColumnNamesFromService(jpaPojoDescriptor, converts);

        // test the generated JPA POJO
        String actualJavaClazz = translator.buildJpaPojo(jpaPojoDescriptor);
        assertEquals(readPath(expectedJavaClazz), actualJavaClazz);

        // test the generated Spring Data Repository
        Path expectedRepository = Paths.get("src/test/resources/CountryRepository.java");
        String actualRepository = translator.buildSpringDataRepository(jpaPojoDescriptor);
        assertEquals(readPath(expectedRepository), actualRepository);
    }

    private String readPath(Path filename) throws IOException {
        return new String(Files.readAllBytes(filename));
    }

}
