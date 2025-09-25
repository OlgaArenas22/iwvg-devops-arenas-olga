package es.upm.miw.devops.code;

import java.util.Objects;

public class Searches {

    public Fraction findFractionAdditionByUserId(String id) {
        return new UsersDatabase().findAll()
                .filter(user -> id.equals(user.getId()))
                .flatMap(user -> user.getFractions().stream())
                .filter(Objects::nonNull)
                .reduce(Fraction::add)
                .orElse(null);
    }
}
