package com.eaton.platform.core.services.search.find.replace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.services.AdminService;

@ExtendWith(
		value = { MockitoExtension.class })
class AbstractFindReplaceFacadeTest {

	private final static String PAGE_TITLE = "Test1";

	private final static String PAGE_PATH = "/content/eaton/us/en-us/test1";

	private final static String PAGE_CONTENT_PATH = PAGE_PATH + "/" + JcrConstants.JCR_CONTENT;

	private final static String PAGE_2_TITLE = "Test2";

	private final static String PAGE_2_PATH = "/content/eaton/us/en-us/test2";

	private final static String PAGE_2_CONTENT_PATH = PAGE_2_PATH + "/" + JcrConstants.JCR_CONTENT;

	private final static String COMPONENT_TITLE = "Test3";

	private final static String COMPONENT_PATH = PAGE_2_CONTENT_PATH + "/root/responsivegrid/how_to_buy";

	@Captor
	private ArgumentCaptor<List<String>> captor;

	@Mock
	private ResourceResolver resourceResolver;

	@Mock
	private FindReplaceReplicationService replicationService;
	
	@Mock
	private FindReplaceNotificationService notificationService;

	@Mock
	private AdminService adminService;

	@Mock
	private PageManager pageManager;

	@Mock
	private Page mockPage1;

	@Mock
	private ValueMap properties1;

	@Mock
	private Page mockPage2;

	@Mock
	private ValueMap properties2;

	private AbstractFindReplaceFacade facade;

	@BeforeEach
	public void setUp() throws WCMException {
		when(this.properties2.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION)).thenReturn("Activate");
		when(this.mockPage1.getPath()).thenReturn(PAGE_CONTENT_PATH);
		when(this.mockPage2.getPath()).thenReturn(PAGE_2_CONTENT_PATH);
		when(this.mockPage2.getLastModified()).thenReturn(GregorianCalendar.getInstance());
		when(this.mockPage2.getProperties()).thenReturn(this.properties2);

		when(this.pageManager.getContainingPage(PAGE_CONTENT_PATH)).thenReturn(this.mockPage1);
		when(this.pageManager.getContainingPage(COMPONENT_PATH)).thenReturn(this.mockPage2);

		when(this.adminService.getReadService()).thenReturn(this.resourceResolver);
		when(this.resourceResolver.adaptTo(PageManager.class)).thenReturn(this.pageManager);

		this.facade = Mockito.mock(AbstractFindReplaceFacade.class, Mockito.CALLS_REAL_METHODS);
		when(this.facade.getAdminService()).thenReturn(this.adminService);
		when(this.facade.getReplicationService()).thenReturn(this.replicationService);
		when(this.facade.getNotificationService()).thenReturn(this.notificationService);
	}

	/**
	 * Test if 2 resources will be activated.
	 */
	@Test
	@DisplayName("Test activation of 2 pages. Both pages haven't been modified after replication.")
	void testRepliacteOrNotify() {
		when(this.properties1.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION)).thenReturn("Activate");

		final Calendar lastReplicated = GregorianCalendar.getInstance();
		lastReplicated.add(Calendar.HOUR, 1);
		when(this.properties1.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED)).thenReturn(lastReplicated);
		when(this.properties2.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED)).thenReturn(lastReplicated);

		when(this.mockPage1.getLastModified()).thenReturn(GregorianCalendar.getInstance());
		when(this.mockPage1.getProperties()).thenReturn(this.properties1);

		this.facade.replicateOrNotify(this.getdResultItems(), null);
		Mockito.verify(this.replicationService).replicate(this.captor.capture());
		final List<String> capturedItems = this.captor.getValue();

		assertEquals(2, capturedItems.size(), "Number of pages to be replicated should be 2");
	}

	/**
	 * Test if 1 resource will be activated.
	 */
	@Test
	@DisplayName("Test activation of 1 page. The other page was modified after replication.")
	void testRepliacteOrNotify2() {
		final Calendar pastDate = GregorianCalendar.getInstance();
		pastDate.set(GregorianCalendar.YEAR, 2020);
		when(this.properties1.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION)).thenReturn("Activate");
		when(this.properties1.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED)).thenReturn(pastDate);
		when(this.properties2.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED)).thenReturn(GregorianCalendar.getInstance());
		when(this.mockPage1.getLastModified()).thenReturn(GregorianCalendar.getInstance());
		when(this.mockPage1.getProperties()).thenReturn(this.properties1);

		this.facade.replicateOrNotify(this.getdResultItems(), null);
		Mockito.verify(this.replicationService).replicate(this.captor.capture());
		final List<String> capturedItems = this.captor.getValue();

		assertEquals(1, capturedItems.size(), "Number of pages to be replicated should be 1");
	}

	/**
	 * Test if we skip activation of deactivated page.
	 */
	@Test
	@DisplayName("Test activation of 1 page. The other page was deactivated.")
	void testRepliacteOrNotify3() {
		when(this.properties1.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION)).thenReturn("Deactivate");
		when(this.properties1.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED)).thenReturn(GregorianCalendar.getInstance());
		when(this.properties2.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED)).thenReturn(GregorianCalendar.getInstance());
		when(this.mockPage1.getLastModified()).thenReturn(GregorianCalendar.getInstance());
		when(this.mockPage1.getProperties()).thenReturn(this.properties1);

		this.facade.replicateOrNotify(this.getdResultItems(), null);
		Mockito.verify(this.replicationService).replicate(this.captor.capture());
		final List<String> capturedItems = this.captor.getValue();

		assertEquals(1, capturedItems.size(), "Number of pages to be replicated should be 1");
	}

	/**
	 * Test if we skip activation of page under language-masters.
	 */
	@Test
	@DisplayName("Test activation of 1 page. The other page is from language-masters tree.")
	void testRepliacteOrNotify4() {
		when(this.properties2.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED)).thenReturn(GregorianCalendar.getInstance());
		when(this.mockPage1.getPath()).thenReturn("/content/eaton/language-masters/en-us/test1");

		this.facade.replicateOrNotify(this.getdResultItems(), null);
		Mockito.verify(this.replicationService).replicate(this.captor.capture());
		final List<String> capturedItems = this.captor.getValue();

		assertEquals(1, capturedItems.size(), "Number of pages to be replicated should be 1");
	}

	private List<ResultItem> getdResultItems() {
		return Arrays.asList(
				ResultItem.builder().withContentType(ContentType.PAGE).withTopContainerPath(PAGE_PATH)
						.withTopContainerTitle(PAGE_TITLE).withContainerPath(PAGE_PATH).withContainerTitle(PAGE_TITLE)
						.withPath(PAGE_CONTENT_PATH).withTitle(PAGE_TITLE).build(),

				ResultItem.builder().withContentType(ContentType.COMPONENT).withTopContainerPath(PAGE_2_PATH)
						.withTopContainerTitle(PAGE_2_TITLE).withContainerPath(COMPONENT_PATH).withContainerTitle(COMPONENT_TITLE)
						.withPath(COMPONENT_PATH).withTitle(COMPONENT_TITLE).build());
	}
}
