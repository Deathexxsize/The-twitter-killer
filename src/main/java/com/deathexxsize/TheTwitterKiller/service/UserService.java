package com.deathexxsize.TheTwitterKiller.service;

import com.deathexxsize.TheTwitterKiller.dto.FollowDTO;
import com.deathexxsize.TheTwitterKiller.dto.UserProfileDTO;
import com.deathexxsize.TheTwitterKiller.exception.AccountDeactivatedException;
import com.deathexxsize.TheTwitterKiller.mapper.FollowMapper;
import com.deathexxsize.TheTwitterKiller.mapper.UserMapper;
import com.deathexxsize.TheTwitterKiller.model.Follow;
import com.deathexxsize.TheTwitterKiller.model.User;
import com.deathexxsize.TheTwitterKiller.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FollowMapper followMapper;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    public UserProfileDTO getUserProfile(String username) {
        User user = userRepo.findByUsername(username);
        isEnable(user);
        return userMapper.toUserProfileDTO(user);
    }

    public List<FollowDTO> getFollowers(String username) {
        User user = userRepo.findByUsername(username);
        List<Follow> followers = user.getFollowers();

        return followMapper.toFollowersDTOs(followers);
    }

    public List<FollowDTO> getFollowing(String username) {
        User user = userRepo.findByUsername(username);
        List<Follow> following = user.getFollowing();

        return followMapper.toFollowingDTOs(following);
    }

    public String editUserData(String username, Map<String, Object> edits) {
        User user = userRepo.findByUsername(username);

        edits.forEach((key, value) -> {
            switch (key) {
                case "username"-> user.setUsername(String.valueOf(value));
                case "email" -> user.setEmail(String.valueOf(value));
                case "password" -> user.setPassword(encoder.encode((String.valueOf(value))));
                default -> throw new UsernameNotFoundException("Filed " + key + " cannot be updated");
            }
        });

        userRepo.save(user);
        return "Success";
    }

    public String deleteProfile(String username) {
        User user = userRepo.findByUsername(username);
        user.setEnabled(false);
        userRepo.save(user);
        return "Account is deactivated";
    }

    private boolean isEnable(User user) {
        if (user.isEnabled() == true) {
            return true;
        } else {
            throw new AccountDeactivatedException("Account is deactivated");
        }
    }

}
