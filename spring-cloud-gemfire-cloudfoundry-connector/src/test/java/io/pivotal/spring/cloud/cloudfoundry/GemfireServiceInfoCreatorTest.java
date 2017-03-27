package io.pivotal.spring.cloud.cloudfoundry;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.spring.cloud.service.common.GemfireServiceInfo;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cloud.cloudfoundry.AbstractCloudFoundryConnectorTest;

public class GemfireServiceInfoCreatorTest extends AbstractCloudFoundryConnectorTest {

	private ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testInfoCreator() throws Exception {
		GemfireServiceInfoCreator creator = new GemfireServiceInfoCreator();
		Map services = readServiceData("test-gemfire-service.json");
		Map<String, Object> serviceData = getServiceData(services, "p-gemfire");

		GemfireServiceInfo info = creator.createServiceInfo(serviceData);
		Assert.assertNotNull(info);
		Assert.assertNotNull(info.getLocators());
		Assert.assertNotNull(info.getDevUsername());
		Assert.assertNotNull(info.getDevPassword());
		Assert.assertNull(info.getRestURL());
	}

	@Test
	public void testAcceptService() throws Exception {
		GemfireServiceInfoCreator creator = new GemfireServiceInfoCreator();
		Map services = readServiceData("test-gemfire-service.json");
		Map<String, Object> serviceData = getServiceData(services, "p-gemfire");
		boolean accepts = creator.accept(serviceData);
		Assert.assertEquals(true, accepts);
	}

	@Test
	public void testInfoCreatorCups() throws Exception {
		GemfireServiceInfoCreator creator = new GemfireServiceInfoCreator();
		Map services = readServiceData("test-gemfire-userprovided.json");
		Map<String, Object> serviceData = getServiceData(services, "user-provided");

		GemfireServiceInfo info = creator.createServiceInfo(serviceData);
		Assert.assertNotNull(info);
	}

	@Test
	public void testAcceptCups() throws Exception {
		GemfireServiceInfoCreator creator = new GemfireServiceInfoCreator();
		Map services = readServiceData("test-gemfire-userprovided.json");
		Map<String, Object> serviceData = getServiceData(services, "user-provided");
		boolean accepts = creator.accept(serviceData);
		Assert.assertEquals(true, accepts);
	}

	@Test
	public void testRestUrl() throws Exception {
		GemfireServiceInfoCreator creator = new GemfireServiceInfoCreator();
		Map services = readServiceData("test-gemfire-rest-service.json");
		Map<String, Object> serviceData = getServiceData(services, "p-gemfire");
		GemfireServiceInfo info = creator.createServiceInfo(serviceData);
		Assert.assertEquals("gemfire-si.foo.bar.com", info.getRestURL());
	}

	@Test
	public void testGemFireServiceOnDemand() throws Exception{
		GemfireServiceInfoCreator creator = new GemfireServiceInfoCreator();
		Map servicesJsonMap = readServiceData("test-gemfire20-service.json");
		Map<String, Object> serviceData = getServiceData(servicesJsonMap, "p-gemfire-on-demand");
		GemfireServiceInfo info = creator.createServiceInfo(serviceData);
		Assert.assertThat("locator://10.0.8.20:55221", Matchers.containsString(info.getLocators()[0].toString()));
	}

	@Test
	public void testLocatorsCloudCache() throws Exception {
		GemfireServiceInfoCreator creator = new GemfireServiceInfoCreator();
		Map servicesJsonMap = readServiceData("test-cloudcache-service.json");
		Map<String, Object> serviceData = getServiceData(servicesJsonMap, "p-cloudcache");
		GemfireServiceInfo info = creator.createServiceInfo(serviceData);
		Assert.assertThat("locator://10.0.8.20:55221", Matchers.containsString(info.getLocators()[0].toString()));
		Assert.assertThat("developer", Matchers.equalTo(info.getDevUsername()));
		Assert.assertThat("crcL7ULsVIgRP93X7Wdg", Matchers.equalTo(info.getDevPassword()));
	}

	private Map readServiceData(String resource) throws java.io.IOException {
		return mapper.readValue(readTestDataFile(resource), Map.class);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getServiceData(Map services, String name) {
		return (Map<String, Object>) ((List) services.get(name)).get(0);
	}
}
