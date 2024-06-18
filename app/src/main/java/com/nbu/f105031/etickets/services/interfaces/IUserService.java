package com.nbu.f105031.etickets.services.interfaces;

public interface IUserService {
    double getUserBalance(String usernameOrEmail);
    void updateUserBalance(String usernameOrEmail, double newBalance);
    boolean addUser(String username, String email, String password);
    boolean checkUser(String usernameOrEmail, String password);
    boolean checkUsernameExists(String username);
    boolean checkEmailExists(String email);
    void addBalance(String usernameOrEmail, double amount);
    int getUserId(String usernameOrEmail);
    boolean buyTicket(int userId, int ticketId, double ticketPrice);
}
