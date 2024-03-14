package edu.tcu.cs.hogwartsartifactsonline.artifact;

import edu.tcu.cs.hogwartsartifactsonline.artifact.utils.IdWorker;
import edu.tcu.cs.hogwartsartifactsonline.system.Result;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ArtifactService {
    private final ArtifactRepository artifactRepository;
    private final IdWorker idWorker;

    // constructor injection
    public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
    }

    public Artifact findById(String artifactId) {
        return this.artifactRepository.findById(artifactId)
                .orElseThrow(()->new ArtifactNotFoundException(artifactId));
    }

    public List<Artifact> findAll() {
        return this.artifactRepository.findAll();
    }

    public Artifact save(Artifact newArtifact) {
        newArtifact.setId(idWorker.nextId() + "");
        return this.artifactRepository.save(newArtifact);
    }

    public Artifact update(String artifactId, Artifact updateArtifact) {
        return this.artifactRepository.findById(artifactId)
                .map(oldArtifact -> {
                    oldArtifact.setName(updateArtifact.getName());
                    oldArtifact.setDescription(updateArtifact.getDescription());
                    oldArtifact.setImgUrl(updateArtifact.getImgUrl());
                    // save is smart. if the id exists, it will update
                    // otherwise, it will create the new artifact
                    return this.artifactRepository.save(oldArtifact);
                })
                .orElseThrow(()-> new ArtifactNotFoundException(artifactId));



    }
    public void delete(String artifactId) {
        Artifact foundArtifact = this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));

        this.artifactRepository.deleteById(artifactId);
    }
}
