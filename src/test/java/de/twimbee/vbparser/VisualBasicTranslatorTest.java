package de.twimbee.vbparser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

public class VisualBasicTranslatorTest {

    private VisualBasicTranslator translator = new VisualBasicTranslator();

    @Test
    public void testSimpleVariableDeclarationAndStringAssignment() {
        String actual = translator.translateLine("Dim test As String = \"Hello World\"");
        assertEquals("String test = \"Hello World\";", actual);
    }

    @Test
    public void testSimpleVariableDeclaration() {
        String actual = translator.translateLine("Dim test As String");
        assertEquals("String test;", actual);
    }

    @Test
    public void testSimpleVariableDeclarationNew() {
        String actual = translator.translateLine("Dim test As New SqlParameter(\"@id_customer\")");
        assertEquals("SqlParameter test = new SqlParameter(\"@id_customer\");", actual);
    }

    @Test
    public void testSimpleVariableDeclarationAndAssignment() {
        String actual = translator.translateLine("Dim test As SqlParameter = New SqlParameter(\"@id_customer\")");
        assertEquals("SqlParameter test = new SqlParameter(\"@id_customer\");", actual);
    }

    @Test
    public void testListVariableDeclarationAndAssignment() {
        String actual = translator.translateLine("Dim result As New List(Of Country)");
        assertEquals("List<Country> result = new ArrayList<>();", actual);
    }

    @Test
    public void testForEach() {
        String actual = translator.translateLine("For Each dataRow As DataRow In dataSet.Tables(0).Rows");
        assertEquals("for (DataRow dataRow : dataSet.Tables(0).Rows) {", actual);
    }

    @Test
    public void testSimpleVariableAssignmentLiteral() {
        String actual = translator.translateLine("test = \"Hello World\"");
        assertEquals("test = \"Hello World\";", actual);
    }

    @Test
    public void testSimpleVariableAssignment() {
        String actual = translator.translateLine("test = New SqlParameter(\"@id_customer\")");
        assertEquals("test = new SqlParameter(\"@id_customer\");", actual);
    }

    @Test
    public void testMethodDeclarationNoParams() {
        String actual = translator.translateLine("Public Function FindAll() As List(Of Country)");
        assertEquals("public List<Country> findAll() {", actual);
    }

    @Test
    public void testMethodDeclarationOneParam() {
        String actual = translator.translateLine("Public Function FindById(countryId As Integer) As Country");
        assertEquals("public Country findById(Integer countryId) {", actual);
    }

    @Test
    public void testPOVBO() throws IOException {
        String expectedJavaClazz = readFile("Country.java");
        String visualBasicClazz = readFile("Country.vb");
        String actualVisualBasicClazz = translator.translateClass(visualBasicClazz);
        assertEquals(expectedJavaClazz, actualVisualBasicClazz);
    }

    @Test
    public void testService() throws IOException {
        String expectedJavaClazz = readFile("CountryService.java");
        String visualBasicClazz = readFile("CountryService.vb");
        String actualVisualBasicClazz = translator.translateClass(visualBasicClazz);
        assertEquals(expectedJavaClazz, actualVisualBasicClazz);
    }

    private String readFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/" + filename)));
    }
}
