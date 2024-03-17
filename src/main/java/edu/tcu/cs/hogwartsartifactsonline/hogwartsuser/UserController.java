package edu.tcu.cs.hogwartsartifactsonline.hogwartsuser;

import edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.converter.UserDtoToUserConverter;
import edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.converter.UserToUserDtoConverter;
import edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.dto.UserDto;
import edu.tcu.cs.hogwartsartifactsonline.system.Result;
import edu.tcu.cs.hogwartsartifactsonline.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {
    private final UserService userService;
    private final UserToUserDtoConverter userToUserDtoConverter;
    private final UserDtoToUserConverter userDtoToUserConverter;

    public UserController(UserService userService, UserToUserDtoConverter userToUserDtoConverter, UserDtoToUserConverter userDtoToUserConverter) {
        this.userService = userService;
        this.userToUserDtoConverter = userToUserDtoConverter;
        this.userDtoToUserConverter = userDtoToUserConverter;
    }
    @GetMapping
    public Result findAllUsers() {
        List<HogwartsUser> foundUsers = this.userService.findAll();

        List<UserDto> foundUsersDto = new ArrayList<>();
        for(HogwartsUser user : foundUsers) {
            foundUsersDto.add(this.userToUserDtoConverter.convert(user));
        }
        return new Result(true, StatusCode.SUCCESS, "Find All Success", foundUsersDto);
    }
    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable Integer userId) {
        HogwartsUser foundUser = this.userService.findById(userId);
        UserDto userDto = this.userToUserDtoConverter.convert(foundUser);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", userDto);
    }

    /**
     * We are not using UserDto, but User, since we require password.
     *
     * @param newHogwartsUser
     * @return
     */
    @PostMapping
    public Result addUser(@Valid @RequestBody HogwartsUser newHogwartsUser) {
        HogwartsUser savedUser = this.userService.save(newHogwartsUser);
        UserDto savedUserDto = this.userToUserDtoConverter.convert(savedUser);
        return new Result(true, StatusCode.SUCCESS, "Add Success", savedUserDto);
    }

    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable Integer userId, @Valid @RequestBody UserDto userDto) {
        HogwartsUser updateUser = this.userDtoToUserConverter.convert(userDto);
        HogwartsUser updatedUser = this.userService.update(userId, updateUser);

        UserDto updatedUserDto = this.userToUserDtoConverter.convert(updatedUser);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedUserDto);
    }
    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable Integer userId) {
        this.userService.delete(userId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }
}
