package com.jvprz.spectrobesreforged.common.feature.excavation;

import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Shared manifest for registrations, geology, client models and generation checks. */
public final class ExcavationDeposits {
    public record Deposit(String id,String host,String element,String grade,int terrain,int rock) {
        public boolean fossil(){return !element.isEmpty();}
    }
    private static final List<Deposit> ALL=load();
    private ExcavationDeposits(){}
    public static List<Deposit> all(){return ALL;}
    public static Optional<Deposit> find(String id){return ALL.stream().filter(d->d.id().equals(id)).findFirst();}
    private static List<Deposit> load(){
        var entries=new ArrayList<Deposit>();
        try(var stream=ExcavationDeposits.class.getResourceAsStream("/data/spectrobesreforged/excavation_deposits.json")){
            if(stream==null)throw new IllegalStateException("Missing excavation deposits");
            var json=JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonArray();
            for(var entry:json){var d=entry.getAsJsonObject();entries.add(new Deposit(d.get("id").getAsString(),d.get("host").getAsString(),d.get("element").getAsString(),d.get("grade").getAsString(),d.get("terrain").getAsInt(),d.get("rock").getAsInt()));}
        }catch(Exception exception){throw new IllegalStateException("Cannot load excavation deposits",exception);}
        return List.copyOf(entries);
    }
}
