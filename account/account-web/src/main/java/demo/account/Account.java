package demo.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import demo.account.action.ActivateAccount;
import demo.account.action.ArchiveAccount;
import demo.account.action.ConfirmAccount;
import demo.account.action.SuspendAccount;
import demo.account.controller.AccountController;
import demo.domain.AbstractEntity;
import demo.domain.Command;
import demo.event.AccountEvent;
import org.springframework.hateoas.Link;

import javax.persistence.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Entity
public class Account extends AbstractEntity<AccountEvent, Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
    private String email;

    @Enumerated(value = EnumType.STRING)
    private AccountStatus status;

    public Account() {
        status = AccountStatus.ACCOUNT_CREATED;
    }

    public Account(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @JsonProperty("accountId")
    @Override
    public Long getIdentity() {
        return this.id;
    }

    @Override
    public void setIdentity(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    @Command(method = "activate", controller = AccountController.class)
    public Account activate() {
        getAction(ActivateAccount.class)
                .getConsumer()
                .accept(this);
        return this;
    }

    @Command(method = "archive", controller = AccountController.class)
    public Account archive() {
        getAction(ArchiveAccount.class)
                .getConsumer()
                .accept(this);
        return this;
    }

    @Command(method = "confirm", controller = AccountController.class)
    public Account confirm() {
        getAction(ConfirmAccount.class)
                .getConsumer()
                .accept(this);
        return this;
    }

    @Command(method = "suspend", controller = AccountController.class)
    public Account suspend() {
        getAction(SuspendAccount.class)
                .getConsumer()
                .accept(this);
        return this;
    }

    /**
     * Returns the {@link Link} with a rel of {@link Link#REL_SELF}.
     */
    @Override
    public Link getId() {
        return linkTo(AccountController.class)
                .slash("accounts")
                .slash(getIdentity())
                .withSelfRel();
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                "} " + super.toString();
    }
}
