package edu.tcu.cs.hogwartsartifactsonline.artifact.dto;

import edu.tcu.cs.hogwartsartifactsonline.wizard.dto.WizardDto;
import jakarta.validation.constraints.NotEmpty;

public record ArtifactDto(String id,
                          // can also use @Length, @Pattern
                          @NotEmpty(message = "name is required.")
                          String name,
                          @NotEmpty(message = "description is required.")
                          String description,
                          @NotEmpty(message = "imgUrl is required.")
                          String imgUrl,
                          WizardDto owner) {
}
