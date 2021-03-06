package com.t11e.discovery.datatool;

import java.util.Arrays;

import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;

public class SubqueryTest
  extends EndToEndTestBase
{
  @Test
  public void testSnapshotWithVarcharSubquery()
  {
    final Document doc = assertChangeset("test-snapshot-varchar", "", "snapshot",
      Arrays.asList("1", "2", "3"),
      Arrays.asList("4", "5"),
      false);

    Assert.assertEquals("red",
      doc.selectSingleNode("/changeset/set-item[@id='1']/properties/struct/entry[@name='color']/string/text()")
        .getText());
    Assert.assertEquals("orange,yellow",
      doc.selectSingleNode("/changeset/set-item[@id='2']/properties/struct/entry[@name='color']/string/text()")
        .getText());
    Assert.assertNull(doc.selectSingleNode("/changeset/set-item[@id='3']/properties/struct/entry[@name='color']"));

    Assert.assertEquals(
      "red",
      doc.selectSingleNode(
        "/changeset/set-item[@id='1']/properties/struct/entry[@name='color-delimited']/string/text()")
        .getText());
    Assert.assertEquals(
      "orange|yellow",
      doc.selectSingleNode(
        "/changeset/set-item[@id='2']/properties/struct/entry[@name='color-delimited']/string/text()")
        .getText());
    Assert.assertNull(doc
      .selectSingleNode("/changeset/set-item[@id='3']/properties/struct/entry[@name='color-delimited']"));

    assertColorArraySubquery(doc, "color-default");
    assertColorArraySubquery(doc, "color-array");

  }

  private void assertColorArraySubquery(final Document doc, final String propertyName)
  {
    Assert.assertEquals("red",
      doc.selectSingleNode(
        "/changeset/set-item[@id='1']/properties/struct/entry[@name='" + propertyName + "']/string/text()")
        .getText());
    Assert.assertEquals(2,
      doc.selectNodes(
        "/changeset/set-item[@id='2']/properties/struct/entry[@name='" + propertyName + "']/array/element/string/text()")
        .size());
    Assert.assertEquals(
      Arrays.asList("orange", "yellow"),
      nodesAsStrings(doc,
        "/changeset/set-item[@id='2']/properties/struct/entry[@name='" + propertyName + "']/array/element/string/text()"));
    Assert.assertNull(doc
      .selectSingleNode("/changeset/set-item[@id='3']/properties/struct/entry[@name='" + propertyName + "']"));
  }

  @Test
  public void testSnapshotWithIntSubquery()
  {
    final Document doc = assertChangeset("test-snapshot-int", "", "snapshot",
      Arrays.asList("1", "2", "3"),
      Arrays.asList("4", "5"),
      false);
    Assert.assertEquals("10",
      doc.selectSingleNode("/changeset/set-item[@id='1']/properties/struct/entry[@name='color']/string/text()")
        .getText());
    Assert.assertEquals("20,30",
      doc.selectSingleNode("/changeset/set-item[@id='2']/properties/struct/entry[@name='color']/string/text()")
        .getText());
    Assert.assertNull(doc.selectSingleNode("/changeset/set-item[@id='3']/properties/struct/entry[@name='color']"));
  }

  @Test
  public void testSnapshotWithTimestampSubquery()
  {
    final Document doc = assertChangeset("test-snapshot-timestamp", "", "snapshot",
      Arrays.asList("1", "2", "3"),
      Arrays.asList("4", "5"),
      false);
    Assert.assertEquals("2011-01-01T00:00:00.000",
      doc.selectSingleNode("/changeset/set-item[@id='1']/properties/struct/entry[@name='color']/string/text()")
        .getText());
    Assert.assertEquals("2011-01-02T00:00:00.000,2011-01-03T00:00:00.000",
      doc.selectSingleNode("/changeset/set-item[@id='2']/properties/struct/entry[@name='color']/string/text()")
        .getText());
    Assert.assertNull(doc.selectSingleNode("/changeset/set-item[@id='3']/properties/struct/entry[@name='color']"));
  }

  @Test
  public void testSubQueryWithMultipleColumn()
  {
    final Document doc = assertChangeset("test-snapshot-multi-column", "", "snapshot",
      Arrays.asList("1", "2", "3"),
      Arrays.asList("4", "5"),
      false);
    Assert.assertEquals("red",
      doc.selectSingleNode(
      "/changeset/set-item[@id='1']/properties/struct/entry[@name='name']/string/text()")
      .getText());
    Assert.assertEquals("10",
      doc.selectSingleNode(
      "/changeset/set-item[@id='1']/properties/struct/entry[@name='renamed']/string/text()")
      .getText());
    Assert.assertEquals("red",
      doc.selectSingleNode(
      "/changeset/set-item[@id='1']/properties/struct/entry[@name='color']/struct/entry[@name='name']/string/text()")
      .getText());
    Assert.assertEquals("10",
      doc.selectSingleNode(
      "/changeset/set-item[@id='1']/properties/struct/entry[@name='color']/struct/entry[@name='renamed']/string/text()")
      .getText());
    Assert.assertEquals("red",
      doc.selectSingleNode(
        "/changeset/set-item[@id='1']/properties/struct/entry[@name='color_delimited_name']/string/text()")
        .getText());
    Assert.assertEquals("10",
      doc.selectSingleNode(
        "/changeset/set-item[@id='1']/properties/struct/entry[@name='color_delimited_renamed']/string/text()")
        .getText());

    Assert.assertEquals(2,
      doc.selectNodes("/changeset/set-item[@id='2']/properties/struct/entry[@name='name']/array/element").size());
    Assert.assertEquals(2,
      doc.selectNodes("/changeset/set-item[@id='2']/properties/struct/entry[@name='renamed']/array/element").size());

    Assert.assertEquals("orange",
      doc.selectSingleNode(
        "/changeset/set-item[@id='2']/properties/struct/entry[@name='name']/array/element[1]/string/text()")
        .getText());
    Assert.assertEquals("yellow",
      doc.selectSingleNode(
        "/changeset/set-item[@id='2']/properties/struct/entry[@name='name']/array/element[2]/string/text()")
        .getText());

    Assert.assertEquals("20",
      doc.selectSingleNode(
        "/changeset/set-item[@id='2']/properties/struct/entry[@name='renamed']/array/element[1]/string/text()")
        .getText());
    Assert.assertEquals("30",
      doc.selectSingleNode(
        "/changeset/set-item[@id='2']/properties/struct/entry[@name='renamed']/array/element[2]/string/text()")
        .getText());

    Assert.assertEquals(2,
      doc.selectNodes("/changeset/set-item[@id='2']/properties/struct/entry[@name='color']/array/element").size());

    Assert.assertEquals("orange",
      doc.selectSingleNode(
      "/changeset/set-item[@id='2']/properties/struct/entry[@name='color']/array/element[1]/struct/entry[@name='name']/string/text()")
      .getText());
    Assert.assertEquals("yellow",
      doc.selectSingleNode(
      "/changeset/set-item[@id='2']/properties/struct/entry[@name='color']/array/element[2]/struct/entry[@name='name']/string/text()")
      .getText());

    Assert.assertEquals("orange,yellow",
      doc.selectSingleNode(
        "/changeset/set-item[@id='2']/properties/struct/entry[@name='color_delimited_name']/string/text()")
        .getText());
    Assert.assertEquals("20,30",
      doc.selectSingleNode(
        "/changeset/set-item[@id='2']/properties/struct/entry[@name='color_delimited_renamed']/string/text()")
        .getText());

    Assert.assertNull(doc.selectSingleNode("/changeset/set-item[@id='3']/properties/struct/entry[@name='color']"));
  }

  @Test
  public void testSubQueryWithMultipleColumnAndDiscriminator()
  {
    final Document doc = assertChangeset("test-snapshot-multi-column-discriminator", "", "snapshot",
      Arrays.asList("1", "2", "3"),
      Arrays.asList("4", "5"),
      false);

    Assert.assertEquals("1 main st",
      doc.selectSingleNode(
        "/changeset/set-item[@id='1']/properties/struct/entry[@name='address']/struct/entry[@name='biz']/struct/entry[@name='street']/string/text()")
      .getText());
    Assert.assertNull(
      doc.selectSingleNode(
        "/changeset/set-item[@id='1']/properties/struct/entry[@name='address']/struct/entry[@name='biz']/struct/entry[@name='discriminator']"));

    Assert.assertEquals(2,
      doc.selectNodes("/changeset/set-item[@id='2']/properties/struct/entry[@name='address']/struct/entry").size());

    Assert.assertEquals(
      "123 main st",
      doc.selectSingleNode(
        "/changeset/set-item[@id='2']/properties/struct/entry[@name='address']/struct/entry[@name='biz']/struct/entry[@name='street']/string/text()")
      .getText());
    Assert.assertNull(
      doc.selectSingleNode(
        "/changeset/set-item[@id='2']/properties/struct/entry[@name='address']/struct/entry[@name='biz']/struct/entry[@name='discriminator']"));

    Assert.assertEquals(
      "456 main st",
      doc.selectSingleNode(
            "/changeset/set-item[@id='2']/properties/struct/entry[@name='address']/struct/entry[@name='per']/struct/entry[@name='street']/string/text()")
      .getText());

    Assert.assertNull(doc.selectSingleNode("/changeset/set-item[@id='3']/properties/struct/entry[@name='address']"));
  }

  @Test
  public void testEmptySubQuery()
  {
    assertChangeset("test-snapshot-empty-subquery", "", "snapshot",
      Arrays.asList("1", "2", "3"),
      Arrays.asList("4", "5"),
      false);
  }
}
