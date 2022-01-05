package tld.yggdrasill.services.gcm.core.utils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import org.apache.commons.lang3.tuple.Triple;
import tld.yggdrasill.services.gcm.core.utils.exceptions.BasicPredicateBuilderException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

class BasicPredicate<T> {

  private Triple<String, String, String> criteria;

  BasicPredicate(Triple criteria) {
    this.criteria = criteria;
  }

  public BooleanExpression getPredicate(Class<T> entityClass) throws BasicPredicateBuilderException {

    final Class<?> typeByPath = getLastTypeByPath(entityClass, criteria.getLeft());
    final PathBuilder<T> entityPath = new PathBuilder<>(entityClass, entityClass.getSimpleName());

    if (typeByPath.equals(String.class)) {
      final StringPath path = entityPath.getString(criteria.getLeft());
      switch (criteria.getMiddle()) {
        case ":":
          if (criteria.getRight().toString().equalsIgnoreCase("null")) {
            return path.isNull();
          } else {
            return path.equalsIgnoreCase(criteria.getRight().toString());
          }
        case ";":
          return path.containsIgnoreCase(criteria.getRight().toString());
        case "!":
          return path.notEqualsIgnoreCase(criteria.getRight().toString());
        default:
          throw new BasicPredicateBuilderException(criteria.getLeft() + "'s search operator", criteria.getMiddle(), "Valid Search Operator");
      }
    } else if (typeByPath.equals(long.class) || typeByPath.equals(Long.class)) {
      if (!isLong(criteria.getRight().toString())) {
        throw new BasicPredicateBuilderException(criteria.getLeft(), criteria.getRight().toString(), "Valid long value");
      } else {
        final NumberPath<Long> path = entityPath.getNumber(criteria.getLeft(), Long.class);
        final long value = Long.parseLong(criteria.getRight().toString());
        switch (criteria.getMiddle()) {
          case ":":
            return path.eq(value);
          case "!":
            return path.ne(value);
          case ">":
            return path.goe(value);
          case "<":
            return path.loe(value);
          default:
            throw new BasicPredicateBuilderException(criteria.getLeft() + "'s search operator", criteria.getMiddle(), "Valid Search Operator");
        }
      }
    } else if (typeByPath.equals(LocalDateTime.class)) {
      if (!isLocalDateTime(criteria.getRight().toString())) {
        throw new BasicPredicateBuilderException(criteria.getLeft(), criteria.getRight().toString(), "Valid LocalDateTime value");
      } else {
        final DateTimePath<LocalDateTime> path = entityPath.getDateTime(criteria.getLeft(), LocalDateTime.class);
        final LocalDateTime value = LocalDateTime.parse(criteria.getRight().toString());
        switch (criteria.getMiddle()) {
          case ":":
            return path.eq(value);
          case "!":
            return path.ne(value);
          case ">":
            return path.after(value);
          case "<":
            return path.before(value);
          default:
            throw new BasicPredicateBuilderException(criteria.getLeft() + "'s search operator", criteria.getMiddle(), "Valid Search Operator");
        }
      }
    } else if (typeByPath.equals(UUID.class)) {
      if (!isUUID(criteria.getRight().toString())) {
        throw new BasicPredicateBuilderException(criteria.getLeft(), criteria.getRight().toString(), "Valid UUID");
      } else {
        final ComparablePath<UUID> path = entityPath.getComparable(criteria.getLeft(), UUID.class);
        switch (criteria.getMiddle()) {
          case ":":
            return path.eq(UUID.fromString(criteria.getRight().toString()));
          case "!":
            return path.ne(UUID.fromString(criteria.getRight().toString()));
          default:
            throw new BasicPredicateBuilderException(criteria.getLeft() + "'s search operator", criteria.getMiddle(), "Valid Search Operator");
        }
      }
    } else if (typeByPath.isEnum()) {
      final EnumPath path = entityPath.getEnum(criteria.getLeft(), (Class) typeByPath);
      switch (criteria.getMiddle()) {
        case ":":
          return path.eq(Enum.valueOf((Class) typeByPath, (String)criteria.getRight()));
        case "!":
          return path.ne(Enum.valueOf((Class) typeByPath, (String)criteria.getRight()));
        default:
          throw new BasicPredicateBuilderException(criteria.getLeft() + "'s search operator", criteria.getMiddle(), "Valid Search Operator");
      }
    } else if (typeByPath.equals(boolean.class) || typeByPath.equals(Boolean.class)) {
      final BooleanPath path = entityPath.getBoolean(criteria.getLeft());
      switch (criteria.getMiddle()) {
        case ":":
          return path.eq(Boolean.valueOf(criteria.getRight().toString()));
        case "!":
          return path.ne(Boolean.valueOf(criteria.getRight().toString()));
        default:
          throw new BasicPredicateBuilderException(criteria.getLeft() + "'s search operator", criteria.getMiddle(), "Valid Search Operator");
      }
    }  else if (typeByPath.getPackage().getName().equals("tld.yggdrasill.core.model")) {
      // the search is made on a entity
      switch (criteria.getMiddle()) {
        case ":":
          if (criteria.getRight().toString().equalsIgnoreCase("null")) {
            return entityPath.get(criteria.getLeft(), typeByPath).isNull();
          }
        default:
          // TODO: enhance this error
          throw new BasicPredicateBuilderException(criteria.getLeft() + "'s search operator", criteria.getMiddle(), "Valid Search Operator");
      }
    }

    throw new BasicPredicateBuilderException("search field", criteria.getLeft(), "Valid Search Field");
  }

  private Class getLastTypeByPath(Class<T> entityClass, String path) throws BasicPredicateBuilderException {
    String[] types = path.split("\\.");
    Class returnType = entityClass;

    for (String type : types) {
      try {
        returnType = returnType.getDeclaredField(type).getType();
      } catch (NoSuchFieldException e) {
        throw new BasicPredicateBuilderException("search field", criteria.getLeft(), "Valid Search Field");
      }
    }
    return returnType;
  }

  private static boolean isLong(final String str) {
    try {
      Long.parseLong(str);
    } catch (final NumberFormatException e) {
      return false;
    }
    return true;
  }

  private static boolean isLocalDateTime(final String str) {
    try {
      LocalDateTime.parse(str);
    } catch (final DateTimeParseException e) {
      return false;
    }
    return true;
  }

  private static boolean isUUID(final String str) {
    try {
      UUID.fromString(str);
    } catch (final Exception e) {
      return false;
    }
    return true;
  }
}
