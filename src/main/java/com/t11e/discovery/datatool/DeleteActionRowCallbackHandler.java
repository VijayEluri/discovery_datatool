package com.t11e.discovery.datatool;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.stream.XMLStreamException;

import org.springframework.jdbc.core.RowCallbackHandler;

public class DeleteActionRowCallbackHandler
  implements RowCallbackHandler
{
  private final ChangesetWriter writer;
  private final String idColumn;

  public DeleteActionRowCallbackHandler(
    final ChangesetWriter writer,
    final String idColumn)
  {
    this.writer = writer;
    this.idColumn = idColumn;
  }

  public void processRow(final ResultSet rs)
    throws SQLException
  {
    final String id = rs.getString(idColumn).trim();
    try
    {
      writer.removeItem(id);
    }
    catch (final XMLStreamException e)
    {
      throw new RuntimeException(e);
    }
  }
}