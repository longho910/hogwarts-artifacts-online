package edu.tcu.cs.hogwartsartifactsonline.artifact;

import edu.tcu.cs.hogwartsartifactsonline.artifact.utils.IdWorker;
import edu.tcu.cs.hogwartsartifactsonline.wizard.Wizard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    // want to mock this, so introduce it to the test class
    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> artifactList;
    @BeforeEach
    void setUp() {
        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a1.setImgUrl("ImageUrl");

        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("An invisibility cloak is used to make the wearer invisible.");

        this.artifactList = new ArrayList<>();
        this.artifactList.add(a1);
        this.artifactList.add(a2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        // Given. Arrange inputs and targets. Define the behavior of Mock object artifactRepository
        /* "id": "1250808601744904192",
            "name": "Invisibility Cloak",
            "description": "An invisibility cloak is used to make the wearer invisible.",
            "imageUrl": "ImageUrl"

         */
        Artifact a = new Artifact();

        a.setId("1250808601744904192");
        a.setName("Invisibility Cloak");
        a.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a.setImgUrl("ImageUrl");

        Wizard w = new Wizard();
        w.setId(2);
        w.setName("Harry Potter");

        a.setOwner(w);

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a)); // Define the behavior of Mock object

        // When. Act on the target behavior. When steps should cover the method to be tested.
        Artifact returnedArtifact = artifactService.findById("1250808601744904192");


        // Then. Assert expected outcomes.
        assertThat(returnedArtifact.getId()).isEqualTo(a.getId());
        assertThat(returnedArtifact.getName()).isEqualTo(a.getName());
        assertThat(returnedArtifact.getDescription()).isEqualTo(a.getDescription());
        assertThat(returnedArtifact.getImgUrl()).isEqualTo(a.getImgUrl());

        // make sure to call once artifactRepository inside artifactService
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void testFindByIdFail() {
        // Given
        given(artifactRepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(()->{
            Artifact returnedArtifact = artifactService.findById("1250808601744904192");
        });

        // Then
        assertThat(thrown)
                .isInstanceOf(ArtifactNotFoundException.class)
                .hasMessage("Could not find artifact with Id 1250808601744904192 :(");
        verify(artifactRepository, times(1)).findById("1250808601744904192");

    }

    @Test
    void testFindAllSuccess() {
        // Given: means not talking to db, just define the wanted behavior (expected output)
        given(artifactRepository.findAll()).willReturn(this.artifactList);

        // When: means actual list of artifacts
        List<Artifact> actualArtifactsList = artifactService.findAll();

        // Then
        assertThat(actualArtifactsList.size()).isEqualTo(this.artifactList.size());
        verify(artifactRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess() {
        // Given
        Artifact newArtifact = new Artifact();
        newArtifact.setName("Artifact 3");
        newArtifact.setDescription("Description...");
        newArtifact.setImgUrl("ImgUrl...");

        given(idWorker.nextId()).willReturn(123456L);
        given(artifactRepository.save(newArtifact)).willReturn(newArtifact);
        // When
        Artifact savedArtifact = artifactService.save(newArtifact);
        // Then
        assertThat(savedArtifact.getId()).isEqualTo("123456");
        assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.getImgUrl()).isEqualTo(newArtifact.getImgUrl());
        verify(artifactRepository, times(1)).save(newArtifact);
    }

    @Test
    void testUpdateSuccess() {
        // Given
        Artifact oldArtifact = new Artifact();
        oldArtifact.setId("1250808601744904192");
        oldArtifact.setName("Invisibility Cloak");
        oldArtifact.setDescription("An invisibility cloak is used to make the wearer invisible.");
        oldArtifact.setImgUrl("imgUrl");

        Artifact update = new Artifact();
        update.setId("1250808601744904192");
        update.setName("Invisibility Cloak");
        update.setDescription("An updated description");
        update.setImgUrl("imgUrl");

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(oldArtifact));
        given(this.artifactRepository.save(oldArtifact)).willReturn(oldArtifact);

        // When
        Artifact updatedArtifact = artifactService.update("1250808601744904192", update);
        // Then
        assertThat(updatedArtifact.getId()).isEqualTo(update.getId());
        assertThat(updatedArtifact.getDescription()).isEqualTo(update.getDescription());
        verify(artifactRepository, times(1)).save(oldArtifact);
        verify(artifactRepository, times(1)).findById(update.getId());

    }

    @Test
    void testUpdateNotFound() {
        // Given
        Artifact update = new Artifact();
//        update.setId("1250808601744904192");
        update.setName("Invisibility Cloak");
        update.setDescription("An updated description");
        update.setImgUrl("imgUrl");

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());
        // When
        assertThrows(ArtifactNotFoundException.class, ()->{
            artifactService.update("1250808601744904192", update);
        });
        // Then
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void testDeleteSuccess() {
        // Given
        // the artifact on db
        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904192");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("An updated description");
        artifact.setImgUrl("imgUrl");

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(artifact));
        doNothing().when(this.artifactRepository).deleteById("1250808601744904192");
        // When
        artifactService.delete("1250808601744904192");
        // Then
        verify(artifactRepository, times(1)).deleteById("1250808601744904192");

    }

    @Test
    void testDeleteNotFound(){
        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());
        // When
        assertThrows(ArtifactNotFoundException.class, ()->{
            artifactService.delete("1250808601744904192");
        });
        // Then
        verify(artifactRepository, times(1)).findById("1250808601744904192");

    }
}




