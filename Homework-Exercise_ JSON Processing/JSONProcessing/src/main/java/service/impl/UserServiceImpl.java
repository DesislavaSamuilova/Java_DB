package service.impl;

import com.google.gson.Gson;
import model.dto.UserSeedDto;
import model.dto.UserSoldDto;
import model.dto.UserWithSoldProductsDto;
import model.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import service.UserService;
import util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final String USERS_FILE_PATH = "JSONProcessing/src/main/resources/files/users.json";
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
    }

    @Override
    public void seedUsers() throws IOException {

        if (userRepository.count() == 0) {
            Arrays.stream(gson.fromJson(Files.readString(Path.of(USERS_FILE_PATH)), UserSeedDto[].class))
                    .filter(validationUtil::isValid).map(userSeedDto -> modelMapper.map(userSeedDto, User.class))
                    .forEach(userRepository::save);
        }
    }

    @Override
    public User findRandomUser() {

        long randomId = ThreadLocalRandom.current().
                nextLong(1, userRepository.count() + 1);

        return userRepository.findById(randomId).orElse(null);

    }

    @Override
    public List<UserSoldDto> findAllUsersWithMoreThanOneSoldProducts() {

        return userRepository.findAllByUsersWithMoreThanOneSoldProductsOrderByLastNameThenFirstName()
                .stream().map(user -> modelMapper.map(user, UserSoldDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserWithSoldProductsDto> findAllUsersWithSoldProducts() {

        return this.userRepository.findAllByUsersWithMoreThanOneSoldProductOrderByProductsSold().stream()
                .map(user -> modelMapper.map(user,UserWithSoldProductsDto.class)).collect(Collectors.toList());
    }
}