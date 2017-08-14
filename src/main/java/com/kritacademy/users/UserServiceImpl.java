package com.kritacademy.users;

import com.kritacademy.authorities.Authority;
import com.kritacademy.authorities.AuthorityService;
import com.kritacademy.exceptions.DataNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final AuthorityService authorityService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, AuthorityService authorityService) {
        this.userRepository = userRepository;
        this.authorityService = authorityService;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> findAllWithAuthorities(Pageable pageable) {
        return userRepository.findAllWithAuthorities(pageable);
    }

    @Override
    public Optional<User> findOneByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }

    @Override
    public Optional<User> findOneByFacebookId(BigInteger id) {
        return userRepository.findOneByFacebookId(id);
    }

    @Override
    public User findOneById(Integer id) {
        return userRepository.findOne(id);
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(Integer id) {
        userRepository.delete(id);
    }

    @Override
    public User promoteUserById(Integer id, String authorityName) {
        Optional<User> user = Optional.ofNullable(this.userRepository.findOne(id));
        if(user.isPresent()){
            User newUser = user.get();
            Optional<Authority> promoteAuthority = this.authorityService.findOneByName(authorityName);
            if(promoteAuthority.isPresent()){
                newUser.getAuthorities().add(promoteAuthority.get());
                return this.userRepository.save(newUser);
            }
            else{
                throw new DataNotFoundException("Authority firstName " + authorityName + "not found");
            }
        }
        throw new DataNotFoundException("User id " + id + "not found");
    }

    @Override
    public User demoteUserById(Integer id, String authorityName) {
        Optional<User> user = Optional.ofNullable(this.findOneById(id));
        if(user.isPresent()){
            User newUser = user.get();
            newUser.getAuthorities().removeIf(authority -> authority.getName().equals(authorityName));
            return this.update(newUser);
        }
        throw new DataNotFoundException("User id " + id + "not found");
    }
}
