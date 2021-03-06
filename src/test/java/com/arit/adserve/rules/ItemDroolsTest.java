package com.arit.adserve.rules;

import com.arit.adserve.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class ItemDroolsTest {

	private static KieSession kSession;
	private static StatelessKieSession kStatelessSession;
	 
    @BeforeAll
    public static void setup() throws IOException {        
        kSession = new DroolsConfig().getKieSession();
        kStatelessSession = new DroolsConfig().getKieStatlessSession();
    }
 
    @Test
    public void testEbayItem() throws Exception{
        Item item = new Item();
        item.setPrice(10);
        item.setCondition("Used");
        FactHandle fhItem = kSession.insert(item); 
       
        kSession.fireAllRules(); 
        log.info("fact count: {}", kSession.getFactCount());
        kSession.delete(fhItem);
        log.info("fact count: {}", kSession.getFactCount());
        assertTrue(item.isProcess());
        kSession.dispose();   
        
        //retrieved all required items, updating
        kSession = new DroolsConfig().getKieSession();
        item.setCondition("New");
        kSession.insert(item);      
        log.info("fact count: {}", kSession.getFactCount());
        kSession.fireAllRules(); 
        assertFalse(item.isProcess());
    }    
  
}
