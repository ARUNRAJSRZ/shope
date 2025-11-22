package com.srz.shope;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// no extra beans here

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class ShopeApplication {

	private static final Logger logger = LoggerFactory.getLogger(ShopeApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ShopeApplication.class, args);
	}

	@Autowired(required = false)
	private DataSource dataSource;

	@PostConstruct
	public void testDbConnection() {
		if (dataSource == null) {
			logger.info("DataSource not configured; skipping DB connection test.");
			return;
		}
		try (Connection conn = dataSource.getConnection()) {
			logger.info("Successfully connected to database: {}", conn.getMetaData().getURL());
		} catch (Exception e) {
			logger.error("Unable to obtain DB connection on startup:", e);
		}
	}

}
