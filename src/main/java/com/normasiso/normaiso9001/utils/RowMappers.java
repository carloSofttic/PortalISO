// utils/RowMappers.java
package com.normasiso.normaiso9001.utils;

import org.springframework.jdbc.core.RowMapper;

public class RowMappers {

  public static final RowMapper<Long> LONG_MAPPER =
      (rs, i) -> rs.getLong(1);

  public static final RowMapper<String> STRING_MAPPER =
      (rs, i) -> rs.getString(1);
}
