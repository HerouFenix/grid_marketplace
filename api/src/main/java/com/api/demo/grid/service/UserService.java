package com.api.demo.grid.service;


import com.api.demo.grid.dtos.UserDTO;
import com.api.demo.grid.exception.ExceptionDetails;
import com.api.demo.grid.exception.UserNotFoundException;
import com.api.demo.grid.models.User;
import com.api.demo.grid.proxy.UserInfoProxy;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import com.api.demo.grid.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository mRepository;

    @Autowired
    private ModelMapper mModelMapper;

    private BCryptPasswordEncoder mPasswordEncoder = new BCryptPasswordEncoder();

    @SneakyThrows
    public User getUser(String username) {

        return mRepository.findByUsername(username);
    }

    public User saveUser(UserDTO user) throws ExceptionDetails {

        // verify if there are already an user with that username in the database
        if (this.getUser(user.getUsername()) != null) {
            throw new ExceptionDetails("There is already a user with that username");
        }

        // verify if there are already some user with that email in the database
        if (mRepository.findByEmail(user.getEmail()) != null) {
            throw new ExceptionDetails("There is already a user with that email");
        }

        // verify if the all credit card info was added at the same time
        String creditCardNumber = user.getCreditCardNumber();
        String creditCardCSC = user.getCreditCardCSC();
        String creditCardOwner = user.getCreditCardOwner();
        Date creditCardExpirationDate = user.getCreditCardExpirationDate();
        boolean noneCreditCardInfo = creditCardNumber == null && creditCardCSC == null  && creditCardOwner == null
                && creditCardExpirationDate == null;
        boolean allCreditCardInfo = creditCardNumber != null && creditCardCSC != null  && creditCardOwner != null
                && creditCardExpirationDate != null;
        if (!allCreditCardInfo && !noneCreditCardInfo)
            throw new ExceptionDetails("If you add a new card you have to give all the details referring to that card");

        User userSave = convertToEntity(user);

        userSave.setPassword(mPasswordEncoder.encode(userSave.getPassword()));
        return mRepository.save(userSave);
    }

    public UserInfoProxy getUserInfo(String username) throws UserNotFoundException {
        User user = mRepository.findByUsername(username);

        if (user == null) throw new UserNotFoundException("Username not found in the database");

        return new UserInfoProxy(user);
    }

    public User getFullUserInfo(String username) throws UserNotFoundException {
        User user = mRepository.findByUsername(username);

        if (user == null) throw new UserNotFoundException("Username not found in the database");

        return user;
    }

    public User addFundsToUser(long id, double funds) throws UserNotFoundException {
        Optional<User> optional = mRepository.findById(id);
        if (optional.isEmpty()) throw new UserNotFoundException("Username not found in database");
        User user = optional.get();

        user.setFunds(user.getFunds() + funds);
        mRepository.save(user);
        System.out.println("ohmendes");
        return user;
    }
    
    public void deleteUser(String username) {

        User user = mRepository.findByUsername(username);

        if (user == null) return;

        mRepository.delete(user);
    }


    private User convertToEntity(UserDTO userDto) {
        return mModelMapper.map(userDto, User.class);
    }
}
