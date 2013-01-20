/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade.test;

import home.assetracker.data.entity.Asset;
import home.assetracker.data.facade.AssetFacade;
import home.assetracker.data.facade.AssetFacadeLocal;
import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author kenmin
 */
@RunWith(Arquillian.class)
public class AssetFacadeTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage(AssetFacade.class.getPackage())
                .addPackage(Asset.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    @Inject
    AssetFacadeLocal assetFacade;

    private void shouldBeEmpty() {
        List<Asset> results = assetFacade.findAll();
        if (results != null) {
            Assert.assertEquals(0, results.size());
        }
    }

    private void insertRecords() {
    }

    @Test
    public void dummyTest() {
        // Do nothing
    }
}
