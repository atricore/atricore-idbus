package com.atricore.idbus.console.services.test;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;
import org.dozer.DozerBeanMapper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class DTOMappingTest {

    private IdentityAppliance appliance ;

    private static ApplicationContext applicationContext;

    @BeforeClass
    public static void setupTestSuite() {
        applicationContext = new ClassPathXmlApplicationContext("com/atricore/idbus/console/services/test/dto-mapping-test-beans.xml");
    }

    @Before
    public void setupTestCase() {

        ApplicationContext appCtx =
                new ClassPathXmlApplicationContext("com/atricore/idbus/console/services/test/appliance-model.xml");

        Map<String, IdentityAppliance> appliances = appCtx.getBeansOfType(IdentityAppliance.class);
        assert appliances.size() == 1 : "One and only one appliance should be defined, found " + appliances.size();

        appliance = appliances.values().iterator().next();
    }

    @AfterClass
    public static void tearDownTestSuite() {

    }

    @Before
    public void tearDownTestCase() {

    }

    @Test
    public void testApplianceToDTOMapping() {
        DozerBeanMapper dozerMapper = (DozerBeanMapper) applicationContext.getBean("dozerMapper");

        IdentityApplianceDTO applianceDTO = new IdentityApplianceDTO ();
        dozerMapper.map(appliance, applianceDTO);
        assertEquivalent(appliance, applianceDTO);
    }

    @Test
    public void testDTOToApplianceMapping() {
        DozerBeanMapper dozerMapper = (DozerBeanMapper) applicationContext.getBean("dozerMapper");

        IdentityApplianceDTO applianceDTO = new IdentityApplianceDTO ();
        dozerMapper.map(appliance, applianceDTO);
        assertEquivalent(appliance, applianceDTO);

        IdentityAppliance appliance = new IdentityAppliance();
        dozerMapper.map(applianceDTO, appliance);
        assertEquivalent(appliance, applianceDTO);
    }


    protected void assertEquivalent(IdentityAppliance model, IdentityApplianceDTO dto ) {
        // TODO ! Implement me
    }

}
