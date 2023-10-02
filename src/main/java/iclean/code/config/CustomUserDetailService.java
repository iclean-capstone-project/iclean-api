package iclean.code.config;

import iclean.code.data.domain.User;
import iclean.code.data.dto.response.authen.UserPrinciple;
import iclean.code.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Cacheable(value = "userDetails")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        return UserPrinciple.build(user);
    }

    public UserDetails loadUserById(Integer id) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(id);
        return UserPrinciple.build(user);
    }
}
