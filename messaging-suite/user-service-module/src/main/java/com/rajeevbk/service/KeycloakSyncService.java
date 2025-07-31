package com.rajeevbk.service;

import com.rajeevbk.entity.User;
import com.rajeevbk.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KeycloakSyncService implements ApplicationRunner {

    private final Keycloak keycloakAdminClient;
    private final UserRepository userRepository;

    @Value("${keycloak.realm}")
    private String realmName;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("******************* Running initial Keycloak user sync on startup... ******************************");
        syncKeycloakUsers();
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void scheduledSync() {
        syncKeycloakUsers();
    }

    @Transactional
    public void syncKeycloakUsers() {
        System.out.println("******************* 1 - Running Sync Job ******************************");
        RealmResource realmResource = keycloakAdminClient.realm(realmName);
        UsersResource usersResource = realmResource.users();

        int start = 0;
        int size = 100;
        List<UserRepresentation> keycloakUsers;
        List<UserRepresentation> allKeycloakUsers = new ArrayList<>();

        do {
            keycloakUsers = usersResource.list(start, size);
            allKeycloakUsers.addAll(keycloakUsers);
            start += size;
        } while (!keycloakUsers.isEmpty());

        int syncedCount = 0;

        System.out.println("fetched " + allKeycloakUsers.size() + " users.... updating.....");
        for (UserRepresentation kcUser : allKeycloakUsers) {
            try {
                // THIS IS THE FIX: Skip the user if they are missing either the
                // unique ID or the username, as both are critical.
                if (kcUser.getId() == null || kcUser.getUsername() == null || kcUser.getUsername().isBlank()) {
                    System.err.println("Skipping user with missing ID or Username.");
                    continue;
                }

                String userId = kcUser.getId();

                Optional<User> optionalUser = userRepository.findByKcUserId(userId);

                if (optionalUser.isPresent()) {
                    User managedUser = optionalUser.get();
                    managedUser.setUsername(kcUser.getUsername());
                    managedUser.setEmail(kcUser.getEmail());
                    managedUser.setFirstName(kcUser.getFirstName());
                    managedUser.setLastName(kcUser.getLastName());
                    managedUser.setIsActive(kcUser.isEnabled());
                    managedUser.setUpdatedAt(OffsetDateTime.now());
                } else {
                    User newUser = new User();
                    newUser.setKcUserId(userId);
                    newUser.setUsername(kcUser.getUsername());
                    newUser.setEmail(kcUser.getEmail());
                    newUser.setFirstName(kcUser.getFirstName());
                    newUser.setLastName(kcUser.getLastName());
                    newUser.setIsActive(kcUser.isEnabled());
                    newUser.setCreatedAt(OffsetDateTime.now());
                    newUser.setUpdatedAt(OffsetDateTime.now());
                    newUser.setRole("user");
                    newUser.setVersion(0L);
                    userRepository.save(newUser);
                    syncedCount++;
                }
            } catch (Exception e) {
                System.err.println("Error syncing Keycloak user: " + kcUser.getUsername() + ", ID: " + kcUser.getId());
                e.printStackTrace();
            }
        }
        System.out.println("Keycloak user sync completed. New users created: " + syncedCount);
    }

}