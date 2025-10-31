package dev.sharkbox.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockSharkboxUserSecurityContextFactory.class)
public @interface WithSharkboxUser {
    String userId() default "test-user-uuid";
    String username() default "test";
    String[] roles() default {};
    String givenName() default "Test";
    String familyName() default "Tester";
    String emailAddress() default "test@test.test";
}
