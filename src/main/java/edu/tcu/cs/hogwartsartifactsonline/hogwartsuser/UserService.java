package edu.tcu.cs.hogwartsartifactsonline.hogwartsuser;

import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<HogwartsUser> findAll(){
        return this.userRepository.findAll();
    }

    public HogwartsUser findById(Integer userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(()-> new ObjectNotFoundException("user", userId));
    }

    public HogwartsUser save(HogwartsUser user) {
        return this.userRepository.save(user);
    }

    public HogwartsUser update(Integer userId, HogwartsUser updateUser) {
        return this.userRepository.findById(userId)
                .map(oldUser -> {
                    oldUser.setUsername(updateUser.getUsername());
                    oldUser.setEnabled(updateUser.isEnabled());
                    oldUser.setRoles(updateUser.getRoles());

                    return this.userRepository.save(oldUser);
                }).orElseThrow(() -> new ObjectNotFoundException("user", userId));
    }

    public void delete(Integer userId) {
        this.userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        this.userRepository.deleteById(userId);
    }

}
