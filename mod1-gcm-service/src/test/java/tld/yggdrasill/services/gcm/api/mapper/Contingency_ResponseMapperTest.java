package tld.yggdrasill.services.gcm.api.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tld.yggdrasill.services.gcm.api.model.ContingencyRequest;
import tld.yggdrasill.services.gcm.api.model.ContingencyResponse;
import tld.yggdrasill.services.gcm.core.model.Contingency;
import tld.yggdrasill.services.gcm.helper.ContingencyHelper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
public class Contingency_ResponseMapperTest {

  private ModelMapper modelMapper = new ModelMapper();

  @BeforeEach
  void init() {
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    modelMapper.typeMap(Contingency.class, ContingencyResponse.class);
  }

  @Test
  public void should_successfully_convert_contingency() {
    Contingency oneContingency = ContingencyHelper.getOneContingency();

    ContingencyResponse response = modelMapper.map(oneContingency, ContingencyResponse.class);
    assertThat(response).isNotNull();
  }
}
