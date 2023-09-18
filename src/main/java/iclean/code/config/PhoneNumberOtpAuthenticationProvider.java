package iclean.code.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class PhoneNumberOtpAuthenticationProvider implements AuthenticationProvider {
    private final Map<String, String> otpStorage = new HashMap<>();
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String phoneNumber = authentication.getName();
        String otp = authentication.getCredentials().toString();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String storedOtp = otpStorage.get(phoneNumber);

        if (storedOtp != null && passwordEncoder.matches(otp, storedOtp)) {
            // OTP is valid; create an authenticated Authentication object
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // Assign a role as needed
            User principal = new User(phoneNumber, "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, otp, authorities);
        } else {
            throw new UsernameNotFoundException("Invalid OTP for phone number: " + phoneNumber);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
