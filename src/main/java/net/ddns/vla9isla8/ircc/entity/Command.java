package net.ddns.vla9isla8.ircc.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Vladk on 26.11.2016.
 */
@JsonIgnoreProperties
public class Command {

    private final String name;
    private final String value;

    @JsonCreator
    public Command(@JsonProperty("name") String name,@JsonProperty("value") String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString(){
        return getName();
    }
}
