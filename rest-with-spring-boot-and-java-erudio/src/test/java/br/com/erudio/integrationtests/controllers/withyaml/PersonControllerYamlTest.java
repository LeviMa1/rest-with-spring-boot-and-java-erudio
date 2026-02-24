package br.com.erudio.integrationtests.controllers.withyaml;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import br.com.erudio.integrationtests.dto.PersonDTO;
import br.com.erudio.integrationtests.dto.wrappers.xmlandyaml.PagedModelPerson;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper yamlMapper;

    private static PersonDTO person;

    @BeforeAll
    static void setUp() {
        yamlMapper = new YAMLMapper();

        person = new PersonDTO();
    }

    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        mockPerson();

        specification = new RequestSpecBuilder().addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ONESYS)
                .setBasePath("/api/person/v1").setPort(TestConfigs.SERVER_PORT).addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL)).build();

        var createdPerson = given().config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
                .spec(specification).contentType(MediaType.APPLICATION_YAML_VALUE).accept(MediaType.APPLICATION_YAML_VALUE).body(person, yamlMapper)
                .when().post().then().statusCode(200).contentType(MediaType.APPLICATION_YAML_VALUE).extract().body().as(PersonDTO.class, yamlMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.isEnabled());
    }

    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {
        person.setLastName("Benedict Torvalds");

        var createdPerson = given().config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
                .spec(specification).contentType(MediaType.APPLICATION_YAML_VALUE).accept(MediaType.APPLICATION_YAML_VALUE).body(person, yamlMapper)
                .when().put().then().statusCode(200).contentType(MediaType.APPLICATION_YAML_VALUE).extract().body().as(PersonDTO.class, yamlMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Benedict Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.isEnabled());
    }

    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {
        var createdPerson = given().config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
                .spec(specification).contentType(MediaType.APPLICATION_YAML_VALUE).accept(MediaType.APPLICATION_YAML_VALUE).pathParam("id", person.getId())
                .when().get("{id}").then().statusCode(200).contentType(MediaType.APPLICATION_YAML_VALUE).extract().body().as(PersonDTO.class, yamlMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Benedict Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.isEnabled());
    }

    @Test
    @Order(4)
    void disableTest() throws JsonProcessingException {
        var createdPerson = given().config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
                .spec(specification).accept(MediaType.APPLICATION_YAML_VALUE).pathParam("id", person.getId())
                .when().patch("{id}").then().statusCode(200).contentType(MediaType.APPLICATION_YAML_VALUE).extract().body().as(PersonDTO.class, yamlMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Benedict Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertFalse(createdPerson.isEnabled());
    }

    @Test
    @Order(5)
    void deleteTest() throws JsonProcessingException {
       given(specification).pathParam("id", person.getId()).when().delete("{id}").then().statusCode(204);
    }

    @Test
    @Order(6)
    void findAllTest() throws JsonProcessingException {
        var response = given(specification).accept(MediaType.APPLICATION_YAML_VALUE).queryParams("page", 3, "size", 12, "direction", "asc")
                .when().get().then().statusCode(200).contentType(MediaType.APPLICATION_YAML_VALUE).extract().body().as(PagedModelPerson.class, yamlMapper);

        List<PersonDTO> people = response.getContent();

        PersonDTO personOne = people.get(0);

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Amber", personOne.getFirstName());
        assertEquals("Pearmine", personOne.getLastName());
        assertEquals("Apt 1546", personOne.getAddress());
        assertEquals("Female", personOne.getGender());
        assertTrue(personOne.isEnabled());

        PersonDTO personFour = people.get(4);

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId() > 0);

        assertEquals("Amil", personFour.getFirstName());
        assertEquals("Loffill", personFour.getLastName());
        assertEquals("PO Box 69333", personFour.getAddress());
        assertEquals("Female", personFour.getGender());
        assertFalse(personFour.isEnabled());
    }

    @Test
    @Order(7)
    void findByNameTest() throws JsonProcessingException {
        //{{baseUrl}}/api/person/v1/findPeopleByName/and?page=0&size=12&direction=asc
        var content = given(specification).accept(MediaType.APPLICATION_YAML_VALUE).pathParam("firstName","and")
                .queryParams("page", 0, "size", 12, "direction", "asc")
                .when().get("findPeopleByName/{firstName}").then().statusCode(200).contentType(MediaType.APPLICATION_YAML_VALUE).extract().body()
                .as(PagedModelPerson.class, yamlMapper);

        List<PersonDTO> people = content.getContent();

        PersonDTO personOne = people.get(0);

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Alessandra", personOne.getFirstName());
        assertEquals("Itzkin", personOne.getLastName());
        assertEquals("Room 899", personOne.getAddress());
        assertEquals("Female", personOne.getGender());
        assertTrue(personOne.isEnabled());

        PersonDTO personFour = people.get(4);

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId() > 0);

        assertEquals("Andeee", personFour.getFirstName());
        assertEquals("Follos", personFour.getLastName());
        assertEquals("Suite 28", personFour.getAddress());
        assertEquals("Female", personFour.getGender());
        assertTrue(personFour.isEnabled());
    }

    private void mockPerson() {
        person.setFirstName("Linus");
        person.setLastName("Torvalds");
        person.setAddress("Helsinki - Finland");
        person.setGender("Male");
        person.setEnabled(true);
    }
}