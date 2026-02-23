package account;

import page.Form;
import page.Page;
import util.Effect;

public class AccountForm implements Page<String> {

  @Override
  public Effect<String> action() {
    return new Form("Enter Username").action().map(x->x[0]);
  }

}