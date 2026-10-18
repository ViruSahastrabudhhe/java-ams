package com.dem_ams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class DemAmsApplication {

	/*
		TODO: Core API
			- Property & PropertyDetail -> Unit -> Lease -> Tenant
				 							|
				 							|
 											-> MaintenanceRequest / Complaint
			- Filtering and field selection
			- Database indexing
			- API versioning

		TODO: Features
			- Request form validation with Hibernate Validation
			- Caching with Redis
			- (2-in-1 deal) Batch processing of scheduled tasks e.g.
					updating of expired leases' status, invoice generation
			- Asynchronous job handling with RabbitMQ
			- Audit trail
			- File storage
			- OpenAPI spec
			- Observability and health monitoring
			- WebSockets for two-way communication with Tenant and Landlord
			- Rate Limiting with Bucket4j
			- JWT / OAuth 2.0 implementation
			- Containerization with Docker
			- Deploying to a cloud environment
			- React frontend mimicking Google Classroom UI/UX
	 */

	public static void main(String[] args) {
        SpringApplication.run(DemAmsApplication.class, args);
	}

}
