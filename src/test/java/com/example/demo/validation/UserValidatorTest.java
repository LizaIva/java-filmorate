package com.example.demo.validation;

import com.example.demo.exception.ValidationException;
import com.example.demo.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    @Test
    public void validateEmailTest(){
        User user = new User(null, "home", "Liza", LocalDate.of(1999, 10, 25));
        assertThrows(ValidationException.class, ()->UserValidator.validate(user));

        User user1 = new User("", "home", "Liza", LocalDate.of(1999, 10, 25));
        assertThrows(ValidationException.class, ()->UserValidator.validate(user1));

        User user2 = new User("  ", "home", "Liza", LocalDate.of(1999, 10, 25));
        assertThrows(ValidationException.class, ()->UserValidator.validate(user1));

        User user3 = new User("ivaivaiva#bk.ru", "home", "Liza", LocalDate.of(1999, 10, 25));
        assertThrows(ValidationException.class, ()->UserValidator.validate(user1));

        User user4 = new User("ivaivaiva@bk.ru", "home", "Liza", LocalDate.of(1999, 10, 25));
        assertDoesNotThrow(()->UserValidator.validate(user4));
    }

    @Test
    public void validateLoginTest(){
        User user = new User("ivaivaiva@bk.ru", null, "Liza", LocalDate.of(1999, 10, 25));
        assertThrows(ValidationException.class, ()->UserValidator.validate(user));

        User user1 = new User("ivaivaiva@bk.ru", "", "Liza", LocalDate.of(1999, 10, 25));
        assertThrows(ValidationException.class, ()->UserValidator.validate(user1));

        User user2 = new User("ivaivaiva@bk.ru", "  ", "Liza", LocalDate.of(1999, 10, 25));
        assertThrows(ValidationException.class, ()->UserValidator.validate(user2));

        User user3 = new User("ivaivaiva@bk.ru", "ho me", "Liza", LocalDate.of(1999, 10, 25));
        assertThrows(ValidationException.class, ()->UserValidator.validate(user3));
    }

    @Test
    public void validateNameTest(){
        User user = new User("ivaivaiva@bk.ru", "home", null, LocalDate.of(1999, 10, 25));
        assertDoesNotThrow(()->UserValidator.validate(user));
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    public void validateBirthday(){
        User user = new User("ivaivaiva@bk.ru", "home", "Liza", LocalDate.of(2078, 10, 25));
        assertThrows(ValidationException.class, ()->UserValidator.validate(user));

        User user1 = new User("ivaivaiva@bk.ru", "home", "Liza", null);
        assertThrows(ValidationException.class, ()->UserValidator.validate(user));
    }


    @Test
    public void validateUpdatedTest() {
        User updateUser = new User(null, null, null, null);
        assertDoesNotThrow(() -> UserValidator.validateForUpdate(updateUser));

        updateUser.setEmail("lalalalala");
        assertThrows(ValidationException.class, () -> UserValidator.validateForUpdate(updateUser));
        updateUser.setEmail(null);

        updateUser.setBirthday(LocalDate.of(2087, 11, 6));
        assertThrows(ValidationException.class, () -> UserValidator.validateForUpdate(updateUser));
        updateUser.setBirthday(null);

        updateUser.setLogin("home 123");
        assertThrows(ValidationException.class, () -> UserValidator.validateForUpdate(updateUser));

        updateUser.setLogin("home123");

        updateUser.setName("  ");
        assertDoesNotThrow(() -> UserValidator.validateForUpdate(updateUser));
        assertEquals(updateUser.getLogin(), updateUser.getName());
    }

}
