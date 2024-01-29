package com.eaton.platform.core.jobs.consumers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.bean.file.purge.FilePurgeConfigBean;
import com.eaton.platform.core.bean.file.purge.FilePurgeConfigBean.Unit;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.file.purge.FilePurgeConfigService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.NonClosableResourceResolverWrapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class FilePurgeConsumerImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class FilePurgeConsumerImplTest {

	private final String ROOT_REPORTS = "/tmp/eaton/msm/conflict/reports";

	private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

	@Mock
	private AdminService adminService;

	@Mock
	private FilePurgeConfigService configService;

	@Mock
	private Job job;

	@InjectMocks
	private final FilePurgeConsumerImpl classUnderTest = new FilePurgeConsumerImpl();

	/**
	 * Test that file is deleted correctly.
	 *
	 * @throws RepositoryException
	 */
	@Test
	@DisplayName("File is deleted correctly")
	void testFileIsDeletedCorrectly() throws RepositoryException {
		// set up
		final Resource root = this.context.create().resource(this.ROOT_REPORTS);
		final Resource oldReport = this.context.create().resource(root, "msm_conflict_report_2023-5-11.csv", Map.of(JcrConstants.JCR_PRIMARYTYPE,
				JcrConstants.NT_FILE));
		// set jcr:created date
		CommonUtil.adapt(oldReport, Node.class).setProperty(JcrConstants.JCR_CREATED,
				GregorianCalendar.from(ZonedDateTime.of(2023, 5, 11, 0, 0, 0, 0, ZoneId.of("UTC"))));
		this.context.create().resource(root, "msm_conflict_report_2023-11-14.csv", Map.of(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE));

		when(this.adminService.getWriteService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));
		when(this.configService.getConfig()).thenReturn(new FilePurgeConfigBean(true, 30, this.ROOT_REPORTS, Unit.DAY));

		this.classUnderTest.bindService(this.configService);

		// exercise
		this.classUnderTest.process(this.job);

		// verify
		assertThat("Resource should be deleted", this.context.resourceResolver().getResource(this.ROOT_REPORTS + "/msm_conflict_report_2023-5-11.csv"),
				is(nullValue()));
		assertThat("Resource should be present", this.context.resourceResolver().getResource(this.ROOT_REPORTS + "/msm_conflict_report_2023-11-14.csv"),
				is(notNullValue()));

	}
}
