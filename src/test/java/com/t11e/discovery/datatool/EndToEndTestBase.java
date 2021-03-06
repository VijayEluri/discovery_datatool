package com.t11e.discovery.datatool;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    locations = {"applicationContext-test.xml"})
public abstract class EndToEndTestBase
{
  private final Logger sqlLogger = Logger.getLogger(getClass().getName() + ".SQL");
  @Autowired
  protected ChangesetController changesetController;
  @Autowired
  protected ConfigurationManager configurationManager;
  protected NamedParameterJdbcOperations template;

  @Before
  public final void setUp()
  {
    configurationManager.loadConfiguration(getConfigurationXml(), false);
    template = LoggingNamedParameterJdbcTemplate
      .create(configurationManager.getBean(DataSource.class), sqlLogger, Level.FINEST);
    executeSqlScripts(getSetupScripts());
    if (shouldValidateSql())
    {
      configurationManager.checkValid();
    }
  }

  protected boolean shouldValidateSql()
  {
    return true;
  }

  @After
  public final void tearDown()
  {
    executeSqlScripts(getCleanupScripts());
  }

  protected InputStream getConfigurationXml()
  {
    return getClass().getResourceAsStream(getClass().getSimpleName() + ".xml");
  }

  protected String[] getCleanupScripts()
  {
    return getDefaultCleanupScripts();
  }

  protected String[] getSetupScripts()
  {
    return getDefaultSetupScripts();
  }

  protected final String[] getDefaultSetupScripts()
  {
    return new String[]{getDefaultSetupScript()};
  }

  protected final String getDefaultSetupScript()
  {
    return getClass().getSimpleName() + "Create.sql";
  }

  protected String[] getDefaultCleanupScripts()
  {
    return new String[]{getDefaultCleanupScript()};
  }

  protected final String getDefaultCleanupScript()
  {
    return getClass().getSimpleName() + "Drop.sql";
  }

  protected void executeSqlScripts(final String... scriptNames)
  {
    final DataSource dataSource = configurationManager.getBean(DataSource.class);
    final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    final Resource[] resources = new Resource[scriptNames.length];
    for (int i = 0; i < scriptNames.length; i++)
    {
      resources[i] = new ClassPathResource(scriptNames[i], getClass());
    }
    populator.setScripts(resources);
    try
    {
      final Connection connection = dataSource.getConnection();
      populator.populate(connection);
      connection.close();
    }
    catch (final SQLException e)
    {
      throw new RuntimeException(e);
    }
  }

  protected List<String> nodesAsStrings(final Node doc, final String xpath)
  {
    final List<String> result = new ArrayList<String>();
    for (final Object node : doc.selectNodes(xpath))
    {
      result.add(((Node) node).getText());
    }
    return result;
  }

  protected Document parseXmlResponse(final MockHttpServletResponse response)
    throws DocumentException
  {
    final SAXReader saxReader = new SAXReader();
    return saxReader.read(new ByteArrayInputStream(response.getContentAsByteArray()));
  }

  protected Document assertChangeset(final String publisher, final String profile, final String expectedType,
    final Collection<String> expectedSetItemIds, final Collection<String> expectedRemoveItemIds,
    final boolean forceSnapshot)
  {
    return assertChangeset(publisher, profile, expectedType, expectedSetItemIds, expectedRemoveItemIds,
      Collections.<String> emptyList(), forceSnapshot);
  }

  protected Document assertChangeset(final String publisher, final String profile, final String expectedType,
    final Collection<String> expectedSetItemIds, final Collection<String> expectedRemoveItemIds,
    final Collection<String> expectedAddToItemIds,
    final boolean forceSnapshot)
  {
    final Document doc = assertChangeset(publisher, profile, expectedType, forceSnapshot);
    final String asXml = doc.asXML();
    Assert.assertEquals(asXml, expectedAddToItemIds.size(), doc.selectNodes("/changeset/add-to-item").size());
    Assert.assertEquals(asXml,
      new HashSet<String>(expectedAddToItemIds),
      new HashSet<String>(nodesAsStrings(doc, "/changeset/add-to-item/@id")));
    Assert.assertEquals(asXml, expectedSetItemIds.size(), doc.selectNodes("/changeset/set-item").size());
    Assert.assertEquals(asXml,
      new HashSet<String>(expectedSetItemIds),
      new HashSet<String>(nodesAsStrings(doc, "/changeset/set-item/@id")));
    Assert.assertEquals(asXml, expectedRemoveItemIds.size(), doc.selectNodes("/changeset/remove-item").size());
    Assert.assertEquals(asXml,
      new HashSet<String>(expectedRemoveItemIds),
      new HashSet<String>(nodesAsStrings(doc, "/changeset/remove-item/@id")));
    return doc;
  }

  protected Document assertChangeset(final String publisher, final String profile, final String expectedType,
    final boolean forceSnapshot)
  {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    try
    {
      changesetController.publish(request, response,
        publisher, null, null, profile, false, forceSnapshot);
    }
    catch (final RuntimeException e)
    {
      throw e;
    }
    catch (final Exception e)
    {
      throw new RuntimeException(e);
    }
    Assert.assertEquals(200, response.getStatus());
    Assert.assertEquals("text/xml; charset=utf-8", response.getContentType());
    Assert.assertEquals(expectedType, response.getHeader("X-t11e-type"));
    Document doc;
    try
    {
      doc = parseXmlResponse(response);
    }
    catch (final DocumentException e)
    {
      throw new RuntimeException(e);
    }
    return doc;
  }

  protected void assertXpath(final String expectedValue, final Node node, final String xpath)
  {
    Object actual = node.selectObject(xpath);
    if (actual instanceof Node)
    {
      actual = ((Node) actual).getText();
    }
    Assert.assertEquals(expectedValue, actual);
  }

  protected void assertXpath(final List<String> expectedValues, final Node node, final String xpath)
  {
    Assert.assertEquals(expectedValues, nodesAsStrings(node, xpath));
  }

}
