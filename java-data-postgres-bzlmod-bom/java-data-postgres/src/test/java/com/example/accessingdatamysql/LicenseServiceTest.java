package com.example.accessingdatamysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.MessageSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LicenseServiceTest {

  @InjectMocks
  private LicenseService licenseService;

  @Mock
  private LicenseRepository licenseRepository;

  @Mock
  private MessageSource messages;

  private AutoCloseable closeable;

  @BeforeEach
  void setup() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetLicense_Found() {
    License license = new License();
    license.setLicenseId("123");
    license.setOrganizationId("org1");

    when(licenseRepository.findByOrganizationIdAndLicenseId("org1", "123")).thenReturn(license);

    License result = licenseService.getLicense("123", "org1", "someClient");
    assertEquals("123", result.getLicenseId());
    assertEquals("org1", result.getOrganizationId());
  }

  @Test
  void testGetLicense_NotFound() {
    when(licenseRepository.findByOrganizationIdAndLicenseId("org1", "not-found")).thenReturn(null);
    when(messages.getMessage(eq("license.search.error.message"), any(), any()))
            .thenReturn("License %s for organization %s not found");

    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      licenseService.getLicense("not-found", "org1", "someClient");
    });

    assertTrue(thrown.getMessage().contains("License not-found for organization org1 not found"));
  }

  @Test
  void testCreateLicense() {
    License input = new License();
    License saved = new License();
    saved.setLicenseId(UUID.randomUUID().toString());

    when(licenseRepository.save(any())).thenReturn(saved);

    License result = licenseService.createLicense(input);
    assertNotNull(result.getLicenseId());
    verify(licenseRepository, times(1)).save(any());
  }

  @Test
  void testUpdateLicense() {
    License license = new License();
    license.setLicenseId("456");

    when(licenseRepository.save(license)).thenReturn(license);

    License updated = licenseService.updateLicense(license);
    assertEquals("456", updated.getLicenseId());
  }

  @Test
  void testDeleteLicense() {
    when(messages.getMessage(eq("license.delete.message"), any(), any()))
            .thenReturn("License %s deleted");

    String result = licenseService.deleteLicense("789");
    assertTrue(result.contains("789"));
    verify(licenseRepository, times(1)).delete(any());
  }

  @Test
  void testGetLicensesByOrganization() throws Exception {
    List<License> licenses = new ArrayList<>();
    License l = new License();
    l.setLicenseId("abc");
    l.setOrganizationId("orgX");
    licenses.add(l);

    // sleep() may delay tests unpredictably; consider mocking randomlyRunLong() or skip it in tests.
    doReturn(licenses).when(licenseRepository).findByOrganizationId("orgX");

    List<License> result = licenseService.getLicensesByOrganization("orgX");
    assertEquals(1, result.size());
    assertEquals("abc", result.get(0).getLicenseId());
  }

  @Test
  void testBuildFallbackLicenseList() {
    List<License> result = invokeFallback("org-fail", new RuntimeException("test"));

    assertEquals(1, result.size());
    assertEquals("0000000-00-00000", result.get(0).getLicenseId());
    assertEquals("org-fail", result.get(0).getOrganizationId());
  }

  // Used Java reflection for private methods for test coverage.
  private List<License> invokeFallback(String orgId, Throwable t) {
    try {
      var method = LicenseService.class.getDeclaredMethod("buildFallbackLicenseList", String.class, Throwable.class);
      method.setAccessible(true);
      return (List<License>) method.invoke(licenseService, orgId, t);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
