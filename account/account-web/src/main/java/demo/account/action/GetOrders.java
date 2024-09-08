package demo.account.action;

import demo.account.domain.Account;
import demo.domain.Action;
import demo.order.domain.OrderModule;
import demo.order.domain.Orders;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class GetOrders extends Action<Account> {

    private OrderModule orderProvider;

    public GetOrders(OrderModule orderProvider) {
        this.orderProvider = orderProvider;
    }

    public Function<Account, Orders> getFunction() {
        return (account) -> {
            // Get orders from the order service
            return orderProvider.getDefaultService()
                    .findOrdersByAccountId(account.getIdentity());
        };
    }
}