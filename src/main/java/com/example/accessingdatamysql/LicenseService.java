package com.example.accessingdatamysql;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class LicenseService {

	@Autowired
	MessageSource messages;

	@Autowired
	private LicenseRepository licenseRepository;

	private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);

	public License getLicense(String licenseId, String organizationId, String clientType){
		License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
		if (null == license) {
			String message = String.format(messages.getMessage("license.search.error.message", null, null),licenseId, organizationId);
			logger.error(message);
			throw new IllegalArgumentException(message);	
		}
		logger.debug("Retrieving license information: " + license.toString());
		return license;
	}

	public License createLicense(License license){
		license.setLicenseId(UUID.randomUUID().toString());
		licenseRepository.save(license);
		return license;
	}

	public License updateLicense(License license){
		licenseRepository.save(license);
		return license;
	}

	public String deleteLicense(String licenseId){
		String responseMessage = null;
		License license = new License();
		license.setLicenseId(licenseId);
		licenseRepository.delete(license);
		responseMessage = String.format(messages.getMessage("license.delete.message", null, null),licenseId);
		logger.debug("Deleting license : " + responseMessage);
		return responseMessage;

	}

//	@CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
//	@RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
//	@Retry(name = "retryLicenseService", fallbackMethod = "buildFallbackLicenseList")
//	@Bulkhead(name = "bulkheadLicenseService", type= Type.THREADPOOL, fallbackMethod = "buildFallbackLicenseList")
	public List<License> getLicensesByOrganization(String organizationId) throws TimeoutException {
//		 logger.debug("getLicensesByOrganization Correlation id: {}",
//			UserContextHolder.getContext().getCorrelationId());
		randomlyRunLong();
		return licenseRepository.findByOrganizationId(organizationId);
	}

	@SuppressWarnings("unused")
	private List<License> buildFallbackLicenseList(String organizationId, Throwable t){
		List<License> fallbackList = new ArrayList<>();
		License license = new License();
		license.setLicenseId("0000000-00-00000");
		license.setOrganizationId(organizationId);
		license.setProductName("Sorry no licensing information currently available");
		fallbackList.add(license);
		return fallbackList;
	}

	private void randomlyRunLong(){
		Random rand = new Random();
		int randomNum = rand.nextInt((3 - 1) + 1) + 1;
		if (randomNum==3) sleep();
	}
	private void sleep(){
		try {
			Thread.sleep(11000);
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
	}
}
