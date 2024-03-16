package edu.tcu.cs.hogwartsartifactsonline.wizard;

import edu.tcu.cs.hogwartsartifactsonline.artifact.Artifact;
import edu.tcu.cs.hogwartsartifactsonline.artifact.ArtifactRepository;
import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WizardServiceTest {
    @Mock
    WizardRepository wizardRepository;
    @Mock
    ArtifactRepository artifactRepository;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizardList;

    @BeforeEach
    void setUp(){
        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Albus Dumbledore");

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");

        wizardList = new ArrayList<>();
        wizardList.add(w1);
        wizardList.add(w2);
    }
    @AfterEach
    void tearDown(){

    }

    @Test
    void testFindByIdSuccess() {
        // Given
        Artifact a = new Artifact();

        a.setId("1250808601744904192");
        a.setName("Invisibility Cloak");
        a.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a.setImgUrl("ImageUrl");

        Wizard w = new Wizard();
        w.setId(1);
        w.setName("Albus Dumbledore");

        a.setOwner(w);
        given(wizardRepository.findById(1)).willReturn(Optional.of(w));
        // When
        Wizard returnedWizard = wizardService.findById(1);
        // Then
        assertThat(returnedWizard.getId()).isEqualTo(w.getId());
        assertThat(returnedWizard.getName()).isEqualTo(w.getName());
        assertThat(returnedWizard.getArtifacts()).containsExactlyElementsOf(w.getArtifacts());
        verify(wizardRepository, times(1)).findById(1);
    }
    @Test
    void testFindByIdNotFound() {
        // Given
        given(wizardRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());
        // When
        Throwable thrown = catchThrowable(()->{
            Wizard returnedWizard = wizardService.findById(1);
        });
        // Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id 1 :(");
        verify(wizardRepository, times(1)).findById(1);
    }
    @Test
    void testFindAllSuccess(){
        // Given
        given(wizardRepository.findAll()).willReturn(this.wizardList);
        // When
        List<Wizard> actualWizardList = wizardService.findAll();
        // Then
        assertThat(actualWizardList).containsExactlyElementsOf(this.wizardList);
        verify(wizardRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess(){
        // Given
        Wizard newWizard = new Wizard();
        newWizard.setName("New name...");

        given(wizardRepository.save(newWizard)).willReturn(newWizard);

        // When
        Wizard savedWizard = wizardService.save(newWizard);

        // Then
        assertThat(savedWizard.getId()).isEqualTo(null);
        assertThat(savedWizard.getName()).isEqualTo(newWizard.getName());
        assertThat(savedWizard.getNumberOfArtifacts()).isEqualTo(0);
        verify(wizardRepository, times(1)).save(newWizard);
    }

    @Test
    void testUpdateSuccess() {
        // Given
        Wizard oldWizard = new Wizard();
        oldWizard.setId(2);
        oldWizard.setName("Harry Potter");

        Wizard update = new Wizard();
        update.setId(2);
        update.setName("Harry Potter - updated");

        given(this.wizardRepository.findById(2)).willReturn(Optional.of(oldWizard));
        given(this.wizardRepository.save(oldWizard)).willReturn(oldWizard);
        // When
        Wizard updatedWizard = wizardService.update(2, update);
        // Then
        assertThat(updatedWizard.getId()).isEqualTo(update.getId());
        assertThat(updatedWizard.getName()).isEqualTo(update.getName());
        verify(wizardRepository, times(1)).findById(2);
        verify(wizardRepository, times(1)).save(oldWizard);
    }

    @Test
    void testUpdateNotFound() {
        Wizard update = new Wizard();
        update.setId(2);
        update.setName("Harry Potter");
        // Given
        given(this.wizardRepository.findById(2)).willReturn(Optional.empty());
        // When
        assertThrows(ObjectNotFoundException.class, ()->{
            wizardService.update(2, update);
        });
        verify(wizardRepository, times(1)).findById(2);
    }

    @Test
    void testDeleteSuccess() {
        // Given
        Wizard update = new Wizard();
        update.setId(2);
        update.setName("Harry Potter");

        given(this.wizardRepository.findById(2)).willReturn(Optional.of(update));
        doNothing().when(this.wizardRepository).deleteById(2);
        // When
        wizardService.delete(2);
        // Then
        verify(wizardRepository, times(1)).deleteById(2);
    }
    @Test
    void testDeleteNotFound() {
        // Given
        given(this.wizardRepository.findById(2)).willReturn(Optional.empty());
        // When
        assertThrows(ObjectNotFoundException.class, ()->{
            wizardService.delete(2);
        });
        // Then
        verify(wizardRepository, times(1)).findById(2);
    }
    @Test
    void testAssignSuccess() {
        // Given
        Wizard wizard = new Wizard();
        wizard.setId(2);
        wizard.setName("Harry Potter");

        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904192");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("An invisibility cloak is used to make the wearer invisible.");
        artifact.setImgUrl("imgUrl");
        given(this.wizardRepository.findById(2)).willReturn(Optional.of(wizard));
        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(artifact));
        // When
        wizardService.assignArtifactToWizard(2, "1250808601744904192");
        // Then
        assertTrue(wizard.getArtifacts().contains(artifact));
        verify(wizardRepository, times(1)).findById(2);
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }
    @Test
    void testAssignNotFound() {
        // Given
        Wizard wizard = new Wizard();
        wizard.setId(2);
        wizard.setName("Harry Potter");

        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904192");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("An invisibility cloak is used to make the wearer invisible.");
        artifact.setImgUrl("imgUrl");
        given(this.wizardRepository.findById(2)).willReturn(Optional.of(wizard));
        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());
        // When
        assertThrows(ObjectNotFoundException.class, ()->{
            wizardService.assignArtifactToWizard(2, "1250808601744904192");
        });
        // Then
        verify(wizardRepository, times(1)).findById(2);
        verify(artifactRepository, times(1)).findById("1250808601744904192");

    }
}
