package Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserConstructorAndGetters() {
        User user = new User(1, "johndoe", "John", "Doe", "password123");

        assertEquals(1, user.getUserID());
        assertEquals("johndoe", user.getUserName());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testPasswordValidation() {
        assertTrue(User.isValidPassword("password")); // Valid password
        assertTrue(User.isValidPassword("123456")); // Valid password
        assertFalse(User.isValidPassword("pass")); // Invalid password, too short
        assertFalse(User.isValidPassword("")); // Invalid password, empty
    }
}
