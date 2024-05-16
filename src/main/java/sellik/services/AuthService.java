package sellik.services;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sellik.entities.UserEntity;
import sellik.models.UserDetailsImpl;
import sellik.models.UserLoginModel;
import sellik.models.UserRegistrationModel;
import sellik.repositories.RoleRepository;
import sellik.repositories.UserRepository;
import sellik.utils.JwtUtil;
import sellik.utils.TokenPair;

import java.util.List;

@Service
public class AuthService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public TokenPair loginUser(UserLoginModel loginModel) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginModel.getUsername(), loginModel.getPassword()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String subject = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
        String jwtToken = jwtUtil.generateToken(subject);
        String jwtRefreshToken = jwtUtil.generateRefreshToken(subject);
        TokenPair tokenPair = new TokenPair(jwtToken, jwtRefreshToken);
        return tokenPair;
    }

    public String registerUser(UserRegistrationModel registrationModel) throws BadRequestException {
        if (!registrationModel.getPassword().equals(registrationModel.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }
        if (userRepository.findUserEntityByUsername(registrationModel.getUsername()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The user with such username already exists");
        }
        if (userRepository.findUserEntityByEmail(registrationModel.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The user with such email already exists");
        }
        createNewUser(registrationModel);
        return "Successful registration";
    }

    public void createNewUser(UserRegistrationModel registrationModel) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(registrationModel.getUsername());
        userEntity.setPassword(passwordEncoder.encode(registrationModel.getPassword()));
        userEntity.setEmail(registrationModel.getEmail());
        userEntity.setRoles(List.of(roleRepository.findRoleEntityByName("ROLE_USER")));
        userRepository.save(userEntity);
    }
}
