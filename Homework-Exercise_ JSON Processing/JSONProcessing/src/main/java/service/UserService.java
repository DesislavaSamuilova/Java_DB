package service;

import model.dto.UserSoldDto;
import model.dto.UserWithSoldProductsDto;
import model.entities.User;

import java.io.IOException;
import java.util.List;

public interface UserService {

    void seedUsers() throws IOException;

    User findRandomUser();

    List<UserSoldDto> findAllUsersWithMoreThanOneSoldProducts();

    List<UserWithSoldProductsDto> findAllUsersWithSoldProducts();
}