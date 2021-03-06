package com.arit.adserve.providers.ebay;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.repository.ItemRepository;
import com.arit.adserve.entity.service.LuceneSearchService;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing Camel route related to eBay processing. Route nodes responsible for
 * 'external' calls can be dynamically replaced by mocks
 * 
 * @author Alex Ryjoukhine
 * @since May 11, 2020
 * 
 */
@SpringBootTest
@ActiveProfiles("test")
public class EBayCamelTest {

	@Autowired
	protected CamelContext camelContext;
	@Autowired
	private ProducerTemplate template;
	@Autowired
    private ItemRepository itemRepository;
	@Autowired
	private LuceneSearchService searchService;
	
	private static Analyzer analyzer = new StandardAnalyzer();

	@Test
	public void vertxRouteTest() throws Exception {
		AdviceWithRouteBuilder.adviceWith(camelContext, EbayCamelService.ROUTE_VTX_EBAY_REQ_BRIDGE, a -> {
			a.replaceFromWith("direct:in");
			// weaveById() replace camel step id identified by id()
			a.weaveById("id_vertx_ebay_req_bridge_end").replace().log("replacing call to another route");
			// send the outgoing message to mock:out
			a.weaveAddLast().to("mock:out");
		});
		camelContext.start();
		MockEndpoint mockOut = camelContext.getEndpoint("mock:out", MockEndpoint.class);
		mockOut.expectedMessageCount(1);
		template.sendBody("direct:in", "test");
		mockOut.assertIsSatisfied();
	}

	@Test
	public void vertxGetImageTest() throws Exception {
		AdviceWithRouteBuilder.adviceWith(camelContext, EbayCamelService.ROUTE_GET_FILE_HTTP, a -> {
			a.replaceFromWith("direct:in1");
			// send the outgoing message to mock:out1
			a.weaveAddLast().to("mock:out1");
		});

		camelContext.start();
		MockEndpoint mockOut = camelContext.getEndpoint("mock:out1", MockEndpoint.class);
		mockOut.expectedMessageCount(1);
		template.sendBody("direct:in1", "test");
		mockOut.assertIsSatisfied();
	}

	@Test
	public void testEbayApiGetItems() throws Exception {
		itemRepository.deleteAll();
		searchService.deleteDocumentsAll(analyzer);
		AdviceWithRouteBuilder.adviceWith(camelContext, EbayCamelService.ROUTE_PROCESS_EBAY_ITEMS, a -> {
			a.weaveAddLast().to("mock:out2");
		});
		camelContext.start();
		MockEndpoint mockOut = camelContext.getEndpoint("mock:out2", MockEndpoint.class);
		mockOut.expectedMessageCount(20);
		for (int i = 0; i < 20; i++) {
			template.sendBody("direct:processItems", "init");
		}			
		mockOut.assertIsSatisfied();
	}
	
	@Test
	public void testEbayApiUpdateItems() throws Exception {
		itemRepository.deleteAll();
		searchService.deleteDocumentsAll(analyzer); 
		LocalDate localDate = LocalDate.now().minusDays(2);
		Date date = java.util.Date.from(localDate.atStartOfDay()
			      .atZone(ZoneId.systemDefault())
			      .toInstant());
		 Item item = new Item();
	        item.setProviderItemId("264713783843");
	        item.setTitle("title");
	        item.setProviderName(Constants.EBAY);
	        item.setViewItemURL("http://viewItemURL.com");
	        item.setUpdatedOn(date);
	        itemRepository.save(item);
	        Item item1 = new Item();
	        item1.setProviderItemId("102");
	        item1.setProviderName(Constants.EBAY);
	        item1.setTitle("title1");
	        item1.setViewItemURL("http://viewItemURL.com");
	        item1.setUpdatedOn(date);
	        itemRepository.save(item1);
	        
		AdviceWithRouteBuilder.adviceWith(camelContext, EbayCamelService.ROUTE_UPDATE_EBAY_ITEMS, a -> {
			a.weaveAddLast().to("mock:out3");
		});
		camelContext.start();
		MockEndpoint mockOut = camelContext.getEndpoint("mock:out3", MockEndpoint.class);
		mockOut.expectedMessageCount(1);
		template.sendBody("direct:remoteEbayApiUpdateItems", "init");
		mockOut.assertIsSatisfied();
		Iterable<Item> items = itemRepository.findAll();
		for (Item itemUpdated : items) {
			if (itemUpdated.getProviderItemId().equals("264713783843")) {
				assertFalse(itemUpdated.isDeleted());
			} else {
				assertTrue(itemUpdated.isDeleted());
			}
		}
	}
}
