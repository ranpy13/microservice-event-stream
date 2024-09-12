package demo.account.action;

import demo.account.domain.Account;
import demo.account.domain.AccountModule;
import demo.account.domain.AccountService;
import demo.account.domain.AccountStatus;
import demo.account.event.AccountEvent;
import demo.account.event.AccountEventType;
import demo.domain.Action;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.function.Function;

import static demo.account.domain.AccountStatus.*;

/**
 * Activates an {@link Account}
 *
 * @author Kenny Bastani
 */
@Service
public class ActivateAccount extends Action<Account> {
    private final Logger log = Logger.getLogger(this.getClass());

    public Function<Account, Account> getFunction() {
        return (account) -> {
            Assert.isTrue(account.getStatus() != ACCOUNT_ACTIVE, "The account is already active");
            Assert.isTrue(Arrays.asList(ACCOUNT_CONFIRMED, ACCOUNT_SUSPENDED, ACCOUNT_ARCHIVED)
                    .contains(account.getStatus()), "The account cannot be activated");

            AccountService accountService = account.getModule(AccountModule.class)
                    .getDefaultService();

            Account result;

            AccountStatus status = account.getStatus();

            // Activate the account
            account.setStatus(AccountStatus.ACCOUNT_ACTIVE);
            account = accountService.update(account);

            try {
                // Trigger the account activated event
                result = account.sendEvent(new AccountEvent(AccountEventType.ACCOUNT_ACTIVATED, account)).getEntity();
                result.setIdentity(account.getIdentity());
            } catch (Exception ex) {
                log.error("Account could not be activated", ex);

                // Rollback the operation
                account.setStatus(status);
                accountService.update(account);

                throw ex;
            }

            return result;
        };
    }
}
