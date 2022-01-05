package tld.yggdrasill.services.agm.core.model;

public enum ContingencyCaseType {

  CONTINGENCY_MITIGATION("contingency-mitigation");

  private final String type;

  public String getType() {
    return type;
  }

  ContingencyCaseType(String type) {
    this.type = type;
  }
}
