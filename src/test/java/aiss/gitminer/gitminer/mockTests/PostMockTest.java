package aiss.gitminer.gitminer.mockTests;

import aiss.gitminer.controllers.ProjectController;
import aiss.gitminer.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProjectController.class)
public class PostMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ProjectController projectController;

    public Project getMockProject(Boolean good) {
        User mockUser = new User("1", "Pablo Caballero", "Pablo", "www.avatarUrl.com", "www.webUrl.com");
        Comment mockComment = new Comment("1", "lorem ipsum dolor sit aemet", mockUser, "06/05/2023", "06/05/2023");
        Commit mockCommit = new Commit("1", "title", "message", "Pablo Caballero", "pabcabmar03@gmail.com",
                "06/05/2023", "Pablo Caballero", "pabcabmar03@gmail.com",
                "06/05/2023", "www.webUrl.com");
        Issue mockIssue = new Issue("1", "1", "title", "description", "state", "06/05/2023", "06/05/2023",
                "06/05/2023", List.of("label 1", "label 2", "label 3"), mockUser, mockUser, 5, 7,
                "www.webUrl.com", List.of(mockComment));

        // aquí meto un dato incorrecto según el modelo de Project en caso de que good sea false
        Project mockProject = new Project("1", good ? "project name" : "", "www.webUrl.com", List.of(mockCommit), List.of(mockIssue));
        return mockProject;
    }

    @Test
    @DisplayName("Testing positivo de postear proyecto mock")
    public void postMockProjectPositive() throws Exception {

        // Primero construyo el dato de ejemplo
        Project mockProject = getMockProject(true);

        // Luego lo convierto a String que es lo que espera recibir el .content()
        String mockProjectAsString = new ObjectMapper().writeValueAsString(mockProject);

        // Aquí le digo que en la HTTPResponse quiero que me devuelva el objeto mock que acabo de crear porque
        // por defecto no lo hace, incluso aunque el create en java devuelva la respuesta
        when(projectController.create(any(Project.class))).thenReturn(mockProject);

        this.mockMvc.perform(post("/gitminer/projects").content(mockProjectAsString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.id", is(mockProject.getId()))) // <-- me aseguro de que está bien creado y los campos están bien
                .andExpect(jsonPath("$.name", is(mockProject.getName())))
                .andExpect(jsonPath("$.web_url", is(mockProject.getWebUrl())))
        ;
    }

    @Test
    @DisplayName("Testing negativo de postear proyecto mock")
    public void postMockProjectNegative() throws Exception {

        Project mockProject = getMockProject(false);

        String mockProjectAsString = new ObjectMapper().writeValueAsString(mockProject);

        when(projectController.create(any(Project.class))).thenReturn(mockProject);

        this.mockMvc.perform(post("/gitminer/projects").content(mockProjectAsString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()); // como el dato es incorrecto, quiero un error 400
    }

    @Test
    @DisplayName("Testing de findAll con proyectos mock")
    public void getMockProjects() throws Exception {

        Project mockProject = getMockProject(true);

        String mockProjectAsString = new ObjectMapper().writeValueAsString(mockProject);

        when(projectController.findAll(any(Integer.class), any(Integer.class), any(String.class))).thenReturn(List.of(mockProject));

        this.mockMvc.perform(get("/gitminer/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id", is(mockProject.getId())))
                .andExpect(jsonPath("$[0].name", is(mockProject.getName())))
                .andExpect(jsonPath("$[0].web_url", is(mockProject.getWebUrl())));
    }

    @Test
    @DisplayName("Testing positivo de findOne con proyectos mock")
    public void getOneMockProjectPositive() throws Exception {

        Project mockProject = getMockProject(true);

        String mockProjectAsString = new ObjectMapper().writeValueAsString(mockProject);

        when(projectController.findOne(mockProject.getId())).thenReturn(mockProject);

        this.mockMvc.perform(get("/gitminer/projects/" + mockProject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.id", is(mockProject.getId())))
                .andExpect(jsonPath("$.name", is(mockProject.getName())))
                .andExpect(jsonPath("$.web_url", is(mockProject.getWebUrl())));
    }

    @Test
    @DisplayName("Testing negativo de findOne con proyectos mock")
    public void getOneMockProjectNegative() throws Exception {

        Project mockProject = getMockProject(true);

        String mockProjectAsString = new ObjectMapper().writeValueAsString(mockProject);

        when(projectController.findOne(mockProject.getId())).thenReturn(mockProject);
        // Yo en este get no estoy usando el mío, sino el del servlet de MockMvc, por eso cuando el get no encuentra nada
        // solo no devuelve body, en vez de devolver un 404 :(
        this.mockMvc.perform(get("/gitminer/projects/" + "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$").doesNotExist());
    }
}
