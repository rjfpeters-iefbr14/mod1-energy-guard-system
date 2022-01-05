package tld.yggdrasill.services.gcm.core.utils;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import tld.yggdrasill.services.gcm.core.utils.exceptions.BasicPredicateBuilderException;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class BasicPredicateBuilder {
  private static final List<String> OPERATIONS = Arrays.asList("<", ">", ":", "!");

  List<Triple> criteria;

  public BasicPredicateBuilder() {
    criteria = new ArrayList<Triple>();
  }

  public void from(String search) throws IllegalArgumentException {
    if (search != null) {
      Pattern pattern = Pattern.compile("([\\w.]+?)([:<>])([\\w.\\- ]+?),");
      Matcher matcher = pattern.matcher(search + ",");
      while (matcher.find()) {
        log.debug(String.format("%s, %s, %s",matcher.group(1), matcher.group(2), matcher.group(3)));
        with(matcher.group(1), matcher.group(2), matcher.group(3));
      }
      if (criteria.size() == 0) {
        throw new IllegalArgumentException("Invalid query: " + search);
      }
    }
  }

  public void with(String key, String operation, String value) throws IllegalArgumentException{
    Triple<String,String,String> c = new ImmutableTriple(key, operation, value);
    if (!isValid(c)) {
      throw new IllegalArgumentException("Invalid query: " + c.toString());
    }
    criteria.add(c);
  }

  public List<Triple> getCriteria() {
    return criteria;
  }

  public BooleanExpression build(Class tClass) throws BasicPredicateBuilderException {
    if (criteria.size() == 0) {
      return null;
    }

    List<BooleanExpression> predicates = criteria.stream().map(param -> {
      BasicPredicate predicate = new BasicPredicate(param);
      return predicate.getPredicate(tClass);
    }).filter(Objects::nonNull).collect(Collectors.toList());

    BooleanExpression expression = predicates.remove(0);

    for (BooleanExpression item : predicates) {
      expression = expression.and(item);
    }

    return expression;
  }

  Boolean isValid(@NotNull Triple item) {
    return item.getLeft() != null && OPERATIONS.contains(item.getMiddle()) && item.getRight() != null;
  }
}
