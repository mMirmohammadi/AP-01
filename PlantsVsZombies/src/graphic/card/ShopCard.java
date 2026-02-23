package graphic.card;

import account.Account;
import account.AccountForm;
import account.ShopPage;
import client.Client;
import creature.being.BeingDna;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.Program;
import page.QuestionForm;
import util.Effect;

public class ShopCard extends SimpleCard {

    public final BeingDna dna;
    private int state;
    private ShopPage shopPage;


    public ShopCard(BeingDna dna, double size, int state, ShopPage shopPage) {
        super(dna, 0, 0, size, "");
        setColor(Color.TRANSPARENT);
        this.tName.setFill(Color.WHITESMOKE);
        this.description.setFont(Font.font(Program.screenX / 120));
        this.dna = dna;
        this.state = state;
        this.shopPage = shopPage;
        setState(state);
        this.setOnMouseClicked(e -> mouseClick());
    }

    public int getState() {
        return state;
    }

    // 0 means sold out
    // 1 means not enough money
    // 2 means buy able
    public void setState(int newState) {
        state = newState;
        if (state == 0) {
            setDescription("SOLD OUT!/"+dna.shopCount, Color.YELLOW);

        } else if (state == 1) {
            setDescription(dna.getShopPrice() + "$/"+dna.shopCount, Color.RED);
        } else {
            setDescription(dna.getShopPrice() + "$/"+dna.shopCount, Color.LIGHTGREEN);
        }
    }

    private void mouseClick() {
        if (state == 0) {
            new QuestionForm("sell", "give to another person")
            .action()
            .flatMap(x -> {
                if (x == 0) {
                    return Client.get("shop/sell", Account.myToken, dna.getName());
                }
                return new AccountForm()
                    .action()
                    .flatMap(username -> Client.get(
                        "shop/transfer",
                        Account.myToken,
                        dna.getName(),
                        username
                    ));
            })
            .then(Program.reloadAll())
            .then(Effect.syncWork(shopPage::update))
            .then(Effect.syncWork(()->{
                Program.stage.getScene().setRoot(shopPage.root);
            }))
            .execute();
        }
        else {
            Client.get("shop/buy", Account.myToken, dna.getName())
            .then(Program.reloadAll())
            .then(Effect.syncWork(shopPage::update))
            .execute();
        }
    }

    public void update() {
        this.dna.shopCount = BeingDna.getByName(dna.getName()).shopCount;
        int money = Account.getCurrentAccount().getStore().money;
        if (Account.getCurrentAccount().getStore().haveCard(dna))
            setState(0);
        else if (money >= dna.getShopPrice())
            setState(2);
        else if (money < dna.getShopPrice())
            setState(1);
    }

}
