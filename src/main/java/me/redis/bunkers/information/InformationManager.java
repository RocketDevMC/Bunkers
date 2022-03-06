package me.redis.bunkers.information;

import lombok.Getter;
import lombok.Setter;

public class InformationManager {
    @Getter @Setter public Information information;

    public InformationManager() {
        setInformation(new Information());
    }
}
