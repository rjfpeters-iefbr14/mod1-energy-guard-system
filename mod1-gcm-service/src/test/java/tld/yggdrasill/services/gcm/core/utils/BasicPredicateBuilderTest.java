package tld.yggdrasill.services.gcm.core.utils;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import tld.yggdrasill.services.gcm.core.model.Contingency;
import tld.yggdrasill.services.gcm.core.utils.exceptions.BasicPredicateBuilderException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasicPredicateBuilderTest {

  @Test
  void successful_from_expression_single_numeric_criteria() {
    String query = "fasggq:33"; // Valid format, nonexistent property, should return empty list

    BasicPredicateBuilder builder = new BasicPredicateBuilder();
    builder.from(query);
    assertThat(builder.getCriteria()).isNotEmpty().hasSize(1);
  }

  @Test
  void successful_from_expression_with_null_criteria() {
    BasicPredicateBuilder builder = new BasicPredicateBuilder();
    builder.from(null);
    assertThat(builder.getCriteria()).isEmpty();
  }

  @Test
  void failed_from_expression_illegal_criteria() {
    BasicPredicateBuilder builder = new BasicPredicateBuilder();
    assertThatThrownBy(() -> {
      builder.with("fasggq","::","33");
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void successful_from_expression_single_string_criteria() {
    String query = "fasggq:alfa"; // Valid format, nonexistent property, should return empty list

    BasicPredicateBuilder builder = new BasicPredicateBuilder();
    builder.from(query);
    assertThat(builder.getCriteria()).isNotEmpty().hasSize(1);
  }

  @Test
  void successful_from_expression_mulitple_criteria() {
    String query = "fasggq:33,name:test,build:1-01-2021"; // Valid format, nonexistent property, should return empty list

    BasicPredicateBuilder builder = new BasicPredicateBuilder();
    builder.from(query);
    assertThat(builder.getCriteria()).isNotEmpty().hasSize(3);
  }

  @Test
  void fail_from_expression() {
    String query = "6&3s>,ga3"; // Invalid format, should throw exception

    BasicPredicateBuilder builder = new BasicPredicateBuilder();
    assertThatThrownBy(() -> {
      builder.from(query);
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void successful_build() {
    String query = "mRID:6b0a74dd-5781-4fbb-b576-87c55bf56104"; // Valid format, nonexistent property, should return empty list

    BasicPredicateBuilder builder = new BasicPredicateBuilder();
    builder.from(query);

    BooleanExpression expression = builder.build(Contingency.class);

    assertThat(expression.toString()).isEqualTo("Contingency.mRID = 6b0a74dd-5781-4fbb-b576-87c55bf56104");
  }

  @Test
  void fail_build_invalid_field() {
    String query = "fasggq:33"; // Valid format, nonexistent property, should return empty list

    BasicPredicateBuilder builder = new BasicPredicateBuilder();
    builder.from(query);

    assertThatThrownBy(() -> {
      BooleanExpression expression = builder.build(Contingency.class);
    }).isInstanceOf(BasicPredicateBuilderException.class);
  }

  @Test
  void successful_build_expression_with_null_criteria() {
    BasicPredicateBuilder builder = new BasicPredicateBuilder();
    builder.from(null);
    BooleanExpression expression = builder.build(Contingency.class);
    assertThat(expression).isNull();
  }

  @Test
  void successful_build_with_multiple_items() {
    String query = "mRID:6b0a74dd-5781-4fbb-b576-87c55bf56104";

    BasicPredicateBuilder builder = new BasicPredicateBuilder();
    builder.from(query);
    BooleanExpression expression = builder.build(Contingency.class);
    assertThat(expression).isNotNull();
  }
}
