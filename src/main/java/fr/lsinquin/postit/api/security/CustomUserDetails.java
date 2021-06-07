package fr.lsinquin.postit.api.security;

import fr.lsinquin.postit.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * Custom implementation of {@link org.springframework.security.core.userdetails.UserDetails UserDetails}.
 * Since There is no role management in the application. All users have the same predefined role : ROLE_USER
 */

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;
    private final String password;
    private final String username;
    private final Set<GrantedAuthority> authorities;
    private final boolean isAccountNonExpired;
    private final boolean isAccountNonLocked;
    private final boolean areCredentialsNonExpired;
    private final boolean isEnabled;

    public CustomUserDetails(User user) {
        this(user, user.getPassword(), user.getMail(), Set.of(new SimpleGrantedAuthority("ROLE_USER")), true, true, true, true);
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return areCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
