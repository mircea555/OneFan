package com.onefan.backend;

import com.onefan.backend.model.Role;
import com.onefan.backend.model.RoleName;
import com.onefan.backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OneFanApplication {

	public static void main(String[] args) {
		SpringApplication.run(OneFanApplication.class, args);
	}

	@Bean
	CommandLineRunner initRoles(RoleRepository roleRepository) {
		return args -> {
			if (!roleRepository.findByName(RoleName.ROLE_ADMIN).isPresent()) {
				roleRepository.save(new Role(RoleName.ROLE_ADMIN));
			}
			if (!roleRepository.findByName(RoleName.ROLE_SELLER).isPresent()) {
				roleRepository.save(new Role(RoleName.ROLE_SELLER));
			}
			if (!roleRepository.findByName(RoleName.ROLE_BUYER).isPresent()) {
				roleRepository.save(new Role(RoleName.ROLE_BUYER));
			}
		};
	}
}
